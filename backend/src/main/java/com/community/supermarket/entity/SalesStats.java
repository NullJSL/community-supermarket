package com.community.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_sales_stats")
public class SalesStats {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private LocalDate statDate;
    private Integer quantitySold;
    private BigDecimal revenue;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
