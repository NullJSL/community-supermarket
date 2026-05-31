package com.community.supermarket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.community.supermarket.dto.ProductRequest;
import com.community.supermarket.vo.ProductVO;

public interface ProductService {
    IPage<ProductVO> listProducts(Long categoryId, String keyword, int page, int size);
    ProductVO getProductDetail(Long id);
    ProductVO createProduct(ProductRequest request);
    ProductVO updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
}
