package com.pawelapps.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawelapps.ecommerce.entity.CartProduct;
import com.pawelapps.ecommerce.service.CartProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
//@Import(SecurityConfiguration.class)
@AutoConfigureMockMvc
public class CartProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CartProductService cartProductService;

    @Nested
    @DisplayName("When getting cart products")
    class getCartProductTest {
        @Test
        @DisplayName("When cart owner trying to access")
        @WithMockUser(username = "owner@example.com", authorities = "Everyone")
        void getCartProductsByCartOwnerTest() throws Exception {
            List<CartProduct> cartProductsList = Arrays.asList(
                    CartProduct.builder().name("Cart product 1").build(),
                    CartProduct.builder().name("Cart product 2").build()
            );

            when(cartProductService.findCartProductsByUserEmail("owner@example.com")).thenReturn(cartProductsList);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/cart-products/owner@example.com"))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("Cart product 1")))
                    .andExpect(jsonPath("$[1].name", is("Cart product 2")));

            verify(cartProductService, times(1)).findCartProductsByUserEmail("owner@example.com");
        }

        @Test
        @DisplayName("When not owner trying to access")
        @WithMockUser(username = "notowner@example.com", authorities = "Everyone")
        void getCartProductsByNotOwnerTest() throws Exception {

            List<CartProduct> cartProductsList = Arrays.asList(
                    CartProduct.builder().name("Cart product 1").build(),
                    CartProduct.builder().name("Cart product 2").build()
            );

            when(cartProductService.findCartProductsByUserEmail("owner@example.com")).thenReturn(cartProductsList);


            mockMvc.perform(MockMvcRequestBuilders.get("/api/cart-products/owner@example.com"))
                    .andExpect(status().isForbidden());
            verify(cartProductService, times(0)).findCartProductsByUserEmail("owner@example.com");
        }

        @Test
        @DisplayName("When anonymous trying to access")
        @WithAnonymousUser
        void getCartProductsAnonymousTest() throws Exception {

            List<CartProduct> cartProductsList = Arrays.asList(
                    CartProduct.builder().name("Cart product 1").build(),
                    CartProduct.builder().name("Cart product 2").build()
            );

            when(cartProductService.findCartProductsByUserEmail("owner@example.com")).thenReturn(cartProductsList);


            mockMvc.perform(MockMvcRequestBuilders.get("/api/cart-products/owner@example.com"))
                    .andExpect(status().isForbidden());
            verify(cartProductService, times(0)).findCartProductsByUserEmail("owner@example.com");
        }
    }

}
