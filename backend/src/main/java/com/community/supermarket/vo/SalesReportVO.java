package com.community.supermarket.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class SalesReportVO {
    private BigDecimal totalRevenue;
    private Integer totalQuantitySold;
    private Integer totalOrders;
    private List<Map<String, Object>> topProducts;
    private List<DailySales> dailySales;

    @Data
    public static class DailySales {
        private String date;
        private Integer quantity;
        private BigDecimal revenue;
    }
}
