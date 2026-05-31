package com.community.supermarket.controller;

import com.community.supermarket.common.PageResult;
import com.community.supermarket.common.Result;
import com.community.supermarket.entity.Delivery;
import com.community.supermarket.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "配送模块")
@RestController
@RequestMapping("/admin/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(summary = "配送列表")
    @GetMapping("/list")
    public Result<PageResult<Delivery>> list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(PageResult.of(deliveryService.listDeliveries(status, page, size)));
    }

    @Operation(summary = "更新配送状态")
    @PutMapping("/{id}/status")
    public Result<Delivery> updateStatus(@PathVariable Long id,
                                          @RequestParam String status) {
        return Result.success(deliveryService.updateDeliveryStatus(id, status));
    }
}
