package com.community.supermarket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.supermarket.entity.Delivery;

public interface DeliveryService {
    Delivery createDelivery(Long orderId);
    IPage<Delivery> listDeliveries(String status, int page, int size);
    Delivery updateDeliveryStatus(Long deliveryId, String status);
    Delivery getByOrderId(Long orderId);
}
