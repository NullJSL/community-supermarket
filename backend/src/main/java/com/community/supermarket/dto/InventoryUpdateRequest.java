package com.community.supermarket.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class InventoryUpdateRequest {
    @Min(value = 0, message = "库存不能为负数")
    private Integer stockQuantity;

    @Min(value = 0, message = "安全库存不能为负数")
    private Integer safetyStock;
}
