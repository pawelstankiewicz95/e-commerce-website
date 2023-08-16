package com.pawelapps.ecommerce.controller;

import com.pawelapps.ecommerce.dto.CartDto;
import com.pawelapps.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class CartController {
    private CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/cart/{userEmail}")
    @PreAuthorize("#principal?.name == #userEmail")
    public ResponseEntity<CartDto> getCartByUserEmail(@PathVariable("userEmail") String userEmail, Principal principal) {
        CartDto cartDto = cartService.getCartByUserEmail(userEmail);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    @DeleteMapping("cart/{userEmail}")
    @PreAuthorize("#principal?.name == #userEmail")
    public ResponseEntity<?> deleteCartByUserEmail(@PathVariable("userEmail") String userEmail, Principal principal) {
        cartService.deleteCartByUserEmail(userEmail);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
