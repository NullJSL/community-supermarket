package com.community.supermarket.controller;

import com.community.supermarket.common.PageResult;
import com.community.supermarket.common.Result;
import com.community.supermarket.dto.InventoryUpdateRequest;
import com.community.supermarket.dto.RestockRequest;
import com.community.supermarket.entity.Inventory;
import com.community.supermarket.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "库存模块")
@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "库存列表")
    @GetMapping("/list")
    public Result<PageResult<Inventory>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(PageResult.of(inventoryService.listInventory(page, size)));
    }

    @Operation(summary = "更新库存")
    @PutMapping("/{id}")
    public Result<Inventory> update(@PathVariable Long id,
                                     @Valid @RequestBody InventoryUpdateRequest request) {
        return Result.success(inventoryService.updateInventory(id, request));
    }

    @Operation(summary = "入库操作")
    @PostMapping("/{productId}/restock")
    public Result<Void> restock(@PathVariable Long productId,
                                 @Valid @RequestBody RestockRequest request) {
        inventoryService.restock(productId, request.getQuantity());
        return Result.success();
    }
}
