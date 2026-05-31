package com.community.supermarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.supermarket.common.BusinessException;
import com.community.supermarket.dto.ProductRequest;
import com.community.supermarket.entity.Category;
import com.community.supermarket.entity.Inventory;
import com.community.supermarket.entity.Product;
import com.community.supermarket.mapper.CategoryMapper;
import com.community.supermarket.mapper.InventoryMapper;
import com.community.supermarket.mapper.ProductMapper;
import com.community.supermarket.service.ProductService;
import com.community.supermarket.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final InventoryMapper inventoryMapper;

    @Override
    public IPage<ProductVO> listProducts(Long categoryId, String keyword, int page, int size) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, "ON_SALE");
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Product::getName, keyword);
        }
        wrapper.orderByDesc(Product::getCreateTime);

        IPage<Product> productPage = productMapper.selectPage(new Page<>(page, size), wrapper);
        return productPage.convert(this::toProductVO);
    }

    @Override
    public ProductVO getProductDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return toProductVO(product);
    }

    @Override
    @Transactional
    public ProductVO createProduct(ProductRequest request) {
        Product product = new Product();
        product.setCategoryId(request.getCategoryId());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setUnit(request.getUnit() != null ? request.getUnit() : "件");
        product.setImageUrl(request.getImageUrl());
        product.setStatus(request.getStatus() != null ? request.getStatus() : "ON_SALE");
        product.setSeasonTag(request.getSeasonTag());
        productMapper.insert(product);

        Inventory inventory = new Inventory();
        inventory.setProductId(product.getId());
        inventory.setStockQuantity(request.getInitialStock() != null ? request.getInitialStock() : 0);
        inventory.setSafetyStock(request.getSafetyStock() != null ? request.getSafetyStock() : 10);
        inventoryMapper.insert(inventory);

        return toProductVO(product);
    }

    @Override
    @Transactional
    public ProductVO updateProduct(Long id, ProductRequest request) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        if (request.getCategoryId() != null) product.setCategoryId(request.getCategoryId());
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getOriginalPrice() != null) product.setOriginalPrice(request.getOriginalPrice());
        if (request.getUnit() != null) product.setUnit(request.getUnit());
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());
        if (request.getStatus() != null) product.setStatus(request.getStatus());
        if (request.getSeasonTag() != null) product.setSeasonTag(request.getSeasonTag());
        productMapper.updateById(product);
        return toProductVO(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productMapper.deleteById(id);
    }

    private ProductVO toProductVO(Product product) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setCategoryId(product.getCategoryId());
        vo.setName(product.getName());
        vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setUnit(product.getUnit());
        vo.setImageUrl(product.getImageUrl());
        vo.setStatus(product.getStatus());
        vo.setSeasonTag(product.getSeasonTag());

        Category category = categoryMapper.selectById(product.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        Inventory inventory = inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>().eq(Inventory::getProductId, product.getId()));
        if (inventory != null) {
            vo.setStockQuantity(inventory.getStockQuantity());
        }
        return vo;
    }
}
