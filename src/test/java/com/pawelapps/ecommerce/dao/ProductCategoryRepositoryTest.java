package com.pawelapps.ecommerce.dao;

import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.entity.ProductCategory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@DataJpaTest
@TestPropertySource("/test-application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProductCategoryRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    Product product;
    ProductCategory productCategory;

    @BeforeEach
    void setUpDataBase() {
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
        productCategory = ProductCategory.builder().categoryName("Cup").build();
        productCategory.addProduct(product);
        entityManager.persist(product);
        entityManager.flush();
    }
}
