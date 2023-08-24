package com.pawelapps.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.entity.ProductCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
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

    String uri = "/api/products";

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
        mockMvc.perform(MockMvcRequestBuilders.get(uri))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Nested
    class createProductTest {

        @BeforeEach
        void setUp() {
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

        private void testAccessForbidden() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post(uri)
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
            mockMvc.perform(MockMvcRequestBuilders.post(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath(("$.sku"), is("333222")));
        }
    }

    Product getProductFromDB(String sku) {
        Product productFromDB;
        TypedQuery<Product> query = entityManager.createQuery(
                "SELECT p FROM Product p WHERE p.sku = :sku", Product.class);
        query.setParameter("sku", sku);
        List<Product> products = query.getResultList();
        if (products.size() > 0) {
            productFromDB = products.get(0);
        } else productFromDB = null;

        entityManager.clear();
        return productFromDB;
    }

    @Nested
    class UpdateProductTest {
        private final String sku = "123456";

        private void testUnauthorizedUpdate() throws Exception {
            Product existingProduct = getProductFromDB(this.sku);
            String initialProductName = existingProduct.getName();
            String initialProductDescription = existingProduct.getDescription();

            assertEquals(initialProductName, "Test Product 1");
            assertEquals(initialProductDescription, "Product for testing 1");

            existingProduct.setName("Updated Name");
            existingProduct.setDescription("Updated Description");

            mockMvc.perform(MockMvcRequestBuilders.put(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(existingProduct)))
                    .andExpect(status().isForbidden());


            Product productAfterUpdateAttempt = getProductFromDB(sku);
            assertEquals(initialProductName, productAfterUpdateAttempt.getName(), "Name shouldn't be updated");
            assertEquals(initialProductDescription, productAfterUpdateAttempt.getDescription(), "Description shouldn't be updated");

        }

        @Test
        @WithAnonymousUser
        void shouldNotUpdateProductForAnonymousUser() throws Exception {
            testUnauthorizedUpdate();
        }

        @Test
        @WithMockUser(authorities = "user")
        void shouldNotUpdateProductForUnauthorizedUser() throws Exception {
            testUnauthorizedUpdate();
        }

        @Test
        @WithMockUser(authorities = "admin")
        void shouldUpdateProductForAuthorizedUser() throws Exception {
            Product existingProduct = getProductFromDB(this.sku);
            String initialProductName = existingProduct.getName();
            String initialProductDescription = existingProduct.getDescription();
            assertEquals(initialProductName, "Test Product 1");
            assertEquals(initialProductDescription, "Product for testing 1");

            existingProduct.setName("Updated Name");
            existingProduct.setDescription("Updated Description");

            mockMvc.perform(MockMvcRequestBuilders.put(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(existingProduct)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated Name"))
                    .andExpect(jsonPath("$.description").value("Updated Description"));

            Product updatedProduct = getProductFromDB(this.sku);
            assertEquals(updatedProduct.getName(), "Updated Name");
            assertEquals(updatedProduct.getDescription(), "Updated Description");
        }
    }

    @Nested
    class DeleteProductTest {
        private final String sku = "888999";

        private void testUnauthorizedDelete() throws Exception{
            Product existingProduct = getProductFromDB(this.sku);
            assertNotNull(existingProduct);

            String existingProductName = existingProduct.getName();
            String existingProductDescription = existingProduct.getDescription();
            assertEquals(existingProductName, "Test Product 2");
            assertEquals(existingProductDescription, "Product for testing 2");

            mockMvc.perform(MockMvcRequestBuilders.delete(uri + "/" + existingProduct.getId()))
                    .andExpect(status().isForbidden());

            Optional<Product> optionalProduct = Optional.ofNullable(getProductFromDB(this.sku));
            assertTrue(optionalProduct.isPresent(), "Optional product should not be empty");
            assertEquals(optionalProduct.get().getName(), "Test Product 2");
            assertEquals(optionalProduct.get().getDescription(), "Product for testing 2");
        }

        @Test
        @WithAnonymousUser
        void shouldNotDeleteProductForAnonymous() throws Exception{
            testUnauthorizedDelete();
        }

        @Test
        @WithMockUser(authorities = "admin")
        void shouldDeleteProductForAuthorizedUser() throws Exception {
            Product existingProduct = getProductFromDB(this.sku);
            assertNotNull(existingProduct);

            String existingProductName = existingProduct.getName();
            String existingProductDescription = existingProduct.getDescription();
            assertEquals(existingProductName, "Test Product 2");
            assertEquals(existingProductDescription, "Product for testing 2");

            mockMvc.perform(MockMvcRequestBuilders.delete(uri + "/" + existingProduct.getId()))
                    .andExpect(status().isOk());

            Optional<Product> optionalProduct = Optional.ofNullable(getProductFromDB(this.sku));
            assertTrue(optionalProduct.isEmpty(), "Optional product should be empty");
        }

        @Test
        @WithMockUser(authorities = "name")
        void shouldNotDeleteProductForUnauthorizedUser() throws Exception{
           testUnauthorizedDelete();
        }
    }
}
