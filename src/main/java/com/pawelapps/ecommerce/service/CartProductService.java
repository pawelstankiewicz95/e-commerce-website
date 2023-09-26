package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dto.CartProductDto;
import com.pawelapps.ecommerce.entity.CartProduct;

import java.util.List;


public interface CartProductService {
    CartProduct saveCartProduct(CartProduct cartProduct);

    CartProduct getCartProductById(Long surrogateId);

    CartProduct updateCartProduct(CartProduct cartProduct);

    List<CartProduct> findCartProductsByUserEmail(String email);

    Integer increaseCartProductQuantityByOne(Long id);

    Integer decreaseCartProductQuantityByOne(Long id);

    void deleteCartProduct(Long productId);

    void deleteAllCartProductsByUserEmail(String email);

    CartProductDto saveCartProductToCart(CartProductDto cartProductDto, String userEmail);

}
