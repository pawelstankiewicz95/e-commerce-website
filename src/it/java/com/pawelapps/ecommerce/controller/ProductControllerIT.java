package com.pawelapps.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.entity.ProductCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT extends BaseIT {

    @Autowired
    MockMvc mockMvc;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ObjectMapper objectMapper;

    Product product;

    ProductCategory productCategory;

    @BeforeEach
    void setUpDataBase() {

        productCategory = ProductCategory.builder().categoryName("Test Category").build();
        entityManager.persist(productCategory);
        entityManager.flush();

        product = Product.builder()
                .sku("123456")
                .name("Test Product 1")
                .description("Product for testing 1")
                .unitPrice(BigDecimal.valueOf(24.35))
                .imageUrl("assets/images/test-image-1.jpg")
                .active(true)
                .unitsInStock(10)
                .dateCreated(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .productCategory(productCategory)
                .build();
        entityManager.persist(product);

        product = Product.builder()
                .sku("888999")
                .name("Test Product 2")
                .description("Product for testing 2")
                .unitPrice(BigDecimal.valueOf(55.45))
                .imageUrl("assets/images/test-image-2.jpg")
                .active(true)
                .unitsInStock(10)
                .dateCreated(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .productCategory(productCategory)
                .build();
        entityManager.persist(product);
        entityManager.flush();
    }


    @Test
    void getAllProductsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Nested
    class createProductTest {

        @BeforeEach
        void setUp() {
            productCategory = ProductCategory.builder().categoryName("Test Category For Creating Products").build();
            entityManager.persist(productCategory);
            entityManager.flush();

            product = Product.builder()
                    .sku("333222")
                    .name("Created Test Product")
                    .description("Just Created Test Product")
                    .productCategory(productCategory)
                    .unitPrice(BigDecimal.valueOf(24.35))
                    .imageUrl("assets/images/test-image-1.jpg")
                    .active(true)
                    .unitsInStock(10)
                    .dateCreated(LocalDateTime.now())
                    .lastUpdated(LocalDateTime.now())
                    .productCategory(productCategory)
                    .build();
        }

        void testAccessForbidden() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithAnonymousUser
        void shouldReturnForbiddenForAnonymous() throws Exception {
            testAccessForbidden();
        }

        @Test
        @WithMockUser(authorities = "user")
        void shouldReturnForbiddenForUnauthorized() throws Exception {
            testAccessForbidden();
        }

        @Test
        @WithMockUser(authorities = "admin")
        void shouldCreateProductForAuthorizedUser() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath(("$.sku"), is("333222")));
        }
    }
}