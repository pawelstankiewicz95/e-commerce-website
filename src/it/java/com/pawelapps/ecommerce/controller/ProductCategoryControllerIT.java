package com.pawelapps.ecommerce.controller;

import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.entity.ProductCategory;
import com.pawelapps.ecommerce.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
public class ProductCategoryControllerIT extends BaseIT {

    private final String uri = "/api/product-categories";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private ProductCategory productCategory1;
    private ProductCategory productCategory2;

    @BeforeEach
    void setUp() {
        productCategory1 = ProductCategory.builder().categoryName("Test category 1").build();
        productCategory2 = ProductCategory.builder().categoryName("Test category 2").build();
        entityManager.persist(productCategory1);
        entityManager.persist(productCategory2);
        entityManager.clear();
    }

    private ProductCategory getProductCategoryFromDBByName(String name) {
        ProductCategory productCategoryFromDB;
        TypedQuery<ProductCategory> query = entityManager.createQuery(
                "SELECT c FROM ProductCategory c WHERE c.categoryName = :name", ProductCategory.class);
        query.setParameter("name", name);

        List<ProductCategory> categories = query.getResultList();
        if (categories.size() > 0) {
            productCategoryFromDB = categories.get(0);
        } else productCategoryFromDB = null;

        entityManager.clear();
        return productCategoryFromDB;
    }

    @Nested
    class getProductCategoryTest {

        @Test
        void shouldReturnAllProductCategories() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(uri))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        void shouldGetProductCategoryWhenIdExist() throws Exception {
            ProductCategory categoryFromDB = getProductCategoryFromDBByName("Test category 1");
            Long categoryId = categoryFromDB.getId();
            mockMvc.perform(MockMvcRequestBuilders.get(uri + "/" + categoryId))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.categoryName").value("Test category 1"));
        }

        @Test
        void shouldThrowNotFoundExceptionWhenIdDoesNotExist() throws Exception {
            Long notExistingCategoryID = 9999999999L;
            mockMvc.perform(MockMvcRequestBuilders.get(uri + "/" + notExistingCategoryID))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
        }
    }
}
