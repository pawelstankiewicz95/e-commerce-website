package com.pawelapps.ecommerce.controller;

import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.entity.ProductCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@Transactional
public class ProductCategoryControllerIT extends BaseIT {

    private final String uri = "/product-categories";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductCategory productCategory1;
    private ProductCategory productCategory2;

    @BeforeEach
    void setUp() {
        productCategory1 = ProductCategory.builder().id(1L).categoryName("Test category 1").build();
        productCategory2 = ProductCategory.builder().id(2L).categoryName("Test category 2").build();
        entityManager.persist(productCategory1);
        entityManager.persist(productCategory2);
        entityManager.clear();
    }
}
