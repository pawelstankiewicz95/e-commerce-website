package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dto.CartDto;

public interface CartService {
    CartDto getCartByUserEmail(String userEmail);
}
