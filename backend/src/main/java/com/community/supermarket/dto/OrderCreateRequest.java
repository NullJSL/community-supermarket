package com.community.supermarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequest {
    @NotBlank(message = "配送地址不能为空")
    private String deliveryAddress;

    @NotBlank(message = "配送电话不能为空")
    private String deliveryPhone;

    private String remark;

    @NotEmpty(message = "订单商品不能为空")
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        @Positive(message = "商品ID必须大于0")
        private Long productId;

        @Positive(message = "数量必须大于0")
        private Integer quantity;
    }
}
