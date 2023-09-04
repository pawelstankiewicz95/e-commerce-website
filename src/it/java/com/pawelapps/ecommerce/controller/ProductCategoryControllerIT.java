package com.pawelapps.ecommerce.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.entity.Product;
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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
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
    private Product product;

    @BeforeEach
    void setUp() {
        productCategory1 = ProductCategory.builder().categoryName("Test category 1").build();
        productCategory2 = ProductCategory.builder().categoryName("Test category 2").build();

        productCategory1.addProduct(Product.builder().sku("123").name("Product 1 in Test Category 1").build());
        productCategory1.addProduct(Product.builder().sku("124").name("Product 2 in Test Category 1").build());
        entityManager.persist(productCategory1);
        entityManager.persist(productCategory2);
        entityManager.clear();
    }

    private ProductCategory getProductCategoryFromDBByName(String name) {
        ProductCategory productCategoryFromDB;
        TypedQuery<ProductCategory> query = entityManager.createQuery(
                "SELECT pc FROM ProductCategory pc LEFT JOIN FETCH pc.products WHERE pc.categoryName = :name", ProductCategory.class);
        query.setParameter("name", name);

        List<ProductCategory> categories = query.getResultList();
        if (categories.size() > 0) {
            productCategoryFromDB = categories.get(0);
        } else productCategoryFromDB = null;

        entityManager.clear();
        return productCategoryFromDB;
    }

    @Nested
    class getProductCategoryTests {

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

    @Nested
    class createProductCategoryTests {

        private final String categoryName = "Category for create tests";
        private ProductCategory productCategory = ProductCategory.builder().categoryName(categoryName).build();

        private void testForbiddenAccess() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productCategory)))
                    .andExpect(status().isForbidden());

            assertNull(getProductCategoryFromDBByName(categoryName));
        }

        @Test
        @WithAnonymousUser
        void shouldNotCreateProductCategoryForAnonymousUser() throws Exception {
            testForbiddenAccess();
        }

        @Test
        @WithMockUser(authorities = "user")
        void shouldNotCreateProductCategoryForUnauthorizedUser() throws Exception {
            testForbiddenAccess();
        }

        @Test
        @WithMockUser(authorities = "admin")
        void shouldCreateProductCategoryForAuthorizedUser() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productCategory)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.categoryName").value(categoryName));

            assertNotNull(getProductCategoryFromDBByName(categoryName));
        }
    }

    @Nested
    class UpdateProductCategoryTests {

        private void testUnauthorizedUpdate() throws Exception {
            ProductCategory existingProductCategory = getProductCategoryFromDBByName("Test category 1");
            String initialProductCategoryName = existingProductCategory.getCategoryName();
            assertEquals(initialProductCategoryName, "Test category 1");

            existingProductCategory.setCategoryName("Updated Name");

            mockMvc.perform(MockMvcRequestBuilders.put(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(existingProductCategory)))
                    .andExpect(status().isForbidden());

            ProductCategory productAfterUpdateAttempt = getProductCategoryFromDBByName("Test category 1");
            assertEquals(initialProductCategoryName, productAfterUpdateAttempt.getCategoryName(), "Name shouldn't be updated");

        }

        @Test
        @WithAnonymousUser
        void shouldNotUpdateProductCategoryForAnonymousUser() throws Exception {
            testUnauthorizedUpdate();
        }

        @Test
        @WithMockUser(authorities = "user")
        void shouldNotUpdateProductCategoryForUnauthorizedUser() throws Exception {
            testUnauthorizedUpdate();
        }

        @Test
        @WithMockUser(authorities = "admin")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        void shouldUpdateProductCategoryForAuthorizedUser() throws Exception {
            ProductCategory initialProductCategory = getProductCategoryFromDBByName("Test category 1");
            Long existingProductCategoryId = initialProductCategory.getId();

            initialProductCategory.setCategoryName("Updated Name");

            mockMvc.perform(MockMvcRequestBuilders.put(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(initialProductCategory)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.categoryName").value("Updated Name"));

            ProductCategory categoryByOldName = getProductCategoryFromDBByName("Test category 1");
            ProductCategory categoryAfterUpdate = getProductCategoryFromDBByName("Updated Name");

            assertNull(categoryByOldName, "Category with name \"Test category 1\" should not exists");
            assertNotNull(categoryAfterUpdate, "Category name should be \"Updated Name\"");
            assertEquals(categoryAfterUpdate.getId(), existingProductCategoryId, "Category Id should not change");
        }
    }

    @Nested
    class deleteProductCategoryTests {

        private void testUnauthorizedDelete() throws Exception {
            ProductCategory existingProductCategory = getProductCategoryFromDBByName("Test category 1");
            String initialProductCategoryName = existingProductCategory.getCategoryName();
            assertNotNull(existingProductCategory);
            assertEquals(initialProductCategoryName, "Test category 1");

            Long existingProductCategoryId = existingProductCategory.getId();
            mockMvc.perform(MockMvcRequestBuilders.delete(uri + "/" + existingProductCategoryId))
                    .andExpect(status().isForbidden());

            Optional<ProductCategory> optionalProductCategory = Optional.ofNullable(getProductCategoryFromDBByName("Test category 1"));

            assertTrue(optionalProductCategory.isPresent(), "Product category should be present");
            assertEquals(optionalProductCategory.get().getCategoryName(), "Test category 1");
        }

        @Test
        @WithMockUser(authorities = "user")
        void shouldNotDeleteProductCategoryForUnauthorizedUser() throws Exception {
            testUnauthorizedDelete();
        }

        @Test
        @WithMockUser(authorities = "user")
        void shouldNotDeleteProductCategoryForAnonymousUser() throws Exception {
            testUnauthorizedDelete();
        }

        @Test
        @WithMockUser(authorities = "admin")
        void shouldDeleteProductCategoryForAuthorizedUser() throws Exception {
            ProductCategory existingProductCategory = getProductCategoryFromDBByName("Test category 1");
            String initialProductCategoryName = existingProductCategory.getCategoryName();
            assertNotNull(existingProductCategory);
            assertEquals(initialProductCategoryName, "Test category 1");

            List<Product> productsInExistingProductCategory = existingProductCategory.getProducts();
            assertEquals(2, productsInExistingProductCategory.size());

            entityManager.clear();

            Long existingProductCategoryId = existingProductCategory.getId();
            mockMvc.perform(MockMvcRequestBuilders.delete(uri + "/" + existingProductCategoryId))
                    .andExpect(status().isOk());

            Optional<ProductCategory> deletedProductCategory = Optional.ofNullable(getProductCategoryFromDBByName("Test category 1"));
            assertTrue(deletedProductCategory.isEmpty(), "Product category should be deleted");

            TypedQuery<Product> query = entityManager.createQuery("select p from Product p where p.productCategory.id = :id", Product.class);
            query.setParameter("id", existingProductCategoryId);
            List<Product> products = query.getResultList();
            assertEquals(0, products.size(), "Products should be deleted with category");

        }

        @Test
        void shouldThrowNotFoundExceptionWhenProductCategoryIdDoesNotExist() throws Exception {
            Long notExistingProductCategoryID = 99999999999L;
            mockMvc.perform(MockMvcRequestBuilders.get(uri + "/" + notExistingProductCategoryID))
                    .andExpect(status().isNotFound())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
        }

    }
}
