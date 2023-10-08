package com.pawelapps.ecommerce.dao;

import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Transactional
public class CartRepositoryIT extends BaseIT {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CartRepository cartRepository;

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

    private Cart getCartFromDB(Long id) {
        Cart cart;
        TypedQuery<Cart> query = entityManager.createQuery(
                "SELECT c FROM Cart c WHERE c.id = :id", Cart.class);
        query.setParameter("id", id);

        try {
            cart = query.getSingleResult();
        } catch (NoResultException noResultException) {
            cart = null;
        }

        //   entityManager.clear();

        return cart;
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

        cartProduct1 = CartProduct.builder().product(product1).name(product1.getName()).quantity(5)
                .build();
        cartProduct2 = CartProduct.builder().product(product2).name(product2.getName()).quantity(1)
                .build();

        cartProducts = new ArrayList<>();
        cartProducts.add(cartProduct1);
        cartProducts.add(cartProduct2);

        user = User.builder().email(authorizedUserEmail).build();
        cart = Cart.builder().user(user).cartProducts(cartProducts).build();
        user.setCart(cart);

        entityManager.persist(cart);
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void shouldFindCartByUserEmail() {
        Cart cartFromDB = cartRepository.findByUserEmail(authorizedUserEmail);

        assertNotNull(cartFromDB);

        assertEquals(this.cart.getId(), cartFromDB.getId());
        assertEquals(this.cart.getUser().getEmail(), cartFromDB.getUser().getEmail());
        assertEquals(this.cart.getCartProducts().size(), cartFromDB.getCartProducts().size());
    }
}