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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Autowired
    CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public CartDto getCartByUserEmail(String userEmail) {
        Cart cartFromDb = cartRepository.findByUserEmail(userEmail);

        if (cartFromDb != null) {
            List<CartProduct> cartProductsFromDb = cartFromDb.getCartProducts();
            List<CartProduct> dtoCartProducts = cartProductsFromDb.stream().collect(Collectors.toList());
            CartDto cartDto = CartDto.builder()
                    .user(cartFromDb.getUser())
                    .cartProducts(dtoCartProducts).build();
            return cartDto;
        } else {
            return null;
        }
    }

    @Override
    public void deleteCartByUserEmail(String userEmail) {
        cartRepository.deleteByUserEmail(userEmail);
    }

}
