package com.pawelapps.ecommerce;

import com.pawelapps.ecommerce.dao.ProductRepository;
import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.entity.ProductCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@TestPropertySource("/test-application.properties")
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    void saveProductTest(){

        ProductCategory productCategory = ProductCategory.builder().categoryName("Book").build();
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
                .productCategory(productCategory)
                .build();
        productRepository.save(product);
        assertTrue(product.getId() > 0, "should be greater than 0");


    }
}
