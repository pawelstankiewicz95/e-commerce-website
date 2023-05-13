package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dao.CartProductRepository;
import com.pawelapps.ecommerce.dao.CartRepository;
import com.pawelapps.ecommerce.entity.Cart;
import com.pawelapps.ecommerce.entity.CartProduct;
import com.pawelapps.ecommerce.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CartProductServiceImpl implements CartProductService {

    private final CartProductRepository cartProductRepository;

    @Autowired
    public CartProductServiceImpl(CartProductRepository cartProductRepository) {
        this.cartProductRepository = cartProductRepository;
    }

    @Override
    public CartProduct saveCartProduct(CartProduct cartProduct) {
        return this.cartProductRepository.save(cartProduct);
    }

    @Override
    public CartProduct getCartProductById(Long surrogateId) {
        return this.cartProductRepository.findById(surrogateId).orElseThrow(() -> new ProductNotFoundException("Cart product doesn't exist"));
    }

    @Override
    public List<CartProduct> getAllCartProducts() {
        return this.cartProductRepository.findAll();
    }

    @Override
    public CartProduct updateCartProduct(CartProduct cartProduct) {
        return this.cartProductRepository.save(cartProduct);
    }

    @Override
    public void deleteCartProductById(Long surrogateId) {

    }

    @Override
    public List<CartProduct> findCartProductsByUserEmail(String email) {
        List<CartProduct> cartProducts = cartProductRepository.findCartProductsByUserEmail(email);
        return cartProducts;
    }

    @Override
    public Integer increaseCartProductQuantityByOne(String email, Long id) {
        Integer updatedRows = cartProductRepository.increaseCartProductQuantityByOne(email, id);
        return updatedRows;
    }

    @Override
    public Integer decreaseCartProductQuantityByOne(String email, Long id) {
        Integer updatedRows = cartProductRepository.decreaseCartProductQuantityByOne(email, id);
        return updatedRows;
    }

    @Override
    public void deleteCartProduct(String email, Long productId) {
        cartProductRepository.deleteCartProduct(email, productId);
    }

    @Override
    public void deleteAllCartProductsByUserEmail(String email) {
        cartProductRepository.deleteAllCartProductsByUserEmail(email);
    }
}
