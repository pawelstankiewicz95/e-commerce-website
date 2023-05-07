package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dao.CartRepository;
import com.pawelapps.ecommerce.dto.CartDto;
import com.pawelapps.ecommerce.entity.Cart;
import com.pawelapps.ecommerce.entity.CartProduct;
import com.pawelapps.ecommerce.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Autowired
    CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    @Transactional
    public Cart saveCart(CartDto cartDto) {
        User user = cartDto.getUser();
        Set<CartProduct> cartProducts = cartDto.getCartProducts();
        Cart cart = Cart.builder().user(user).build();
        cartProducts.forEach(cartProduct -> cart.addCartProduct(cartProduct));
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    @PreAuthorize("principal.subject == #email")
    public CartDto getCartByUserEmail(String email) {
        Cart cartFromDb = cartRepository.findByUserEmail(email);

        if (cartFromDb != null) {
            Set<CartProduct> cartProductsFromDb = cartFromDb.getCartProducts();
            Set<CartProduct> dtoCartProducts = cartProductsFromDb.stream().collect(Collectors.toSet());
            CartDto cartDto = CartDto.builder()
                    .user(cartFromDb.getUser())
                    .cartProducts(dtoCartProducts).build();
            return cartDto;
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    @PreAuthorize("principal.subject == #email")
    public void deleteCartByUserEmail(String email) {
        cartRepository.deleteByUserEmail(email);
    }

}
