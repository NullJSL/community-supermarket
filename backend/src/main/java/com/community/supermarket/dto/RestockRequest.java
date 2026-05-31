package com.community.supermarket.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RestockRequest {
    @Positive(message = "补货数量必须大于0")
    private Integer quantity;
}
