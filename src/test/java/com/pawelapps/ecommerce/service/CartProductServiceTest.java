package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dao.CartProductRepository;
import com.pawelapps.ecommerce.dao.CartRepository;
import com.pawelapps.ecommerce.dto.CartProductDto;
import com.pawelapps.ecommerce.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CartProductServiceTest {

    @MockBean
    private CartProductRepository cartProductRepository;

    @MockBean
    private CartRepository cartRepository;

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
    void setUp(){
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
                .product(product)
                .cartProductId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .quantity(1)
                .build();

        cartProduct = CartProduct.builder()
                .product(product)
                .cartProductId(cartProductDto.getCartProductId())
                .name(cartProductDto.getName())
                .description(cartProductDto.getDescription())
                .quantity(cartProductDto.getQuantity())
                .build();
    }

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
}
