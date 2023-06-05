package com.pawelapps.ecommerce.controller;

import com.pawelapps.ecommerce.dto.CartProductDto;
import com.pawelapps.ecommerce.entity.CartProduct;
import com.pawelapps.ecommerce.service.CartProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")

public class CartProductController {
    private CartProductService cartProductService;

    @Autowired
    public CartProductController(CartProductService cartProductService) {
        this.cartProductService = cartProductService;

    }

    @GetMapping("/cart-products/{userEmail}")
    public ResponseEntity<List<CartProduct>> getCartProductsByUserEmail(@PathVariable("userEmail") String email, Principal principal) {
        if (principal == null || !principal.getName().equals(email)) {
            throw new AccessDeniedException("Access denied");
        }
        List<CartProduct> cartProducts = cartProductService.findCartProductsByUserEmail(email);
        return new ResponseEntity<>(cartProducts, HttpStatus.OK);
    }

    @PutMapping("/cart-products/increase/{userEmail}/{productId}")
    public ResponseEntity<Integer> increaseCartProductQuantityByOne(@PathVariable("userEmail") String email, @PathVariable("productId") Long id, Principal principal) throws AccessDeniedException {
        if (principal == null || !principal.getName().equals(email)) {
            throw new AccessDeniedException("Access denied");
        }
        Integer updatedRows = cartProductService.increaseCartProductQuantityByOne(email, id);
        return new ResponseEntity<>(updatedRows, HttpStatus.OK);
    }

    @PutMapping("/cart-products/decrease/{userEmail}/{productId}")
    public ResponseEntity<Integer> decreaseCartProductQuantityByOne(@PathVariable("userEmail") String email, @PathVariable("productId") Long id, Principal principal) throws AccessDeniedException {
        if (principal == null || !principal.getName().equals(email)) {
            throw new AccessDeniedException("Access denied");
        }
        Integer updatedRows = cartProductService.decreaseCartProductQuantityByOne(email, id);
        return new ResponseEntity<>(updatedRows, HttpStatus.OK);
    }

    @DeleteMapping("cart-products/{userEmail}")
    public ResponseEntity<?> deleteAllCartProductsByUserEmail(@PathVariable("userEmail") String email, Principal principal) {
        if (principal == null || !principal.getName().equals(email)) {
            throw new AccessDeniedException("Access denied");
        }
        cartProductService.deleteAllCartProductsByUserEmail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("cart-products/{userEmail}/{productId}")
    public ResponseEntity<?> deleteCartProductByUserEmailAndProductId(@PathVariable("userEmail") String email, @PathVariable("productId") Long id, Principal principal) {
        if (principal == null || !principal.getName().equals(email)) {
            throw new AccessDeniedException("Access denied");
        }
        cartProductService.deleteCartProduct(email, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("cart-products/{userEmail}")
    public ResponseEntity<CartProductDto> saveCartProduct(@RequestBody CartProductDto cartProductDto, @PathVariable("userEmail") String email, Principal principal) {
        if (principal == null || !principal.getName().equals(email)) {
            throw new AccessDeniedException("Access denied");
        }
        cartProductService.saveCartProductToCart(cartProductDto, email);
        return new ResponseEntity<>(cartProductDto, HttpStatus.CREATED);
    }
}
