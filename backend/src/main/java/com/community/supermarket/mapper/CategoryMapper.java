package com.community.supermarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.supermarket.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
