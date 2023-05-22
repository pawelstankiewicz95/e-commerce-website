package com.pawelapps.ecommerce.dao;

import com.pawelapps.ecommerce.entity.Cart;
import com.pawelapps.ecommerce.entity.CartProduct;
import com.pawelapps.ecommerce.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestPropertySource("/test-application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CartProductRepositoryTest {

    @Autowired
    EntityManager entityManager;
    @Autowired
    CartProductRepository cartProductRepository;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("test@email.com");
        entityManager.persist(user);

        Cart cart = new Cart();
        cart.setUser(user);
        entityManager.persist(cart);

        CartProduct cartProduct1 = new CartProduct();
        cartProduct1.setCart(cart);
        entityManager.persist(cartProduct1);

        CartProduct cartProduct2 = new CartProduct();
        cartProduct2.setCart(cart);
        entityManager.persist(cartProduct2);

        entityManager.flush();
    }

    @Test
    void testFindCartProductsByUserEmail(){
        String userEmail = "test@email.com";
        List<CartProduct> cartProducts = cartProductRepository.findCartProductsByUserEmail(userEmail);
        assertEquals(2, cartProducts.size());
    }
}
