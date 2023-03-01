package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.entity.Product;

import java.util.List;


public interface ProductService {
    Product createProduct(Product product);
    Product getProductById(Long id);
    List<Product> getAllProducts();
    Product updateProduct(Product product);
}
