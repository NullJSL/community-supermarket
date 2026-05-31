package com.community.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_restock_alert")
public class RestockAlert {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Integer currentStock;
    private Integer safetyStock;
    private Integer suggestedQuantity;
    private String status;
    private LocalDateTime alertTime;
    private LocalDateTime processTime;
}
