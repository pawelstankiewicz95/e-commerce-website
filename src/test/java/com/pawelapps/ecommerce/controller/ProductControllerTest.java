package com.pawelapps.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawelapps.ecommerce.configuration.SecurityConfiguration;
import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.service.ProductService;
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

@WebMvcTest(ProductController.class)
@Import(SecurityConfiguration.class)
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ProductService productService;

    @Autowired
    ObjectMapper objectMapper;

    Product product;


    @Test
    @DisplayName("when getting all products")
    void getAllProductsTest() throws Exception {

        List<Product> productList = Arrays.asList(
                Product.builder()
                        .sku("123")
                        .name("Product 1")
                        .description("Description 1")
                        .build(),
                Product.builder()
                        .sku("456")
                        .name("Product 2")
                        .description("Description 2")
                        .build()
        );

        when(productService.getAllProducts()).thenReturn(productList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Product 1")))
                .andExpect(jsonPath("$[0].description", is("Description 1")))
                .andExpect(jsonPath("$[1].name", is("Product 2")))
                .andExpect(jsonPath("$[1].description", is("Description 2")));

        verify(productService, times(1)).getAllProducts();
    }

    @Nested
    @DisplayName("When creating product")
    class CreateProductTest {

        @BeforeEach
        void setUp() {
            product = Product.builder()
                    .sku("333222")
                    .name("New TestCup")
                    .description("Just new testing cup")
                    .build();
        }

        @Test
        @DisplayName("When anonymous wants to access")
        void createProductAsAnonymous() throws Exception {
            when(productService.createProduct(any(Product.class))).thenReturn(product);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product)))
                    .andExpect(status().isForbidden());

            verify(productService, times(0)).updateProduct(any(Product.class));
        }

        @Test
        @DisplayName("When unauthorized user wants to access")
        @WithMockUser(authorities = "user")
        void createProductAsUnauthorizedUser() throws Exception {
            when(productService.createProduct(any(Product.class))).thenReturn(product);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product)))
                    .andExpect(status().isForbidden());

            verify(productService, times(0)).updateProduct(any(Product.class));
        }

        @Test
        @DisplayName("When authorized user wants to access")
        @WithMockUser(authorities = "admin")
        void createProductAsAuthorizedUser() throws Exception {
            when(productService.createProduct(any(Product.class))).thenReturn(product);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath(("$.sku"), is("333222")));

            verify(productService, times(1)).createProduct(any(Product.class));

        }
    }

    @Nested
    @DisplayName("When updating product")
    class UpdateProductTests {

        @BeforeEach
        void setUp() {
            product = Product.builder()
                    .id(123L)
                    .sku("333222")
                    .name("Product 1")
                    .description("Description 1")
                    .build();

        }

        @Test
        @DisplayName("When anonymous user wants to access")
        void updateProductTestAsAnonymous() throws Exception {
            when(productService.updateProduct(any(Product.class))).thenReturn(product);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product)))
                    .andExpect(status().isForbidden());
            verify(productService, times(0)).updateProduct(any(Product.class));
        }

        @Test
        @DisplayName("When unauthorized user wants to access")
        @WithMockUser(authorities = "user")
        void updateProductTestAsUnauthorized() throws Exception {
            when(productService.updateProduct(any(Product.class))).thenReturn(product);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product)))
                    .andExpect(status().isForbidden());
            verify(productService, times(0)).updateProduct(any(Product.class));
        }

        @Test
        @DisplayName("When authorized user wants to access")
        @WithMockUser(authorities = "admin")
        void updateProductTestAsAuthorized() throws Exception {
            when(productService.updateProduct(any(Product.class))).thenReturn(product);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(123)))
                    .andExpect(jsonPath("$.sku", is("333222")))
                    .andExpect(jsonPath("$.name", is("Product 1")))
                    .andExpect(jsonPath("$.description", is("Description 1")));
            verify(productService, times(1)).updateProduct(any(Product.class));
        }
    }

    @Test
    @DisplayName("when getting by name or sku")
    void getProductsByNameOrSkuTest() throws Exception {
        List<Product> testProducts = Arrays.asList(
                Product.builder().id(1L).name("Product 1").sku("123").build(),
                Product.builder().id(2L).name("Product 2").sku("456").build()
        );

        when(productService.getProductsByNameOrSku("Second")).thenReturn(testProducts);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/products-by-name-or-sku/{nameOrSku}", "Second"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Product 1")))
                .andExpect(jsonPath("$[1].name", is("Product 2")));

        verify(productService, times(1)).getProductsByNameOrSku("Second");
    }

    @Test
    void getProductByIdTest() throws Exception {
        product = Product.builder()
                .id(123L)
                .sku("123")
                .name("Product 1")
                .description("Description 1")
                .build();

        when(productService.getProductById(123L)).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{id}", 123))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                //  .andExpect(jsonPath("$.id", 123))
                .andExpect(jsonPath("$.sku", is("123")))
                .andExpect(jsonPath("$.name", is("Product 1")))
                .andExpect(jsonPath("$.description", is("Description 1")));

        verify(productService, times(1)).getProductById(123L);

    }
}
