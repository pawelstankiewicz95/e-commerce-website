package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.entity.ProductCategory;

import java.util.List;


public interface ProductCategoryService {
    ProductCategory createProductCategory(ProductCategory productCategory);

    ProductCategory getProductCategoryById(Long id);

    List<ProductCategory> getAllProductCategories();

    ProductCategory updateProductCategory(ProductCategory productCategory);

    void deleteProductCategoryById(Long id);
}
