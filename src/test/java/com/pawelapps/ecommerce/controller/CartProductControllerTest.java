package com.pawelapps.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawelapps.ecommerce.dto.CartProductDto;
import com.pawelapps.ecommerce.entity.CartProduct;
import com.pawelapps.ecommerce.service.CartProductService;
import org.junit.jupiter.api.BeforeEach;
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
@AutoConfigureMockMvc
public class CartProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CartProductService cartProductService;

    final String cartOwnerEmail = "owner@example.com";
    final String notCartOwnerEmail = "notowner@example.com";

    @Nested
    class GetCartProductsByUserEmailTest {

        List<CartProduct> cartProductsList;

        @BeforeEach
        void setUp() {
            cartProductsList = Arrays.asList(
                    CartProduct.builder().name("Cart product 1").build(),
                    CartProduct.builder().name("Cart product 2").build()
            );
        }

        private void testForbiddenAccessWhenGettingCartProducts() throws Exception {
            when(cartProductService.findCartProductsByUserEmail(cartOwnerEmail)).thenReturn(cartProductsList);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/cart-products/" + cartOwnerEmail))
                    .andExpect(status().isForbidden());
            verify(cartProductService, times(0)).findCartProductsByUserEmail(cartOwnerEmail);
        }

        @Test
        @WithMockUser(username = cartOwnerEmail, authorities = "Everyone")
        void shouldReturnCartProductsForCartOwner() throws Exception {
            when(cartProductService.findCartProductsByUserEmail(cartOwnerEmail)).thenReturn(cartProductsList);

            mockMvc.perform(MockMvcRequestBuilders.get("/api/cart-products/owner@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("Cart product 1")))
                    .andExpect(jsonPath("$[1].name", is("Cart product 2")));

            verify(cartProductService, times(1)).findCartProductsByUserEmail(cartOwnerEmail);
        }

        @Test
        @WithMockUser(username = notCartOwnerEmail, authorities = "Everyone")
        void shouldReturnForbiddenForNotOwner() throws Exception {
            testForbiddenAccessWhenGettingCartProducts();
        }

        @Test
        @WithAnonymousUser
        void shouldReturnForbiddenForAnonymousUser() throws Exception {
            testForbiddenAccessWhenGettingCartProducts();
        }
    }

    @Nested
    class SaveCartProductTest {

        CartProductDto cartProductDto;
        CartProduct cartProduct;

        @BeforeEach
        void setUp() {
            cartProductDto = CartProductDto.builder().name("Cart Product 1").quantity(10).build();
            cartProduct = CartProduct.builder().name(cartProductDto.getName()).quantity(cartProductDto.getQuantity()).build();
        }

        private void testForbiddenAccessWhenSavingCartProduct() throws Exception {
            when(cartProductService.saveCartProductToCart(any(CartProductDto.class), eq(cartOwnerEmail))).thenReturn(cartProduct);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/cart-products/" + cartOwnerEmail)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cartProductDto)))
                    .andExpect(status().isForbidden());
            verify(cartProductService, times(0)).saveCartProductToCart(any(CartProductDto.class), eq(cartOwnerEmail));
        }

        @Test
        @WithMockUser(username = cartOwnerEmail)
        void shouldSaveCartProductForCartOwner() throws Exception {
            when(cartProductService.saveCartProductToCart(any(CartProductDto.class), eq(cartOwnerEmail))).thenReturn(cartProduct);

            mockMvc.perform(MockMvcRequestBuilders.post("/api/cart-products/" + cartOwnerEmail)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cartProductDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is("Cart Product 1")));
            verify(cartProductService, times(1)).saveCartProductToCart(any(CartProductDto.class), eq(cartOwnerEmail));
        }

        @Test
        @WithMockUser(username = notCartOwnerEmail)
        void shouldReturnForbiddenForNotOwner() throws Exception {
            testForbiddenAccessWhenSavingCartProduct();
        }

        @Test
        @WithMockUser(username = notCartOwnerEmail)
        void shouldReturnForbiddenForAnonymousUser() throws Exception {
            testForbiddenAccessWhenSavingCartProduct();
        }
    }

    @Nested
    class IncreaseCartProductQuantityByOneTest {
        Long cartProductId = 123L;

        private void testForbiddenAccessWhenIncreasingCartProductQuantity() throws Exception {
            when(cartProductService.increaseCartProductQuantityByOne(eq(cartOwnerEmail), eq(cartProductId))).thenReturn(1);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/cart-products/increase/{userEmail}/{productId}", cartOwnerEmail, cartProductId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(cartProductService, times(0)).increaseCartProductQuantityByOne(eq(cartOwnerEmail), eq(cartProductId));
        }

        @Test
        @WithMockUser(username = cartOwnerEmail)
        void shouldIncreaseCartProductQuantityForCartOwner() throws Exception {
            when(cartProductService.increaseCartProductQuantityByOne(eq(cartOwnerEmail), eq(cartProductId))).thenReturn(1);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/cart-products/increase/{userEmail}/{productId}", cartOwnerEmail, cartProductId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("1"));

            verify(cartProductService, times(1)).increaseCartProductQuantityByOne(eq(cartOwnerEmail), eq(cartProductId));
        }

        @Test
        @WithMockUser(username = notCartOwnerEmail)
        void shouldReturnForbiddenForNotOwner() throws Exception {
            testForbiddenAccessWhenIncreasingCartProductQuantity();
        }

        @Test
        @WithAnonymousUser
        void shouldReturnForbiddenForAnonymousUser() throws Exception {
            testForbiddenAccessWhenIncreasingCartProductQuantity();
        }
    }

    @Nested
    class DecreaseCartProductQuantityByOneTest {
        Long cartProductId = 123L;

        private void testForbiddenAccessWhenIncreasingCartProductQuantity() throws Exception {
            when(cartProductService.decreaseCartProductQuantityByOne(eq(cartOwnerEmail), eq(cartProductId))).thenReturn(1);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/cart-products/decrease/{userEmail}/{productId}", cartOwnerEmail, cartProductId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

            verify(cartProductService, times(0)).decreaseCartProductQuantityByOne(eq(cartOwnerEmail), eq(cartProductId));
        }

        @Test
        @WithMockUser(username = cartOwnerEmail)
        void shouldDecreaseCartProductQuantityForCartOwner() throws Exception {
            when(cartProductService.decreaseCartProductQuantityByOne(eq(cartOwnerEmail), eq(cartProductId))).thenReturn(1);

            mockMvc.perform(MockMvcRequestBuilders.put("/api/cart-products/decrease/{userEmail}/{productId}", cartOwnerEmail, cartProductId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string("1"));

            verify(cartProductService, times(1)).decreaseCartProductQuantityByOne(eq(cartOwnerEmail), eq(cartProductId));
        }

        @Test
        @WithMockUser(username = notCartOwnerEmail)
        void shouldReturnForbiddenForNotOwner() throws Exception {
            testForbiddenAccessWhenIncreasingCartProductQuantity();
        }

        @Test
        @WithAnonymousUser
        void shouldReturnForbiddenForAnonymousUser() throws Exception {
            testForbiddenAccessWhenIncreasingCartProductQuantity();
        }
    }

    @Nested
    class DeleteAllCartProductsByUserEmailTest {
        Long cartProductId = 123L;

        private void testForbiddenAccessForDeletingAllCartProducts() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/cart-products/{userEmail}", cartOwnerEmail))
                    .andExpect(status().isForbidden());
            verify(cartProductService, times(0)).deleteAllCartProductsByUserEmail(eq(cartOwnerEmail));
        }

        @Test
        @WithMockUser(username = cartOwnerEmail)
        void shouldDeleteAllProductsForCartOwner() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/cart-products/{userEmail}", cartOwnerEmail))
                    .andExpect(status().isOk());
            verify(cartProductService, times(1)).deleteAllCartProductsByUserEmail(eq(cartOwnerEmail));
        }

        @Test
        @WithMockUser(username = notCartOwnerEmail)
        void shouldReturnForbiddenForNotCartOwner() throws Exception {
            testForbiddenAccessForDeletingAllCartProducts();
        }

        @Test
        @WithAnonymousUser
        void shouldReturnForbiddenForAnonymousUser() throws Exception {
            testForbiddenAccessForDeletingAllCartProducts();
        }
    }

    @Nested
    class DeleteCartProductByUserEmailAndProductIdTest {
        Long cartProductId = 123L;

        private void testForbiddenAccessForDeleteCartProduct() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/cart-products/{userEmail}/{productId}", cartOwnerEmail, cartProductId))
                    .andExpect(status().isForbidden());
            verify(cartProductService, times(0)).deleteCartProduct(eq(cartOwnerEmail), eq(cartProductId));
        }

        @Test
        @WithMockUser(username = cartOwnerEmail)
        void shouldDeleteCartProductForCartOwner() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/cart-products/{userEmail}/{productId}", cartOwnerEmail, cartProductId))
                    .andExpect(status().isOk());
            verify(cartProductService, times(1)).deleteCartProduct(eq(cartOwnerEmail), eq(cartProductId));
        }

        @Test
        @WithMockUser(username = notCartOwnerEmail)
        void shouldReturnForbiddenForNotCartOwner() throws Exception {
            testForbiddenAccessForDeleteCartProduct();
        }

        @Test
        @WithAnonymousUser
        void shouldReturnForbiddenForAnonymousUser() throws Exception {
            testForbiddenAccessForDeleteCartProduct();
        }
    }
}
