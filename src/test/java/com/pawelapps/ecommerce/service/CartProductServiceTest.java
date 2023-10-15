package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dao.CartProductRepository;
import com.pawelapps.ecommerce.dao.CartRepository;
import com.pawelapps.ecommerce.dao.ProductRepository;
import com.pawelapps.ecommerce.dto.CartProductDto;
import com.pawelapps.ecommerce.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CartProductServiceTest {

    @MockBean
    private CartProductRepository cartProductRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private CartProductService cartProductService;

    private final String userEmail = "test@email.com";

    private User user;

    private Cart cart;

    private ProductCategory productCategory;

    private Product product;

    private CartProduct cartProduct;

    private CartProductDto cartProductDto;

    @BeforeEach
    void setUp() {
        user = User.builder().email(userEmail).build();

        cart = Cart.builder().user(user).build();

        productCategory = ProductCategory.builder().categoryName("Test Category").build();

        product = Product.builder().productCategory(productCategory)
                .id(1L)
                .sku("123")
                .name("Test Product")
                .description("Test Description")
                .unitsInStock(10)
                .unitPrice(BigDecimal.valueOf(1)).build();

        cartProductDto = CartProductDto.builder()
                .cartProductId(1L)
                .product(product)
                .cartProductId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .quantity(1)
                .build();

        cartProduct = CartProduct.builder()
                .cartProductId(cartProductDto.getCartProductId())
                .product(product)
                .cartProductId(cartProductDto.getCartProductId())
                .name(cartProductDto.getName())
                .description(cartProductDto.getDescription())
                .quantity(cartProductDto.getQuantity())
                .build();
    }

    @Nested
    class SaveCartProductTests {

        @Test
        void shouldSaveCartProductWhenCartIsPresent() {
            when(cartRepository.findByUserEmail(userEmail)).thenReturn(cart);
            when(cartProductRepository.save(any(CartProduct.class))).thenReturn(cartProduct);

            CartProductDto savedCartProductDto = cartProductService.saveCartProductToCart(cartProductDto, userEmail);

            assertNotNull(savedCartProductDto);
            assertNotNull(savedCartProductDto.getCartProductId());
            assertEquals(cart, savedCartProductDto.getCart());

            verify(cartRepository).findByUserEmail(userEmail);
            verify(cartProductRepository).save(any(CartProduct.class));
        }

        @Test
        void shouldSaveCartProductWhenCartIsNotPresent() {
            when(cartRepository.findByUserEmail(userEmail)).thenReturn(null);
            when(cartProductRepository.save(any(CartProduct.class))).thenReturn(cartProduct);

            CartProductDto savedCartProductDto = cartProductService.saveCartProductToCart(cartProductDto, userEmail);

            assertNotNull(savedCartProductDto);
            assertNotNull(savedCartProductDto.getCartProductId());
            assertNotNull(savedCartProductDto.getCart());

            verify(cartRepository).findByUserEmail(userEmail);
            verify(cartProductRepository).save(any(CartProduct.class));
        }
    }

    @Nested
    class IncreaseCartProductQuantityByOneTests {

        @Test
        void shouldIncreaseQuantityWhenProductHasEnoughUnitsInStock() {
            product.setUnitsInStock(10);
            cartProduct.setQuantity(9);
            when(cartProductRepository.findById(cartProduct.getCartProductId())).thenReturn(Optional.ofNullable(cartProduct));
            when(productRepository.findById(cartProduct.getProduct().getId())).thenReturn(Optional.ofNullable(product));
            when(cartProductRepository.increaseCartProductQuantityByOne(cartProduct.getCartProductId())).thenReturn(1);

            Integer updatedRows = cartProductService.increaseCartProductQuantityByOne(cartProduct.getCartProductId());

            assertEquals(1, updatedRows);
        }

        @Test
        void shouldThrowExceptionWhenProductHasNotGotEnoughUnitsInStock() {
            product.setUnitsInStock(10);
            cartProduct.setQuantity(10);
            when(cartProductRepository.findById(cartProduct.getCartProductId())).thenReturn(Optional.ofNullable(cartProduct));
            when(productRepository.findById(cartProduct.getProduct().getId())).thenReturn(Optional.ofNullable(product));

            assertThrows(IllegalStateException.class, () -> cartProductService.increaseCartProductQuantityByOne(cartProduct.getCartProductId()));
        }
    }


}
