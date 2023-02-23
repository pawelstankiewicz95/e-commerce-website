package com.pawelapps.ecommerce;

import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.entity.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;


class ProductCategoryTest {

    ProductCategory productCategory;

    @BeforeEach
    void init() {
        productCategory = new ProductCategory();
    }

    @Test
    void testAddProduct() {
        Product product = Product.builder()
                .sku("123456")
                .name("Cup")
                .description("Just testing cup")
                .unitPrice(BigDecimal.valueOf(24.35))
                .imageUrl("www.test.com")
                .active(true)
                .unitsInStock(10)
                .dateCreated(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();
        productCategory.addProduct(product);
        assertFalse(productCategory.getProducts().isEmpty(), "Should not be empty");
        assertTrue(productCategory.getProducts().contains(product), "Should contain added product");
    }
}
