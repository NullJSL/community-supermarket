package com.community.supermarket.controller;

import com.community.supermarket.common.Result;
import com.community.supermarket.dto.CategoryRequest;
import com.community.supermarket.entity.Category;
import com.community.supermarket.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品分类")
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取所有分类")
    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.listAll());
    }

    @Operation(summary = "新增分类（管理员）")
    @PostMapping
    public Result<Category> create(@Valid @RequestBody CategoryRequest request) {
        return Result.success(categoryService.create(request));
    }

    @Operation(summary = "编辑分类（管理员）")
    @PutMapping("/{id}")
    public Result<Category> update(@PathVariable Long id,
                                    @Valid @RequestBody CategoryRequest request) {
        return Result.success(categoryService.update(id, request));
    }

    @Operation(summary = "删除分类（管理员）")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
