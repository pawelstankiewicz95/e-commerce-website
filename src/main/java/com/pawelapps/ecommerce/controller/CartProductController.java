package com.pawelapps.ecommerce.controller;

import com.pawelapps.ecommerce.dto.CartDto;
import com.pawelapps.ecommerce.entity.Cart;
import com.pawelapps.ecommerce.entity.CartProduct;
import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.service.CartProductService;
import com.pawelapps.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartProductController {
    private CartProductService cartProductService;

    @Autowired
    public CartProductController(CartProductService cartProductService) {
        this.cartProductService = cartProductService;
    }

 /*   @PostMapping("/cart-products")
    public ResponseEntity<CartDto> saveCart(@RequestBody CartDto cartDto) {
        cartService.saveCart(cartDto);
        return new ResponseEntity<>(cartDto, HttpStatus.CREATED);
    }*/

    @GetMapping("/cart-products/{userEmail}")
    public ResponseEntity<List<CartProduct>> getCartProductsByUserEmail(@PathVariable("userEmail") String email) {
        List<CartProduct> cartProducts = cartProductService.findCartProductsByUserEmail(email);
        return new ResponseEntity<>(cartProducts, HttpStatus.OK);
    }

    @PutMapping("/cart-products/increase/{userEmail}/{productId}")
    public ResponseEntity<Integer> increaseCartProductQuantityByOne(@PathVariable("userEmail") String email, @PathVariable("productId") Long id) {
        Integer updatedRows = cartProductService.increaseCartProductQuantityByOne(email, id);
        return new ResponseEntity<>(updatedRows, HttpStatus.OK);
    }

    @PutMapping("/cart-products/decrease/{userEmail}/{productId}")
    public ResponseEntity<Integer> decreaseCartProductQuantityByOne(@PathVariable("userEmail") String email, @PathVariable("productId") Long id) {
        Integer updatedRows = cartProductService.decreaseCartProductQuantityByOne(email, id);
        return new ResponseEntity<>(updatedRows, HttpStatus.OK);
    }

    @DeleteMapping("cart-products/{userEmail}")
    public ResponseEntity<?> deleteAllCartProductsByUserEmail(@PathVariable("userEmail") String email){
        cartProductService.deleteAllCartProductsByUserEmail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("cart-products/{userEmail}/{productId}")
    public ResponseEntity<?> deleteCartProductByUserEmailAndProductId(@PathVariable("userEmail") String email, @PathVariable("productId") Long id){
        cartProductService.deleteCartProduct(email,id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
