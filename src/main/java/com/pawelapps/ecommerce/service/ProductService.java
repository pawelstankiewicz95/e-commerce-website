package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.entity.Product;


public interface ProductService {
    Product createProduct(Product product);

    Product getProductById(Long id);
}
