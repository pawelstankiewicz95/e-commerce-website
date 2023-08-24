package com.pawelapps.ecommerce.controller;

import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") Long id) {
        Product product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping(value = "/products", consumes = "application/json;charset=UTF-8")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Product> createProduct(@RequestBody Product product){
        Product newProduct = productService.createProduct(product);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    @PutMapping("/products")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product){
        Product updatedProduct = productService.updateProduct(product);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> deleteProductById(@PathVariable("id") Long id){
        productService.deleteProductById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/products/products-by-category-id/{id}")
    public ResponseEntity<List<Product>> getProductsByCategoryId(@PathVariable("id") Long id){
        List<Product> productsByCategoryId = productService.getProductsByCategoryId(id);
        return new ResponseEntity<>(productsByCategoryId, HttpStatus.OK);
    }

    @GetMapping("/products/products-by-name-or-sku/{nameOrSku}")
    public ResponseEntity<List<Product>> getProductsByNameLikeOrSkuLike(@PathVariable("nameOrSku") String nameOrSku){
        List<Product> productsByNameOrSku = productService.getProductsByNameOrSku(nameOrSku);
        return new ResponseEntity<>(productsByNameOrSku, HttpStatus.OK);
    }
}
