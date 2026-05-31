package com.community.supermarket.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RestockAlertVO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer currentStock;
    private Integer safetyStock;
    private Integer suggestedQuantity;
    private String status;
    private LocalDateTime alertTime;
}
