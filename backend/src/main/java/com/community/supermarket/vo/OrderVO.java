package com.community.supermarket.vo;

import com.community.supermarket.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;
    private String deliveryAddress;
    private String deliveryPhone;
    private String remark;
    private LocalDateTime createTime;
    private List<OrderItem> items;
    private String deliveryStatus;
}
