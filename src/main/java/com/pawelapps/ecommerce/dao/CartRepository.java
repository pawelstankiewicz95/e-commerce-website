package com.pawelapps.ecommerce.dao;

import com.pawelapps.ecommerce.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserEmail(String email);
}
