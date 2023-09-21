package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dao.ProductRepository;
import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(Product product) {
        product.setDateCreated(LocalDateTime.now());
        productRepository.save(product);
        return product;
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product with id " + id + " doesn't exist"));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(Product product) {
        product.setLastUpdated(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> getProductsByCategoryId(Long id) {
        return productRepository.findByProductCategoryId(id);
    }

    @Override
    public List<Product> getProductsByNameOrSku(String nameOrSku) {
        return productRepository.findByNameLikeOrSkuLike(nameOrSku);
    }

    @Override
    public Product decreaseProductQuantity(Long productId, int quantityToDecrease) {
        Product product = getProductById(productId);
        int newQuantity = product.getUnitsInStock() - quantityToDecrease;

        if (newQuantity < 0) {
            throw new IllegalArgumentException("Not enough quantity available for the product.");
        }

        product.setUnitsInStock(newQuantity);
        return productRepository.save(product);
    }
}
