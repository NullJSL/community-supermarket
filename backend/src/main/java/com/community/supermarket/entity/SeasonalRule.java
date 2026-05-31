package com.community.supermarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_seasonal_rule")
public class SeasonalRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private String season;
    private BigDecimal demandMultiplier;
    private Integer autoAdjustSafetyStock;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
