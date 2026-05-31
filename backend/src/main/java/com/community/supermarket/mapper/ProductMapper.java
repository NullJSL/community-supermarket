package com.community.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.supermarket.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
