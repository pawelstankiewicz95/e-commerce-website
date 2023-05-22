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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource("/test-application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CartProductRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    CartProductRepository cartProductRepository;

    CartProduct cartProduct1;
    CartProduct cartProduct2;
    Cart cart;
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@email.com");
        entityManager.persist(user);

        cart = new Cart();
        cart.setUser(user);
        entityManager.persist(cart);

        cartProduct1 = new CartProduct();
        cartProduct1.setId(1L);
        cartProduct1.setCart(cart);
        cartProduct1.setQuantity(2);
        entityManager.persist(cartProduct1);

        cartProduct2 = new CartProduct();
        cartProduct2.setId(2L);
        cartProduct2.setCart(cart);
        cartProduct2.setQuantity(2);
        entityManager.persist(cartProduct2);

        entityManager.flush();
    }

    @Test
    void testFindCartProductsByUserEmail() {
        String userEmail = "test@email.com";

        List<CartProduct> cartProducts = cartProductRepository.findCartProductsByUserEmail(userEmail);

        assertEquals(2, cartProducts.size(), "List should have two objects");
    }

    @Test
    void testIncreaseCartProductQuantityByOne() {
        String userEmail = "test@email.com";
        Long cartProductId = 1L;

        CartProduct cartProductBeforeUpdate = cartProductRepository.findById(cartProductId).orElse(null);
        assertNotNull(cartProductBeforeUpdate, "Object should not be null");
        int initialQuantity = cartProductBeforeUpdate.getQuantity();

        Integer updatedRows = cartProductRepository.increaseCartProductQuantityByOne(userEmail, cartProductId);

        entityManager.flush();
        entityManager.clear();

        CartProduct cartProductAfterUpdate = cartProductRepository.findById(cartProductId).orElse(null);
        assertNotNull(cartProductAfterUpdate, "Object should not be null");
        int updatedQuantity = cartProductAfterUpdate.getQuantity();

        assertEquals(1, updatedRows, "One row should be updated");
        assertEquals(initialQuantity + 1, updatedQuantity, "Product quantity should be increased by 1");
    }

    @Test
    void testDecreaseCartProductQuantityByOne() {
        String userEmail = "test@email.com";
        Long cartProductId = 1L;

        CartProduct cartProductBeforeUpdate = cartProductRepository.findById(cartProductId).orElse(null);
        assertNotNull(cartProductBeforeUpdate, "Object should not be null");
        int initialQuantity = cartProductBeforeUpdate.getQuantity();

        Integer updatedRows = cartProductRepository.decreaseCartProductQuantityByOne(userEmail, cartProductId);

        entityManager.flush();
        entityManager.clear();

        CartProduct cartProductAfterUpdate = cartProductRepository.findById(cartProductId).orElse(null);
        assertNotNull(cartProductAfterUpdate, "Object should not be null");
        int updatedQuantity = cartProductAfterUpdate.getQuantity();

        assertEquals(1, updatedRows, "One row should be updated");
        assertEquals(initialQuantity - 1, updatedQuantity, "Product quantity should be increased by 1");
    }

    @Test
    void testDeleteCartProduct() {
        String userEmail = "test@email.com";
        Long cartProductId = 1L;

        CartProduct cartProductFromDb = cartProductRepository.findById(cartProductId).orElse(null);
        assertNotNull(cartProductFromDb);

        cartProductRepository.deleteCartProduct(userEmail, cartProductId);
        entityManager.flush();
        entityManager.clear();

        Optional<CartProduct> optionalCartProduct = cartProductRepository.findById(cartProductId);


        assertTrue(optionalCartProduct.isEmpty(), "optionalCartProduct should be empty");
    }

    @Test
    void deleteAllCartProductsByUserEmail() {
        String userEmail = "test@email.com";

        List<CartProduct> cartProducts = cartProductRepository.findCartProductsByUserEmail(userEmail);
        assertEquals(2, cartProducts.size(), "List should contain 2 items");

        cartProductRepository.deleteAllCartProductsByUserEmail(userEmail);
        entityManager.flush();
        entityManager.clear();

        List<CartProduct> listOfCartProductsAfterDelete = cartProductRepository.findCartProductsByUserEmail(userEmail);

        assertTrue(listOfCartProductsAfterDelete.isEmpty());
    }
}


