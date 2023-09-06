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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

    private CartProduct cartProduct1;
    private CartProduct cartProduct2;

    private User user;

    private Cart cart;

    private final String uri = "/api/cart-products";

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
        final String username = "testuser@example.com";

        @Test
        @WithMockUser(username = username)
        void shouldSaveCartProduct() throws Exception {
            CartProductDto cartProductDto1 = CartProductDto.builder().id(1L).name("Test Cart Product 1").quantity(1)
                    .build();
            mockMvc.perform(MockMvcRequestBuilders.post(uri + "/" + username)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cartProductDto1)))
                    .andExpect(status().isCreated());
            CartProduct cartProductFromDB = getCartProductFromDB("Test Cart Product 1");
            assertEquals("Test Cart Product 1", cartProductFromDB.getName(), "Saved cart product should exist in database");

            Cart createdCart = cartProductFromDB.getCart();
            assertNotNull(createdCart, "Cart should be created");

            User savedUser =  cartProductFromDB.getCart().getUser();
            assertNotNull(savedUser, "User should be saved to database");

            String userEmail = savedUser.getEmail();
            assertEquals(username, userEmail, "Logged user should be cart owner");
        }

    }

}

