package com.pawelapps.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.entity.ProductCategory;
import com.pawelapps.ecommerce.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
import org.springframework.test.web.servlet.MvcResult;
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

    private ProductCategory getProductCategoryFromDB(Long id) {
        ProductCategory productCategoryFromDB;

        TypedQuery<ProductCategory> query = entityManager.createQuery(
                "SELECT pc FROM ProductCategory pc LEFT JOIN FETCH pc.products WHERE pc.id = :id", ProductCategory.class);
        query.setParameter("id", id);

        try {
            productCategoryFromDB = query.getSingleResult();

        } catch (NoResultException noResultException) {
            productCategoryFromDB = null;
        }

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
            ProductCategory categoryFromDB = getProductCategoryFromDB(productCategory1.getId());
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

        private ProductCategory productCategory = ProductCategory.builder().categoryName("Category for create tests").build();

        private void testForbiddenAccess() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productCategory)))
                    .andExpect(status().isForbidden());
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
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productCategory)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.categoryName").value(productCategory.getCategoryName())).andReturn();

            String content = result.getResponse().getContentAsString();
            JsonPath.parse(content).read("$.id", Long.class);
            Long id = JsonPath.parse(content).read("$.id", Long.class);
            assertNotNull(id);

            assertNotNull(getProductCategoryFromDB(id));
        }
    }

    @Nested
    class UpdateProductCategoryTests {

        private void testUnauthorizedUpdate() throws Exception {
            ProductCategory existingProductCategory = getProductCategoryFromDB(productCategory1.getId());
            String initialProductCategoryName = existingProductCategory.getCategoryName();
            assertEquals("Test category 1", initialProductCategoryName);

            existingProductCategory.setCategoryName("Updated Name");

            mockMvc.perform(MockMvcRequestBuilders.put(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(existingProductCategory)))
                    .andExpect(status().isForbidden());

            ProductCategory productCategoryAfterUpdateAttempt = getProductCategoryFromDB(productCategory1.getId());
            assertEquals(initialProductCategoryName, productCategoryAfterUpdateAttempt.getCategoryName(), "Name shouldn't be updated");

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
        void shouldUpdateProductCategoryForAuthorizedUser() throws Exception {
            Long productCategoryId = productCategory1.getId();
            ProductCategory initialProductCategory = getProductCategoryFromDB(productCategoryId);
            assertEquals("Test category 1", initialProductCategory.getCategoryName());

            initialProductCategory.setCategoryName("Updated Name");

            mockMvc.perform(MockMvcRequestBuilders.put(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(initialProductCategory)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.categoryName").value("Updated Name"));

            ProductCategory categoryAfterUpdate = getProductCategoryFromDB(productCategory1.getId());

            assertEquals("Updated Name", categoryAfterUpdate.getCategoryName());
        }
    }

    @Nested
    class deleteProductCategoryTests {

        private void testUnauthorizedDelete() throws Exception {
            ProductCategory existingProductCategory = getProductCategoryFromDB(productCategory1.getId());
            String initialProductCategoryName = existingProductCategory.getCategoryName();
            assertNotNull(existingProductCategory);
            assertEquals("Test category 1", initialProductCategoryName);

            Long existingProductCategoryId = existingProductCategory.getId();
            mockMvc.perform(MockMvcRequestBuilders.delete(uri + "/" + existingProductCategoryId))
                    .andExpect(status().isForbidden());

            Optional<ProductCategory> optionalProductCategory = Optional.ofNullable(getProductCategoryFromDB(productCategory1.getId()));

            assertTrue(optionalProductCategory.isPresent(), "Product category should be present");
            assertEquals("Test category 1", optionalProductCategory.get().getCategoryName());
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
            ProductCategory existingProductCategory = getProductCategoryFromDB(productCategory1.getId());
            String initialProductCategoryName = existingProductCategory.getCategoryName();
            assertNotNull(existingProductCategory);
            assertEquals(initialProductCategoryName, "Test category 1");

            List<Product> productsInExistingProductCategory = existingProductCategory.getProducts();
            assertEquals(2, productsInExistingProductCategory.size());

            entityManager.clear();

            Long existingProductCategoryId = existingProductCategory.getId();
            mockMvc.perform(MockMvcRequestBuilders.delete(uri + "/" + existingProductCategoryId))
                    .andExpect(status().isOk());

            Optional<ProductCategory> deletedProductCategory = Optional.ofNullable(getProductCategoryFromDB(productCategory1.getId()));
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
