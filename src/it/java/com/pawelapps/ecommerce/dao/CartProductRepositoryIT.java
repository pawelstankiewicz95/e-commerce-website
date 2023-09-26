package com.pawelapps.ecommerce.dao;

import com.pawelapps.ecommerce.BaseIT;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class CartProductRepositoryIT extends BaseIT {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CartProductRepository cartProductRepository;

    private CartProduct cartProduct1;
    private CartProduct cartProduct2;
    private Cart cart;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().email("test@example.com").build();
        entityManager.persist(user);

        cart = Cart.builder().user(user).build();
        entityManager.persist(cart);

        cartProduct1 = CartProduct.builder().cart(cart).name("Test cart product 1").quantity(1).build();
        entityManager.persist(cartProduct1);

        cartProduct2 = CartProduct.builder().cart(cart).name("Test cart product 2").quantity(23).build();
        entityManager.persist(cartProduct2);

        entityManager.flush();
    }

    @Test
    void testFindCartProductsByUserEmail() {
        String userEmail = "test@example.com";

        List<CartProduct> cartProducts = cartProductRepository.findCartProductsByUserEmail(userEmail);

        assertEquals(2, cartProducts.size(), "List should have two cart products");
    }

    @Test
    void testIncreaseCartProductQuantityByOne() {
        String userEmail = "test@example.com";
        Long cartProductId = cartProduct1.getCartProductId();

        CartProduct cartProductBeforeUpdate = cartProductRepository.findById(cartProductId).orElse(null);
        assertNotNull(cartProductBeforeUpdate, "Object should not be null");
        int initialQuantity = cartProductBeforeUpdate.getQuantity();

        Integer updatedRows = cartProductRepository.increaseCartProductQuantityByOne(cartProductId);

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
        String userEmail = "test@example.com";
        Long cartProductId = cartProduct1.getCartProductId();

        CartProduct cartProductBeforeUpdate = cartProductRepository.findById(cartProductId).orElse(null);
        assertNotNull(cartProductBeforeUpdate, "Object should not be null");
        int initialQuantity = cartProductBeforeUpdate.getQuantity();

        Integer updatedRows = cartProductRepository.decreaseCartProductQuantityByOne(cartProductId);

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
        String userEmail = "test@example.com";
        Long cartProductId = cartProduct1.getCartProductId();

        CartProduct cartProductFromDb = cartProductRepository.findById(cartProductId).orElse(null);
        assertNotNull(cartProductFromDb, "cartProductFromDb should not be empty");

        cartProductRepository.deleteCartProduct(cartProductId);
        entityManager.flush();
        entityManager.clear();

        Optional<CartProduct> optionalCartProduct = cartProductRepository.findById(cartProductId);


        assertTrue(optionalCartProduct.isEmpty(), "optionalCartProduct should be empty");
    }

    @Test
    void deleteAllCartProductsByUserEmail() {
        String userEmail = "test@example.com";

        List<CartProduct> cartProducts = cartProductRepository.findCartProductsByUserEmail(userEmail);
        assertEquals(2, cartProducts.size(), "List should contain 2 items");

        cartProductRepository.deleteAllCartProductsByUserEmail(userEmail);
        entityManager.flush();
        entityManager.clear();

        List<CartProduct> listOfCartProductsAfterDelete = cartProductRepository.findCartProductsByUserEmail(userEmail);

        assertTrue(listOfCartProductsAfterDelete.isEmpty(),"List of cart products should be empty");
    }
}


