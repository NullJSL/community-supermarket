package com.community.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String unit;
    private String imageUrl;
    private String status;
    private String seasonTag;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
