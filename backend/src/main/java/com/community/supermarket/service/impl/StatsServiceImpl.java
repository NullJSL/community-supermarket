package com.community.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.supermarket.entity.*;
import com.community.supermarket.mapper.*;
import com.community.supermarket.service.StatsService;
import com.community.supermarket.util.SeasonUtil;
import com.community.supermarket.vo.SalesReportVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final SalesStatsMapper salesStatsMapper;
    private final InventoryMapper inventoryMapper;
    private final SeasonalRuleMapper seasonalRuleMapper;

    @Override
    public void generateDailyStats(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        List<Order> completedOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .in(Order::getStatus, "PAID", "DELIVERING", "COMPLETED")
                        .ge(Order::getCreateTime, startOfDay)
                        .lt(Order::getCreateTime, endOfDay));

        for (Order order : completedOrders) {
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));

            for (OrderItem item : items) {
                SalesStats existing = salesStatsMapper.selectOne(
                        new LambdaQueryWrapper<SalesStats>()
                                .eq(SalesStats::getProductId, item.getProductId())
                                .eq(SalesStats::getStatDate, date));

                if (existing != null) {
                    existing.setQuantitySold(existing.getQuantitySold() + item.getQuantity());
                    existing.setRevenue(existing.getRevenue().add(item.getSubtotal()));
                    salesStatsMapper.updateById(existing);
                } else {
                    SalesStats stats = new SalesStats();
                    stats.setProductId(item.getProductId());
                    stats.setStatDate(date);
                    stats.setQuantitySold(item.getQuantity());
                    stats.setRevenue(item.getSubtotal());
                    salesStatsMapper.insert(stats);
                }
            }
        }
        log.info("日销售统计完成: date={}, orders={}", date, completedOrders.size());
    }

    @Override
    public SalesReportVO getSalesReport(LocalDate startDate, LocalDate endDate) {
        SalesReportVO report = new SalesReportVO();

        List<SalesStats> allStats = salesStatsMapper.selectList(
                new LambdaQueryWrapper<SalesStats>()
                        .ge(SalesStats::getStatDate, startDate)
                        .le(SalesStats::getStatDate, endDate));

        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalQuantity = 0;
        for (SalesStats stat : allStats) {
            totalRevenue = totalRevenue.add(stat.getRevenue());
            totalQuantity += stat.getQuantitySold();
        }
        report.setTotalRevenue(totalRevenue);
        report.setTotalQuantitySold(totalQuantity);

        long orderCount = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .ge(Order::getCreateTime, startDate.atStartOfDay())
                        .lt(Order::getCreateTime, endDate.plusDays(1).atStartOfDay())
                        .ne(Order::getStatus, "CANCELLED"));
        report.setTotalOrders((int) orderCount);

        List<Map<String, Object>> topProducts = salesStatsMapper.getTopSellingProducts(startDate, endDate, 10);
        report.setTopProducts(topProducts);

        List<SalesReportVO.DailySales> dailySalesList = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDate currentDate = date;
            SalesReportVO.DailySales daily = new SalesReportVO.DailySales();
            daily.setDate(currentDate.toString());
            BigDecimal dayRevenue = BigDecimal.ZERO;
            int dayQuantity = 0;
            for (SalesStats stat : allStats) {
                if (stat.getStatDate().equals(currentDate)) {
                    dayRevenue = dayRevenue.add(stat.getRevenue());
                    dayQuantity += stat.getQuantitySold();
                }
            }
            daily.setRevenue(dayRevenue);
            daily.setQuantity(dayQuantity);
            dailySalesList.add(daily);
        }
        report.setDailySales(dailySalesList);

        return report;
    }

    @Override
    public void applySeasonalAdjustments() {
        String currentSeason = SeasonUtil.getCurrentSeason();
        List<SeasonalRule> rules = seasonalRuleMapper.selectList(
                new LambdaQueryWrapper<SeasonalRule>()
                        .eq(SeasonalRule::getSeason, currentSeason)
                        .eq(SeasonalRule::getAutoAdjustSafetyStock, 1));

        for (SeasonalRule rule : rules) {
            Inventory inventory = inventoryMapper.selectOne(
                    new LambdaQueryWrapper<Inventory>()
                            .eq(Inventory::getProductId, rule.getProductId()));
            if (inventory != null) {
                int adjustedStock = BigDecimal.valueOf(inventory.getSafetyStock())
                        .multiply(rule.getDemandMultiplier())
                        .setScale(0, RoundingMode.CEILING)
                        .intValue();
                inventory.setSafetyStock(adjustedStock);
                inventoryMapper.updateById(inventory);
                log.info("季节性调整: 商品ID={}, 季节={}, 安全库存调整为={}",
                        rule.getProductId(), currentSeason, adjustedStock);
            }
        }
    }
}
