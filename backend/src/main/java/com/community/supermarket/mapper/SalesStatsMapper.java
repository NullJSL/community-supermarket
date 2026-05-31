package com.community.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.supermarket.entity.SalesStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface SalesStatsMapper extends BaseMapper<SalesStats> {

    @Select("SELECT AVG(quantity_sold) FROM t_sales_stats " +
            "WHERE product_id = #{productId} AND stat_date >= #{startDate} AND stat_date <= #{endDate}")
    BigDecimal getAvgDailySales(@Param("productId") Long productId,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);

    @Select("SELECT product_id, SUM(quantity_sold) as total_sold, SUM(revenue) as total_revenue " +
            "FROM t_sales_stats WHERE stat_date >= #{startDate} AND stat_date <= #{endDate} " +
            "GROUP BY product_id ORDER BY total_sold DESC LIMIT #{limit}")
    List<Map<String, Object>> getTopSellingProducts(@Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate,
                                                     @Param("limit") int limit);
}
