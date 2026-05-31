package com.community.supermarket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.supermarket.dto.InventoryUpdateRequest;
import com.community.supermarket.entity.Inventory;

public interface InventoryService {
    IPage<Inventory> listInventory(int page, int size);
    Inventory getByProductId(Long productId);
    Inventory updateInventory(Long id, InventoryUpdateRequest request);
    void restock(Long productId, int quantity);
    boolean deductStock(Long productId, int quantity);
}
