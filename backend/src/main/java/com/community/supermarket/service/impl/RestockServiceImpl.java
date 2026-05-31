package com.community.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.supermarket.common.BusinessException;
import com.community.supermarket.entity.Inventory;
import com.community.supermarket.entity.Product;
import com.community.supermarket.entity.RestockAlert;
import com.community.supermarket.entity.SeasonalRule;
import com.community.supermarket.mapper.*;
import com.community.supermarket.service.RestockService;
import com.community.supermarket.util.SeasonUtil;
import com.community.supermarket.vo.RestockAlertVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestockServiceImpl implements RestockService {

    private final InventoryMapper inventoryMapper;
    private final ProductMapper productMapper;
    private final RestockAlertMapper restockAlertMapper;
    private final SeasonalRuleMapper seasonalRuleMapper;
    private final SalesStatsMapper salesStatsMapper;

    @Override
    public void checkAndGenerateAlerts() {
        List<Inventory> inventories = inventoryMapper.selectList(null);
        String currentSeason = SeasonUtil.getCurrentSeason();

        for (Inventory inv : inventories) {
            int effectiveSafetyStock = getEffectiveSafetyStock(inv, currentSeason);

            if (inv.getStockQuantity() <= effectiveSafetyStock) {
                boolean existsPending = restockAlertMapper.selectCount(
                        new LambdaQueryWrapper<RestockAlert>()
                                .eq(RestockAlert::getProductId, inv.getProductId())
                                .eq(RestockAlert::getStatus, "PENDING")) > 0;
                if (existsPending) {
                    continue;
                }

                int suggestedQty = calculateSuggestedQuantity(inv.getProductId(), effectiveSafetyStock);

                RestockAlert alert = new RestockAlert();
                alert.setProductId(inv.getProductId());
                alert.setCurrentStock(inv.getStockQuantity());
                alert.setSafetyStock(effectiveSafetyStock);
                alert.setSuggestedQuantity(suggestedQty);
                alert.setStatus("PENDING");
                restockAlertMapper.insert(alert);

                Product product = productMapper.selectById(inv.getProductId());
                log.info("补货提醒: 商品[{}] 当前库存={}, 安全库存={}, 建议补货={}",
                        product != null ? product.getName() : inv.getProductId(),
                        inv.getStockQuantity(), effectiveSafetyStock, suggestedQty);
            }
        }
    }

    @Override
    public IPage<RestockAlertVO> listAlerts(String status, int page, int size) {
        LambdaQueryWrapper<RestockAlert> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            wrapper.eq(RestockAlert::getStatus, status);
        }
        wrapper.orderByDesc(RestockAlert::getAlertTime);
        IPage<RestockAlert> alertPage = restockAlertMapper.selectPage(new Page<>(page, size), wrapper);
        return alertPage.convert(this::toAlertVO);
    }

    @Override
    public void processAlert(Long alertId) {
        RestockAlert alert = restockAlertMapper.selectById(alertId);
        if (alert == null) {
            throw new BusinessException("提醒不存在");
        }
        alert.setStatus("PROCESSED");
        alert.setProcessTime(java.time.LocalDateTime.now());
        restockAlertMapper.updateById(alert);
    }

    @Override
    public void ignoreAlert(Long alertId) {
        RestockAlert alert = restockAlertMapper.selectById(alertId);
        if (alert == null) {
            throw new BusinessException("提醒不存在");
        }
        alert.setStatus("IGNORED");
        alert.setProcessTime(java.time.LocalDateTime.now());
        restockAlertMapper.updateById(alert);
    }

    @Override
    public Map<String, Object> getRestockAnalysis(Long productId) {
        Map<String, Object> analysis = new HashMap<>();

        Product product = productMapper.selectById(productId);
        Inventory inventory = inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>().eq(Inventory::getProductId, productId));

        analysis.put("product", product);
        analysis.put("inventory", inventory);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate7 = endDate.minusDays(7);
        LocalDate startDate30 = endDate.minusDays(30);

        BigDecimal avgDaily7 = salesStatsMapper.getAvgDailySales(productId, startDate7, endDate);
        BigDecimal avgDaily30 = salesStatsMapper.getAvgDailySales(productId, startDate30, endDate);

        analysis.put("avgDailySales7Days", avgDaily7 != null ? avgDaily7 : BigDecimal.ZERO);
        analysis.put("avgDailySales30Days", avgDaily30 != null ? avgDaily30 : BigDecimal.ZERO);

        String currentSeason = SeasonUtil.getCurrentSeason();
        SeasonalRule rule = seasonalRuleMapper.selectOne(
                new LambdaQueryWrapper<SeasonalRule>()
                        .eq(SeasonalRule::getProductId, productId)
                        .eq(SeasonalRule::getSeason, currentSeason));
        analysis.put("seasonalRule", rule);
        analysis.put("currentSeason", currentSeason);

        if (inventory != null && avgDaily30 != null && avgDaily30.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal daysRemaining = BigDecimal.valueOf(inventory.getStockQuantity())
                    .divide(avgDaily30, 1, RoundingMode.HALF_UP);
            analysis.put("estimatedDaysRemaining", daysRemaining);
        }

        return analysis;
    }

    private int getEffectiveSafetyStock(Inventory inv, String currentSeason) {
        SeasonalRule rule = seasonalRuleMapper.selectOne(
                new LambdaQueryWrapper<SeasonalRule>()
                        .eq(SeasonalRule::getProductId, inv.getProductId())
                        .eq(SeasonalRule::getSeason, currentSeason));

        if (rule != null && rule.getAutoAdjustSafetyStock() == 1) {
            return BigDecimal.valueOf(inv.getSafetyStock())
                    .multiply(rule.getDemandMultiplier())
                    .intValue();
        }
        return inv.getSafetyStock();
    }

    private int calculateSuggestedQuantity(Long productId, int safetyStock) {
        BigDecimal avgDaily = salesStatsMapper.getAvgDailySales(
                productId, LocalDate.now().minusDays(30), LocalDate.now());
        if (avgDaily == null || avgDaily.compareTo(BigDecimal.ZERO) == 0) {
            return safetyStock * 2;
        }
        return avgDaily.multiply(BigDecimal.valueOf(7))
                .add(BigDecimal.valueOf(safetyStock))
                .intValue();
    }

    private RestockAlertVO toAlertVO(RestockAlert alert) {
        RestockAlertVO vo = new RestockAlertVO();
        vo.setId(alert.getId());
        vo.setProductId(alert.getProductId());
        vo.setCurrentStock(alert.getCurrentStock());
        vo.setSafetyStock(alert.getSafetyStock());
        vo.setSuggestedQuantity(alert.getSuggestedQuantity());
        vo.setStatus(alert.getStatus());
        vo.setAlertTime(alert.getAlertTime());

        Product product = productMapper.selectById(alert.getProductId());
        if (product != null) {
            vo.setProductName(product.getName());
        }
        return vo;
    }
}
