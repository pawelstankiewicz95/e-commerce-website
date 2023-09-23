package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dao.CartProductRepository;
import com.pawelapps.ecommerce.dao.CartRepository;
import com.pawelapps.ecommerce.dto.CartProductDto;
import com.pawelapps.ecommerce.entity.Cart;
import com.pawelapps.ecommerce.entity.CartProduct;
import com.pawelapps.ecommerce.entity.User;
import com.pawelapps.ecommerce.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CartProductServiceImpl implements CartProductService {

    private final CartProductRepository cartProductRepository;
    private final CartRepository cartRepository;

    @PersistenceContext
    private  EntityManager entityManager;

    @Autowired
    public CartProductServiceImpl(CartProductRepository cartProductRepository, CartRepository cartRepository) {
        this.cartProductRepository = cartProductRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public CartProduct saveCartProduct(CartProduct cartProduct) {
        return this.cartProductRepository.save(cartProduct);
    }

    @Override
    public CartProduct getCartProductById(Long surrogateId) {
        return this.cartProductRepository.findById(surrogateId).orElseThrow(() -> new NotFoundException("Cart product doesn't exist"));
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

    public CartProductDto saveCartProductToCart(CartProductDto cartProductDto, String userEmail) {

        Cart cart = cartRepository.findByUserEmail(userEmail);
        if (cart == null) {
            User user = User.builder().email(userEmail).build();
            List<CartProduct> cartProducts = new ArrayList<>();
            cart = Cart.builder().user(user).cartProducts(cartProducts).build();
            entityManager.persist(cart);
            entityManager.flush();
        }
        CartProduct cartProduct = CartProduct.builder()
                .product(cartProductDto.getProduct())
                .quantity(cartProductDto.getQuantity())
                .name(cartProductDto.getName())
                .description(cartProductDto.getDescription())
                .unitPrice(cartProductDto.getUnitPrice())
                .imageUrl(cartProductDto.getImageUrl())
                .cart(cart)
                .build();

        CartProduct savedCartProduct = cartProductRepository.save(cartProduct);
        cartProductDto.setCartProductId(savedCartProduct.getCartProductId());

        return cartProductDto;
    }
}
