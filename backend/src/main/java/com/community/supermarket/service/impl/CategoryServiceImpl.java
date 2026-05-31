package com.community.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.supermarket.common.BusinessException;
import com.community.supermarket.dto.CategoryRequest;
import com.community.supermarket.entity.Category;
import com.community.supermarket.mapper.CategoryMapper;
import com.community.supermarket.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> listAll() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus, 1)
                        .orderByAsc(Category::getSortOrder));
    }

    @Override
    public Category create(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setStatus(1);
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public Category update(Long id, CategoryRequest request) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        if (request.getName() != null) category.setName(request.getName());
        if (request.getIcon() != null) category.setIcon(request.getIcon());
        if (request.getSortOrder() != null) category.setSortOrder(request.getSortOrder());
        categoryMapper.updateById(category);
        return category;
    }

    @Override
    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
}
