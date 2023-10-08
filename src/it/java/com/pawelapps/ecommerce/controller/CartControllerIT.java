package com.pawelapps.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.entity.*;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
public class CartControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private final String uri = "/api/cart";

    private final String authorizedUserEmail = "authorized@email.com";
    private final String unauthorizedUserEmail = "unauthorized@email.com";

    private ProductCategory productCategory;

    private Product product1;
    private Product product2;

    private CartProduct cartProduct1;
    private CartProduct cartProduct2;
    List<CartProduct> cartProducts;

    private User user;

    private Cart cart;

    @BeforeEach
    void setUp() {
        user = User.builder().email(authorizedUserEmail).build();

        productCategory = ProductCategory.builder().categoryName("Cart Product Tests Category").build();
        entityManager.persist(productCategory);
        entityManager.flush();

        product1 = Product.builder().productCategory(productCategory).name("Test Product 1").unitsInStock(10).build();
        product2 = Product.builder().productCategory(productCategory).name("Test Product 2").unitsInStock(1).build();
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.flush();

        cartProduct1 = CartProduct.builder().product(product1).name(product1.getName()).quantity(5)
                .build();
        cartProduct2 = CartProduct.builder().product(product2).name(product2.getName()).quantity(1)
                .build();

        cartProducts = new ArrayList<>();
        cartProducts.add(cartProduct1);
        cartProducts.add(cartProduct2);

        cart = Cart.builder().user(user).cartProducts(cartProducts).build();
        entityManager.persist(cart);
        entityManager.flush();
    }

    @Nested
    class GetCartTests {

        private void testUnauthorizedAccess() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(uri + "/" + authorizedUserEmail)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(authorizedUserEmail)
        void shouldReturnCartForAuthorizedUser() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(uri + "/" + authorizedUserEmail)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.user.email").value(authorizedUserEmail))
                    .andExpect(jsonPath("$.cartProducts", hasSize(2)))
                    .andExpect(jsonPath("$.cartProducts.[0].name").value(cartProduct1.getName()));

        }

        @Test
        @WithMockUser(unauthorizedUserEmail)
        void shouldNotReturnCartForUnauthorizedUser() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(uri + "/" + authorizedUserEmail)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithAnonymousUser
        void shouldNotReturnCartForAnonymousUser() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(uri + "/" + authorizedUserEmail)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }
    }
}
