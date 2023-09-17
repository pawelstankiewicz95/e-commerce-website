package com.pawelapps.ecommerce.controller;

import com.pawelapps.ecommerce.dto.CartProductDto;
import com.pawelapps.ecommerce.entity.CartProduct;
import com.pawelapps.ecommerce.service.CartProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("#principal?.name == #userEmail")
    public ResponseEntity<List<CartProduct>> getCartProductsByUserEmail(@PathVariable("userEmail") String userEmail, Principal principal) {
        List<CartProduct> cartProducts = cartProductService.findCartProductsByUserEmail(userEmail);
        return new ResponseEntity<>(cartProducts, HttpStatus.OK);
    }

    @PutMapping("/cart-products/increase/{userEmail}/{productId}")
    @PreAuthorize("#principal?.name == #userEmail")
    public ResponseEntity<Integer> increaseCartProductQuantityByOne(@PathVariable("userEmail") String userEmail, @PathVariable("productId") Long id, Principal principal) {
        Integer updatedRows = cartProductService.increaseCartProductQuantityByOne(userEmail, id);
        return new ResponseEntity<>(updatedRows, HttpStatus.OK);
    }

    @PutMapping("/cart-products/decrease/{userEmail}/{productId}")
    @PreAuthorize("#principal?.name == #userEmail")
    public ResponseEntity<Integer> decreaseCartProductQuantityByOne(@PathVariable("userEmail") String userEmail, @PathVariable("productId") Long id, Principal principal) {
        Integer updatedRows = cartProductService.decreaseCartProductQuantityByOne(userEmail, id);
        return new ResponseEntity<>(updatedRows, HttpStatus.OK);
    }

    @DeleteMapping("cart-products/{userEmail}")
    @PreAuthorize("#principal?.name == #userEmail")
    public ResponseEntity<?> deleteAllCartProductsByUserEmail(@PathVariable("userEmail") String userEmail, Principal principal) {
        cartProductService.deleteAllCartProductsByUserEmail(userEmail);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("cart-products/{userEmail}/{productId}")
    @PreAuthorize("#principal?.name == #userEmail")
    public ResponseEntity<?> deleteCartProductByUserEmailAndProductId(@PathVariable("userEmail") String userEmail, @PathVariable("productId") Long id, Principal principal) {
        cartProductService.deleteCartProduct(userEmail, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("cart-products/{userEmail}")
    @PreAuthorize("#principal?.name == #userEmail")
    public ResponseEntity<CartProductDto> saveCartProduct(@RequestBody CartProductDto cartProductDto, @PathVariable("userEmail") String userEmail, Principal principal) {
        CartProductDto savedCartProductDto = cartProductService.saveCartProductToCart(cartProductDto, userEmail);
        return new ResponseEntity<>(savedCartProductDto, HttpStatus.CREATED);
    }
}
