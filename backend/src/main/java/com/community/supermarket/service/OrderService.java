package com.community.supermarket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.supermarket.dto.OrderCreateRequest;
import com.community.supermarket.vo.OrderVO;

public interface OrderService {
    OrderVO createOrder(Long userId, OrderCreateRequest request);
    IPage<OrderVO> listUserOrders(Long userId, String status, int page, int size);
    IPage<OrderVO> listAllOrders(String status, int page, int size);
    OrderVO getOrderDetail(Long orderId);
    void cancelOrder(Long orderId, Long userId);
    void updateOrderStatus(Long orderId, String status);
}
