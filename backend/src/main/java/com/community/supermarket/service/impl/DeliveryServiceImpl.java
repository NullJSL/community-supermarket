package com.community.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.supermarket.common.BusinessException;
import com.community.supermarket.entity.Delivery;
import com.community.supermarket.mapper.DeliveryMapper;
import com.community.supermarket.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryMapper deliveryMapper;

    @Override
    public Delivery createDelivery(Long orderId) {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setStatus("PENDING");
        delivery.setEstimatedTime(LocalDateTime.now().plusMinutes(30));
        deliveryMapper.insert(delivery);
        return delivery;
    }

    @Override
    public IPage<Delivery> listDeliveries(String status, int page, int size) {
        LambdaQueryWrapper<Delivery> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            wrapper.eq(Delivery::getStatus, status);
        }
        wrapper.orderByDesc(Delivery::getCreateTime);
        return deliveryMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Delivery updateDeliveryStatus(Long deliveryId, String status) {
        Delivery delivery = deliveryMapper.selectById(deliveryId);
        if (delivery == null) {
            throw new BusinessException("配送记录不存在");
        }
        delivery.setStatus(status);
        if ("DELIVERED".equals(status)) {
            delivery.setActualTime(LocalDateTime.now());
        }
        deliveryMapper.updateById(delivery);
        return delivery;
    }

    @Override
    public Delivery getByOrderId(Long orderId) {
        return deliveryMapper.selectOne(
                new LambdaQueryWrapper<Delivery>().eq(Delivery::getOrderId, orderId));
    }
}
