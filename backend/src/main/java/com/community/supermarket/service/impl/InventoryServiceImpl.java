package com.community.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.supermarket.common.BusinessException;
import com.community.supermarket.dto.InventoryUpdateRequest;
import com.community.supermarket.entity.Inventory;
import com.community.supermarket.mapper.InventoryMapper;
import com.community.supermarket.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;

    @Override
    public IPage<Inventory> listInventory(int page, int size) {
        return inventoryMapper.selectPage(new Page<>(page, size), null);
    }

    @Override
    public Inventory getByProductId(Long productId) {
        return inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>().eq(Inventory::getProductId, productId));
    }

    @Override
    public Inventory updateInventory(Long id, InventoryUpdateRequest request) {
        Inventory inventory = inventoryMapper.selectById(id);
        if (inventory == null) {
            throw new BusinessException("库存记录不存在");
        }
        if (request.getStockQuantity() != null) {
            inventory.setStockQuantity(request.getStockQuantity());
        }
        if (request.getSafetyStock() != null) {
            inventory.setSafetyStock(request.getSafetyStock());
        }
        inventoryMapper.updateById(inventory);
        return inventory;
    }

    @Override
    @Transactional
    public void restock(Long productId, int quantity) {
        int rows = inventoryMapper.addStock(productId, quantity);
        if (rows == 0) {
            throw new BusinessException("补货失败，商品库存记录不存在");
        }
    }

    @Override
    @Transactional
    public boolean deductStock(Long productId, int quantity) {
        return inventoryMapper.deductStock(productId, quantity) > 0;
    }
}
