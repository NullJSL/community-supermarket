package com.community.supermarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeasonalRuleRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotBlank(message = "季节不能为空")
    private String season;

    @NotNull(message = "需求系数不能为空")
    private BigDecimal demandMultiplier;

    private Boolean autoAdjustSafetyStock;
}
