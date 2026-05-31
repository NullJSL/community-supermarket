package com.community.supermarket.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String unit;
    private String imageUrl;
    private String status;
    private String seasonTag;
    private Integer stockQuantity;
}
