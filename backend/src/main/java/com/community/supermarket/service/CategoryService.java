package com.community.supermarket.service;

import com.community.supermarket.dto.CategoryRequest;
import com.community.supermarket.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> listAll();
    Category create(CategoryRequest request);
    Category update(Long id, CategoryRequest request);
    void delete(Long id);
}
