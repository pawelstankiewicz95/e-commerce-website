package com.pawelapps.ecommerce.controller;

import com.pawelapps.ecommerce.entity.ProductCategory;
import com.pawelapps.ecommerce.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:4200")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @Autowired
    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @GetMapping("/product-categories")
    public ResponseEntity<List<ProductCategory>> getAllProductCategories() {
        List<ProductCategory> productCategories = productCategoryService.getAllProductCategories();
        return new ResponseEntity<>(productCategories, HttpStatus.OK);
    }

    @GetMapping("/product-categories/{id}")
    public ResponseEntity<ProductCategory> getProductById(@PathVariable("id") Long id) {
        ProductCategory productCategory = productCategoryService.getProductCategoryById(id);
        return new ResponseEntity<>(productCategory, HttpStatus.OK);
    }

    @PostMapping(value = "/product-categories", consumes = "application/json;charset=UTF-8")
    public ResponseEntity<ProductCategory> createProduct(@RequestBody ProductCategory productCategory) {
        ProductCategory newProductCategory = productCategoryService.createProductCategory(productCategory);
        return new ResponseEntity<>(productCategory, HttpStatus.CREATED);
    }

    @PutMapping("/product-categories")
    public ResponseEntity<ProductCategory> updateProduct(@RequestBody ProductCategory productCategory) {
        ProductCategory updatedProductCategory = productCategoryService.updateProductCategory(productCategory);
        return new ResponseEntity<>(updatedProductCategory, HttpStatus.OK);
    }

    @DeleteMapping("/product-categories/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable("id") Long id) {
        productCategoryService.deleteProductCategoryById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
