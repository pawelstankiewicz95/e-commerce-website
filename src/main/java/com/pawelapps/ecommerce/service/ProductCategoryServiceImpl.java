package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dao.ProductCategoryRepository;
import com.pawelapps.ecommerce.entity.ProductCategory;
import com.pawelapps.ecommerce.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    public ProductCategoryServiceImpl(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }


    @Override
    public ProductCategory createProductCategory(ProductCategory productCategory) {
        return productCategoryRepository.save(productCategory);
    }

    @Override
    public ProductCategory getProductCategoryById(Long id) {
        return productCategoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Product category with id " + id + " doesn't exist"));
    }

    @Override
    public List<ProductCategory> getAllProductCategories() {
        return productCategoryRepository.findAll();
    }

    @Override
    public ProductCategory updateProductCategory(ProductCategory productCategory) {
        return productCategoryRepository.save(productCategory);
    }

    @Override
    public void deleteProductCategoryById(Long id) {
        productCategoryRepository.deleteById(id);
    }
}
