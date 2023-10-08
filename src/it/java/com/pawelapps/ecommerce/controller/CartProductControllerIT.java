package com.pawelapps.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.dto.CartProductDto;
import com.pawelapps.ecommerce.entity.*;
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

    private Product product1;
    private Product product2;
    private ProductCategory productCategory;

    private CartProduct getCartProductFromDB(Long id) {
        CartProduct cartProductFromDB;
        TypedQuery<CartProduct> query = entityManager.createQuery(
                "SELECT cp FROM CartProduct cp WHERE cp.id = :id", CartProduct.class);
        query.setParameter("id", id);
        try {
            cartProductFromDB = query.getSingleResult();
        } catch (NoResultException noResultException) {
            cartProductFromDB = null;
        }

        return cartProductFromDB;
    }


    @BeforeEach
    void setUp() {
        productCategory = ProductCategory.builder().categoryName("Cart Product Tests Category").build();
        entityManager.persist(productCategory);
        entityManager.flush();
        product1 = Product.builder().productCategory(productCategory).name("Test Product 1").unitsInStock(10).build();
        product2 = Product.builder().productCategory(productCategory).name("Test Product 2").unitsInStock(1).build();
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.flush();
    }

    @Nested
    class TestsWithNoCartSetUp {

        @Nested
        class saveCartProductTests {
            private CartProductDto cartProductDto1;

            @BeforeEach
            void setUp() {
                cartProductDto1 = CartProductDto.builder().product(product1).name(product1.getName()).quantity(1)
                        .build();
            }


            private void testUnauthorizedSave() throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.post(uri + "/" + authorizedUser)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cartProductDto1)))
                        .andExpect(status().isForbidden());

                assertNull(cartProductDto1.getCartProductId());
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
                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri + "/" + authorizedUser)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(cartProductDto1)))
                        .andExpect(status().isCreated()).andReturn();

                String content = result.getResponse().getContentAsString();
                Long id = JsonPath.parse(content).read("$.cartProductId", Long.class);

                CartProduct cartProductFromDB = getCartProductFromDB(id);
                assertEquals("Test Product 1", cartProductFromDB.getName(), "Saved cart product should exist in database");

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
            cartProduct1 = CartProduct.builder().product(product1).name(product1.getName()).quantity(5)
                    .build();
            cartProduct2 = CartProduct.builder().product(product2).name(product2.getName()).quantity(1)
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

            private void testUnauthorizedIncreaseAttempt() throws Exception {
                Long cartProduct1Id = cartProduct1.getCartProductId();
                assertNotNull(cartProduct1Id);
                CartProduct initialCartProduct = getCartProductFromDB(cartProduct1Id);
                int initialCartProductQuantity = initialCartProduct.getQuantity();

                entityManager.clear();

                mockMvc.perform(MockMvcRequestBuilders.put(uri + "/increase/" + authorizedUser + "/" + cartProduct1Id)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden());

                CartProduct cartProductAfterUpdateAttempt = getCartProductFromDB(cartProduct1Id);
                int cartProductQuantityAfterUpdateAttempt = cartProductAfterUpdateAttempt.getQuantity();

                assertEquals(initialCartProductQuantity, cartProductQuantityAfterUpdateAttempt, "Should not increase cart product quantity");
            }

            @Test
            @WithAnonymousUser
            void shouldNotIncreaseQuantityForAnonymousUser() throws Exception {
                testUnauthorizedIncreaseAttempt();
            }

            @Test
            @WithMockUser(unauthorizedUser)
            void shouldNotIncreaseQuantityForUnauthorizedUser() throws Exception {
                testUnauthorizedIncreaseAttempt();
            }

            @Test
            @WithMockUser(authorizedUser)
            void shouldIncreaseQuantityAuthorizedUser() throws Exception {
                Long cartProduct1Id = cartProduct1.getCartProductId();
                CartProduct initialCartProduct = getCartProductFromDB(cartProduct1Id);
                int initialCartProductQuantity = initialCartProduct.getQuantity();

           //     entityManager.clear();

                mockMvc.perform(MockMvcRequestBuilders.put(uri + "/increase/" + authorizedUser + "/" + cartProduct1Id)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$").value("1"));

              entityManager.clear();

                CartProduct cartProductAfterUpdateAttempt = getCartProductFromDB(cartProduct1Id);
                int cartProductQuantityAfterUpdateAttempt = cartProductAfterUpdateAttempt.getQuantity();

                assertEquals(initialCartProductQuantity + 1, cartProductQuantityAfterUpdateAttempt);
            }
        }

        @Nested
        class DecreaseCartProductQuantityByOneTests {

            private void testUnauthorizedDecreaseAttempt() throws Exception {
                Long cartProduct1Id = cartProduct1.getCartProductId();
                CartProduct initialCartProduct = getCartProductFromDB(cartProduct1Id);
                int initialCartProductQuantity = initialCartProduct.getQuantity();

                mockMvc.perform(MockMvcRequestBuilders.put(uri + "/decrease/" + authorizedUser + "/" + cartProduct1Id)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden());

                entityManager.clear();

                CartProduct cartProductAfterUpdateAttempt = getCartProductFromDB(cartProduct1Id);
                int cartProductQuantityAfterUpdateAttempt = cartProductAfterUpdateAttempt.getQuantity();

                assertEquals(initialCartProductQuantity, cartProductQuantityAfterUpdateAttempt);
            }

            @Test
            @WithAnonymousUser
            void shouldNotDecreaseQuantityForAnonymousUser() throws Exception {
                testUnauthorizedDecreaseAttempt();
            }

            @Test
            @WithMockUser(unauthorizedUser)
            void shouldNotDecreaseQuantityForUnauthorizedUser() throws Exception {
                testUnauthorizedDecreaseAttempt();
            }

            @Test
            @WithMockUser(authorizedUser)
            void shouldDecreaseQuantityAuthorizedUser() throws Exception {
                Long cartProduct1Id = cartProduct1.getCartProductId();
                CartProduct initialCartProduct = getCartProductFromDB(cartProduct1Id);
                int initialCartProductQuantity = initialCartProduct.getQuantity();

                mockMvc.perform(MockMvcRequestBuilders.put(uri + "/decrease/" + authorizedUser + "/" + cartProduct1Id)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

                entityManager.clear();

                CartProduct cartProductAfterUpdateAttempt = getCartProductFromDB(cartProduct1Id);
                int cartProductQuantityAfterUpdateAttempt = cartProductAfterUpdateAttempt.getQuantity();

                assertEquals(initialCartProductQuantity - 1, cartProductQuantityAfterUpdateAttempt);
            }
        }

        @Nested
        class DeleteAllCartProductsByUserEmailTests {

            private List<CartProduct> getAllCartProductsByUser(String userEmail) {
                TypedQuery<CartProduct> getAllCartProductsQuery = entityManager
                        .createQuery("SELECT cp FROM CartProduct cp JOIN cp.cart c JOIN c.user u WHERE u.email = :email", CartProduct.class);
                getAllCartProductsQuery.setParameter("email", userEmail);

                List<CartProduct> cartProducts = getAllCartProductsQuery.getResultList();

                return cartProducts;
            }

            private void testUnauthorizedDeleteAttempt() throws Exception {

                List<CartProduct> cartProducts = getAllCartProductsByUser(authorizedUser);
                assertEquals(2, cartProducts.size());

                entityManager.clear();

                mockMvc.perform(MockMvcRequestBuilders.delete(uri + "/" + authorizedUser))
                        .andExpect(status().isForbidden());

                List<CartProduct> cartProductsAfterDeleteAttempt = getAllCartProductsByUser(authorizedUser);
                assertEquals(2, cartProductsAfterDeleteAttempt.size());
            }

            @Test
            @WithAnonymousUser
            void shouldNotDeleteProductsForAnonymousUser() throws Exception {
                testUnauthorizedDeleteAttempt();
            }

            @Test
            @WithMockUser(unauthorizedUser)
            void shouldNotDeleteProductsForUnauthorizedUser() throws Exception {
                testUnauthorizedDeleteAttempt();
            }

            @Test
            @WithMockUser(authorizedUser)
            void shouldDeleteProductsForAuthorizedUser() throws Exception {
                List<CartProduct> cartProducts = getAllCartProductsByUser(authorizedUser);
                assertEquals(2, cartProducts.size());

                entityManager.clear();

                mockMvc.perform(MockMvcRequestBuilders.delete(uri + "/" + authorizedUser))
                        .andExpect(status().isOk());

                List<CartProduct> cartProductsAfterDeleteAttempt = getAllCartProductsByUser(authorizedUser);
                assertEquals(0, cartProductsAfterDeleteAttempt.size());
            }
        }

        @Nested
        class DeleteCartProductByUserEmailAndProductIdTests {

            private void testUnauthorizedDeleteAttempt() throws Exception {
                Long cartProduct1Id = cartProduct1.getCartProductId();
                CartProduct initialCartProduct = getCartProductFromDB(cartProduct1Id);
                assertNotNull(initialCartProduct);

                entityManager.clear();

                mockMvc.perform(MockMvcRequestBuilders.delete(uri + "/" + authorizedUser + "/" + cartProduct1Id)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden());

                CartProduct cartProductAfterDeleteAttempt = getCartProductFromDB(cartProduct1Id);
                assertNotNull(cartProductAfterDeleteAttempt);
            }

            @Test
            @WithAnonymousUser
            void shouldNotDeleteProductForAnonymousUser() throws Exception {
                testUnauthorizedDeleteAttempt();
            }

            @Test
            @WithMockUser(unauthorizedUser)
            void shouldNotDeleteProductForUnauthorizedUser() throws Exception {
                testUnauthorizedDeleteAttempt();
            }

            @Test
            @WithMockUser(authorizedUser)
            void shouldDeleteProductForAuthorizedUser() throws Exception {
                Long cartProduct1Id = cartProduct1.getCartProductId();
                CartProduct initialCartProduct = getCartProductFromDB(cartProduct1Id);
                assertNotNull(initialCartProduct);

                entityManager.clear();

                mockMvc.perform(MockMvcRequestBuilders.delete(uri + "/" + authorizedUser + "/" + cartProduct1Id)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

                CartProduct cartProductAfterDeleteAttempt = getCartProductFromDB(cartProduct1Id);
                assertNull(cartProductAfterDeleteAttempt);
            }
        }
    }
}

