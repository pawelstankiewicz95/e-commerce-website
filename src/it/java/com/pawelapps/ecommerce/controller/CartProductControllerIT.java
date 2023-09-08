package com.pawelapps.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.dto.CartProductDto;
import com.pawelapps.ecommerce.entity.Cart;
import com.pawelapps.ecommerce.entity.CartProduct;
import com.pawelapps.ecommerce.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jdk.jfr.ContentType;
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

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
public class CartProductControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private final String uri = "/api/cart-products";

    private final String authorizedUser = "authorized@example.com";
    private final String unauthorizedUser = "unauthorized@example.com";

    private CartProduct getCartProductFromDB(String name) {
        CartProduct cartProductFromDB;
        TypedQuery<CartProduct> query = entityManager.createQuery(
                "SELECT cp FROM CartProduct cp WHERE cp.name = :name", CartProduct.class);
        query.setParameter("name", name);
        List<CartProduct> cartProducts = query.getResultList();

        if (cartProducts.size() == 1) {
            cartProductFromDB = cartProducts.get(0);
        } else if (cartProducts.size() > 1) {
            fail("Should be tested with unique cart product name");
            cartProductFromDB = null;
        } else {
            cartProductFromDB = null;
        }

        entityManager.clear();

        return cartProductFromDB;
    }

    @Nested
    class TestsWithNoSetUp {

        @Nested
        class saveCartProductTests {
            private CartProductDto cartProductDto1 = CartProductDto.builder().id(1L).name("Test Cart Product 1").quantity(1)
                    .build();

            private void testUnauthorizedSave() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.post(uri + "/" + authorizedUser)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cartProductDto1)))
                        .andExpect(status().isForbidden());

                CartProduct cartProductFromDB = getCartProductFromDB("Test Cart Product 1");
                assertNull(cartProductFromDB, "Cart product should not be created");
            }

            @Test
            @WithMockUser(unauthorizedUser)
            void shouldNotSaveCartProductForUnauthorizedUser() throws Exception {
                testUnauthorizedSave();
            }

            @Test
            @WithAnonymousUser
            void shouldNotSaveCartProductForAnonymousUser() throws Exception {
                testUnauthorizedSave();
            }

            @Test
            @WithMockUser(username = authorizedUser)
            void shouldSaveCartProduct() throws Exception {
                CartProductDto cartProductDto1 = CartProductDto.builder().id(1L).name("Test Cart Product 1").quantity(1)
                        .build();
                mockMvc.perform(MockMvcRequestBuilders.post(uri + "/" + authorizedUser)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cartProductDto1)))
                        .andExpect(status().isCreated());
                CartProduct cartProductFromDB = getCartProductFromDB("Test Cart Product 1");
                assertEquals("Test Cart Product 1", cartProductFromDB.getName(), "Saved cart product should exist in database");

                Cart createdCart = cartProductFromDB.getCart();
                assertNotNull(createdCart, "Cart should be created");

                User savedUser = cartProductFromDB.getCart().getUser();
                assertNotNull(savedUser, "User should be saved to database");

                String userEmail = savedUser.getEmail();
                assertEquals(authorizedUser, userEmail, "Logged user should be cart owner");
            }
        }
    }

    @Nested
    class TestsWithSetUp {

        private CartProduct cartProduct1;

        private CartProduct cartProduct2;

        private User user;

        private Cart cart;

        @BeforeEach
        void setUpDataBase() {
            cartProduct1 = CartProduct.builder().productId(1L).name("Test Cart Product 1").quantity(1)
                    .build();
            cartProduct2 = CartProduct.builder().productId(2L).name("Test Cart Product 2").quantity(1)
                    .build();
            user = User.builder().email(authorizedUser).build();
            cart = Cart.builder().user(user).build();
            cart.addCartProduct(cartProduct1);
            cart.addCartProduct(cartProduct2);
            entityManager.persist(cart);
            entityManager.flush();
            entityManager.clear();
        }

        @Nested
        class GetCartProductsByUserEmailTests {


            private void testUnauthorizedGetAttempt() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.get(uri + "/" + authorizedUser)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden());

            }

            @Test
            @WithAnonymousUser
            void shouldNotGetCartProductForAnonymousUser() throws Exception {
                testUnauthorizedGetAttempt();
            }

            @Test
            @WithMockUser(unauthorizedUser)
            void shouldNotGetCartProductForUnauthorizedUser() throws Exception {
                testUnauthorizedGetAttempt();
            }

            @Test
            @WithMockUser(authorizedUser)
            void shouldGetCartProductsForAuthorizedUser() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.get(uri + "/" + authorizedUser)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(greaterThan(0))));
            }
        }

        @Nested
        class IncreaseCartProductQuantityByOneTests {

            private void testUnauthorizedIncreaseAttempt() {

            }

            @WithAnonymousUser
            void shouldNotIncreaseQuantityForAnonymousUser() {

            }

            @WithMockUser(unauthorizedUser)
            void shouldNotIncreaseQuantityForUnauthorizedUser() {

            }

            @WithMockUser(authorizedUser)
            void shouldIncreaseQuantityAuthorizedUser() {

            }
        }

        @Nested
        class DecreaseCartProductQuantityByOneTests {

            private void testUnauthorizedDecreaseAttempt() {

            }

            @WithAnonymousUser
            void shouldNotDecreaseQuantityForAnonymousUser() {

            }

            @WithMockUser(unauthorizedUser)
            void shouldNotDecreaseQuantityForUnauthorizedUser() {

            }

            @WithMockUser(authorizedUser)
            void shouldDecreaseQuantityAuthorizedUser() {

            }
        }

        @Nested
        class DeleteAllCartProductsByUserEmailTests {

            private void testUnauthorizedDeleteAttempt() {

            }

            @WithAnonymousUser
            void shouldNotDeleteProductsForAnonymousUser() {

            }

            @WithMockUser(unauthorizedUser)
            void shouldNotDeleteProductsForUnauthorizedUser() {

            }

            @WithMockUser(authorizedUser)
            void shouldDeleteProductsForAuthorizedUser() {

            }
        }

        @Nested
        class DeleteCartProductByUserEmailAndProductIdTests {

            private void testUnauthorizedDeleteAttempt() {

            }

            @WithAnonymousUser
            void shouldNotDeleteProductForAnonymousUser() {

            }

            @WithMockUser(unauthorizedUser)
            void shouldNotDeleteProductForUnauthorizedUser() {

            }

            @WithMockUser(authorizedUser)
            void shouldDeleteProductForAuthorizedUser() {

            }
        }
    }
}

