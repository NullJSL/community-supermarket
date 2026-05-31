package com.community.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.supermarket.common.BusinessException;
import com.community.supermarket.dto.OrderCreateRequest;
import com.community.supermarket.entity.*;
import com.community.supermarket.mapper.OrderItemMapper;
import com.community.supermarket.mapper.OrderMapper;
import com.community.supermarket.mapper.ProductMapper;
import com.community.supermarket.service.DeliveryService;
import com.community.supermarket.service.InventoryService;
import com.community.supermarket.service.OrderService;
import com.community.supermarket.util.OrderNoGenerator;
import com.community.supermarket.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final InventoryService inventoryService;
    private final DeliveryService deliveryService;

    @Override
    @Transactional
    public OrderVO createOrder(Long userId, OrderCreateRequest request) {
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderCreateRequest.OrderItemRequest itemReq : request.getItems()) {
            Product product = productMapper.selectById(itemReq.getProductId());
            if (product == null || !"ON_SALE".equals(product.getStatus())) {
                throw new BusinessException("商品不存在或已下架: " + itemReq.getProductId());
            }

            boolean deducted = inventoryService.deductStock(product.getId(), itemReq.getQuantity());
            if (!deducted) {
                throw new BusinessException("商品库存不足: " + product.getName());
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductPrice(product.getPrice());
            item.setQuantity(itemReq.getQuantity());
            item.setSubtotal(subtotal);
            orderItems.add(item);

            totalAmount = totalAmount.add(subtotal);
        }

        Order order = new Order();
        order.setOrderNo(OrderNoGenerator.generate());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PAID");
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryPhone(request.getDeliveryPhone());
        order.setRemark(request.getRemark());
        orderMapper.insert(order);

        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        deliveryService.createDelivery(order.getId());

        return toOrderVO(order, orderItems);
    }

    @Override
    public IPage<OrderVO> listUserOrders(Long userId, String status, int page, int size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId);
        if (status != null && !status.isBlank()) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        IPage<Order> orderPage = orderMapper.selectPage(new Page<>(page, size), wrapper);
        return orderPage.convert(this::toOrderVOWithItems);
    }

    @Override
    public IPage<OrderVO> listAllOrders(String status, int page, int size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        IPage<Order> orderPage = orderMapper.selectPage(new Page<>(page, size), wrapper);
        return orderPage.convert(this::toOrderVOWithItems);
    }

    @Override
    public OrderVO getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return toOrderVOWithItems(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (!"PENDING".equals(order.getStatus()) && !"PAID".equals(order.getStatus())) {
            throw new BusinessException("当前订单状态不可取消");
        }

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            inventoryService.restock(item.getProductId(), item.getQuantity());
        }

        order.setStatus("CANCELLED");
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        order.setStatus(status);
        orderMapper.updateById(order);
    }

    private OrderVO toOrderVO(Order order, List<OrderItem> items) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setStatus(order.getStatus());
        vo.setDeliveryAddress(order.getDeliveryAddress());
        vo.setDeliveryPhone(order.getDeliveryPhone());
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setItems(items);

        Delivery delivery = deliveryService.getByOrderId(order.getId());
        if (delivery != null) {
            vo.setDeliveryStatus(delivery.getStatus());
        }
        return vo;
    }

    private OrderVO toOrderVOWithItems(Order order) {
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        return toOrderVO(order, items);
    }
}
