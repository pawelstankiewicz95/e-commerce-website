package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dao.ProductRepository;
import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.entity.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProductServiceTest {

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    Product product;

    ProductCategory productCategory;

    @BeforeEach
    void setUp() {
        productCategory = ProductCategory.builder().categoryName("Cup").build();
        product = Product.builder()
                .sku("123456")
                .name("TestCup")
                .description("Just testing cup")
                .unitPrice(BigDecimal.valueOf(24.35))
                .imageUrl("www.test.com")
                .active(true)
                .unitsInStock(10)
                .dateCreated(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .productCategory(productCategory)
                .build();
    }

    @Test
    void createProductTest(){
        when(productService.createProduct(any(Product.class))).thenReturn(product);
        assertEquals(product, productService.createProduct(new Product()), "products should be equals");
        verify(productRepository).createProduct(any(Product.class));
    }
}
