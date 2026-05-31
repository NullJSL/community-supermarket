package com.community.supermarket.controller;

import com.community.supermarket.common.PageResult;
import com.community.supermarket.common.Result;
import com.community.supermarket.dto.OrderCreateRequest;
import com.community.supermarket.service.OrderService;
import com.community.supermarket.vo.OrderVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单模块")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping("/order")
    public Result<OrderVO> create(HttpServletRequest request,
                                   @Valid @RequestBody OrderCreateRequest orderRequest) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.createOrder(userId, orderRequest));
    }

    @Operation(summary = "我的订单列表")
    @GetMapping("/order/list")
    public Result<PageResult<OrderVO>> myOrders(
            HttpServletRequest request,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(PageResult.of(orderService.listUserOrders(userId, status, page, size)));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/order/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(id));
    }

    @Operation(summary = "取消订单")
    @PutMapping("/order/{id}/cancel")
    public Result<Void> cancel(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.cancelOrder(id, userId);
        return Result.success();
    }

    @Operation(summary = "所有订单列表（管理员）")
    @GetMapping("/admin/order/list")
    public Result<PageResult<OrderVO>> allOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(PageResult.of(orderService.listAllOrders(status, page, size)));
    }

    @Operation(summary = "更新订单状态（管理员）")
    @PutMapping("/admin/order/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                      @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
        return Result.success();
    }
}
