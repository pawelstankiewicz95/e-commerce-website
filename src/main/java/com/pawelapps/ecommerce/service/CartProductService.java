package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dto.CartProductDto;
import com.pawelapps.ecommerce.entity.CartProduct;

import java.util.List;


public interface CartProductService {
    CartProduct saveCartProduct(CartProduct cartProduct);

    CartProduct getCartProductById(Long surrogateId);

    List<CartProduct> getAllCartProducts();

    CartProduct updateCartProduct(CartProduct cartProduct);

    void deleteCartProductById(Long surrogateId);

    List<CartProduct> findCartProductsByUserEmail(String email);

    Integer increaseCartProductQuantityByOne(String email, Long id);

    Integer decreaseCartProductQuantityByOne(String email, Long id);

    void deleteCartProduct(String email, Long productId);

    void deleteAllCartProductsByUserEmail(String email);

    CartProductDto saveCartProductToCart(CartProductDto cartProductDto, String userEmail);

}
