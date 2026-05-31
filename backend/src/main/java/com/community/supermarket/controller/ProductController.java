package com.community.supermarket.controller;

import com.community.supermarket.common.PageResult;
import com.community.supermarket.common.Result;
import com.community.supermarket.dto.ProductRequest;
import com.community.supermarket.service.ProductService;
import com.community.supermarket.vo.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品模块")
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "商品列表")
    @GetMapping("/product/list")
    public Result<PageResult<ProductVO>> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(PageResult.of(productService.listProducts(categoryId, keyword, page, size)));
    }

    @Operation(summary = "商品详情")
    @GetMapping("/product/{id}")
    public Result<ProductVO> detail(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }

    @Operation(summary = "新增商品（管理员）")
    @PostMapping("/admin/product")
    public Result<ProductVO> create(@Valid @RequestBody ProductRequest request) {
        return Result.success(productService.createProduct(request));
    }

    @Operation(summary = "编辑商品（管理员）")
    @PutMapping("/admin/product/{id}")
    public Result<ProductVO> update(@PathVariable Long id,
                                     @Valid @RequestBody ProductRequest request) {
        return Result.success(productService.updateProduct(id, request));
    }

    @Operation(summary = "删除商品（管理员）")
    @DeleteMapping("/admin/product/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }
}
