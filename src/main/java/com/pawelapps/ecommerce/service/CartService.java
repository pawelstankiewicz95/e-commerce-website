package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dto.CartDto;
import com.pawelapps.ecommerce.entity.Cart;

public interface CartService {
    Cart saveCart(CartDto cartDto);
}
