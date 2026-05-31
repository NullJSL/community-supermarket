package com.community.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.supermarket.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {

    @Update("UPDATE t_inventory SET stock_quantity = stock_quantity - #{quantity}, " +
            "update_time = NOW() WHERE product_id = #{productId} AND stock_quantity >= #{quantity} AND deleted = 0")
    int deductStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    @Update("UPDATE t_inventory SET stock_quantity = stock_quantity + #{quantity}, " +
            "last_restock_time = NOW(), update_time = NOW() WHERE product_id = #{productId} AND deleted = 0")
    int addStock(@Param("productId") Long productId, @Param("quantity") int quantity);
}
