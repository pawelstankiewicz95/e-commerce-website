package com.pawelapps.ecommerce.controller;

import com.pawelapps.ecommerce.dto.CartDto;
import com.pawelapps.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CartController {
    private CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/cart")
    public ResponseEntity<CartDto> saveCart(@RequestBody CartDto cartDto) {
        cartService.saveCart(cartDto);
        return new ResponseEntity<>(cartDto, HttpStatus.CREATED);
    }
    @CrossOrigin("http://localhost:4200")
    @GetMapping("/cart/{userEmail}")
    public ResponseEntity<CartDto> getCartByUserEmail(@PathVariable("userEmail") String email) {
        CartDto cartDto = cartService.getCartByUserEmail(email);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }
}
