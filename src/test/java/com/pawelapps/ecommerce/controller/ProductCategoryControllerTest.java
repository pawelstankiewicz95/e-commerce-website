package com.pawelapps.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawelapps.ecommerce.configuration.SecurityConfiguration;
import com.pawelapps.ecommerce.entity.ProductCategory;
import com.pawelapps.ecommerce.service.ProductCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductCategoryController.class)
@Import(SecurityConfiguration.class)
@AutoConfigureMockMvc
public class ProductCategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductCategoryService productCategoryService;

    @Autowired
    ObjectMapper objectMapper;

    ProductCategory productCategory;


    @Test
    @DisplayName("When getting all categories")
    void getAllCategoriesTest() throws Exception {

        List<ProductCategory> productCategoryList = Arrays.asList(
                ProductCategory.builder().categoryName("Category 1").build(),
                ProductCategory.builder().categoryName("Category 2").build()
        );

        when(productCategoryService.getAllProductCategories()).thenReturn(productCategoryList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/product-categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].categoryName", is("Category 1")))
                .andExpect(jsonPath("$[1].categoryName", is("Category 2")));


        verify(productCategoryService, times(1)).getAllProductCategories();
    }

    @Nested
    @DisplayName("When creating product category")
    class CreateProductCategoryTest {

        private void testForbiddenAccess() throws Exception {
            productCategory = ProductCategory.builder().categoryName("Category 1").build();

            when(productCategoryService.createProductCategory(any(ProductCategory.class))).thenReturn(productCategory);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/product-categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productCategory)))
                    .andExpect(status().isForbidden());

            verify(productCategoryService, times(0)).updateProductCategory(any(ProductCategory.class));
        }

        @Test
        @WithAnonymousUser
        void shouldReturnForbiddenForAnonymousUser() throws Exception {
            testForbiddenAccess();
        }

        @Test
        @WithMockUser(authorities = "user")
        void shouldReturnForbiddenForNotAuthorizedUser() throws Exception {
            testForbiddenAccess();
        }

        @Test
        @WithMockUser(authorities = "admin")
        void shouldCreateCategoryForAuthorizedUser() throws Exception {
            productCategory = ProductCategory.builder().categoryName("Category 1").build();

            when(productCategoryService.createProductCategory(any(ProductCategory.class))).thenReturn(productCategory);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/product-categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productCategory)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath(("$.categoryName"), is("Category 1")));

            verify(productCategoryService, times(1)).createProductCategory(any(ProductCategory.class));

        }
    }

    @Nested
    @DisplayName("When updating product category")
    class UpdateProductTests {

        @BeforeEach
        void setup() {
            productCategory = ProductCategory.builder().categoryName("Category 1").build();
        }

        private void testForbiddenAccess() throws Exception {
            when(productCategoryService.updateProductCategory(any(ProductCategory.class))).thenReturn(productCategory);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/product-categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productCategory)))
                    .andExpect(status().isForbidden());
            verify(productCategoryService, times(0)).updateProductCategory(any(ProductCategory.class));
        }

        @Test
        @WithAnonymousUser
        void shouldReturnForbiddenForAnonymousUser() throws Exception {
            testForbiddenAccess();
        }

        @Test
        @WithMockUser(authorities = "user")
        void shouldReturnForbiddenForUnauthorizedUser() throws Exception {
            testForbiddenAccess();
        }

        @Test
        @WithMockUser(authorities = "admin")
        void shouldUpdateCategoryForAdmin() throws Exception {
            when(productCategoryService.updateProductCategory(any(ProductCategory.class))).thenReturn(productCategory);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/product-categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(productCategory)))
                    .andExpect(status().isOk());

            verify(productCategoryService, times(1)).updateProductCategory(any(ProductCategory.class));
        }
    }

    @Test
    void getProductCategoryByIdTest() throws Exception {
        productCategory = ProductCategory.builder().id(123L).categoryName("Category 1").build();

        when(productCategoryService.getProductCategoryById(123L)).thenReturn(productCategory);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/product-categories/{id}", 123))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(123)))
                .andExpect(jsonPath("$.categoryName", is("Category 1")));

        verify(productCategoryService, times(1)).getProductCategoryById(123L);

    }
}
