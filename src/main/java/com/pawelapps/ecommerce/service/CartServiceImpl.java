package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dao.CartRepository;
import com.pawelapps.ecommerce.dto.CartDto;
import com.pawelapps.ecommerce.entity.Cart;
import com.pawelapps.ecommerce.entity.CartProduct;
import com.pawelapps.ecommerce.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Autowired
    CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Cart saveCart(CartDto cartDto) {
        User user = cartDto.getUser();
        Set<CartProduct> cartProducts = cartDto.getCartProducts();
        Cart cart = Cart.builder().user(user).build();
        cartProducts.forEach(cartProduct -> cart.addCartProduct(cartProduct));
        return cartRepository.save(cart);
    }
}
