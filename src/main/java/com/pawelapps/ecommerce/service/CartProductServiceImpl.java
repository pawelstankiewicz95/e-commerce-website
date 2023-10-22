package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dao.CartProductRepository;
import com.pawelapps.ecommerce.dao.CartRepository;
import com.pawelapps.ecommerce.dao.ProductRepository;
import com.pawelapps.ecommerce.dto.CartProductDto;
import com.pawelapps.ecommerce.entity.Cart;
import com.pawelapps.ecommerce.entity.CartProduct;
import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.entity.User;
import com.pawelapps.ecommerce.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CartProductServiceImpl implements CartProductService {

    private final CartProductRepository cartProductRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public CartProductServiceImpl(CartProductRepository cartProductRepository, CartRepository cartRepository, ProductRepository productRepository) {
        this.cartProductRepository = cartProductRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Override
    public CartProduct saveCartProduct(CartProduct cartProduct) {
        cartProduct.setCartProductId(null);
        return this.cartProductRepository.save(cartProduct);
    }

    @Override
    public CartProduct getCartProductById(Long surrogateId) {
        return this.cartProductRepository.findById(surrogateId).orElseThrow(() -> new NotFoundException("Cart product doesn't exist"));
    }

    @Override
    public CartProduct updateCartProduct(CartProduct cartProduct) {
        return this.cartProductRepository.save(cartProduct);
    }

    @Override
    public List<CartProduct> findCartProductsByUserEmail(String email) {
        List<CartProduct> cartProducts = cartProductRepository.findCartProductsByUserEmail(email);
        return cartProducts;
    }

    @Override
    public Integer increaseCartProductQuantityByOne(Long id) {
        Integer updatedRows;
        CartProduct cartProduct = cartProductRepository.findById(id).orElseThrow();
        Product product = productRepository.findById(cartProduct.getProduct().getId()).orElseThrow(() -> new NotFoundException("Product with id " + id + " doesn't exist"));
        if (cartProduct.getQuantity() < product.getUnitsInStock()) {
            updatedRows = cartProductRepository.increaseCartProductQuantityByOne(id);
        } else {
            throw new IllegalStateException("Not enough units in stock");
        }
        return updatedRows;
    }

    @Override
    public Integer decreaseCartProductQuantityByOne(Long id) {
        Integer updatedRows;
        CartProduct cartProduct = cartProductRepository.findById(id).orElseThrow(() -> new NotFoundException("Product with id " + id + " doesn't exist"));
        if (cartProduct.getQuantity() != 0) {
            updatedRows = cartProductRepository.decreaseCartProductQuantityByOne(id);
        } else {
            throw new IllegalStateException("Value can not be lower than 0");
        }
        return updatedRows;
    }

    @Override
    public void deleteCartProduct(Long cartProductId) {
        cartProductRepository.deleteCartProduct(cartProductId);
    }

    @Override
    public void deleteAllCartProductsByUserEmail(String email) {
        cartProductRepository.deleteAllCartProductsByUserEmail(email);
    }

    public CartProductDto saveCartProductToCart(CartProductDto cartProductDto, String userEmail) {

        Cart cart = getOrCreateCart(userEmail);

        CartProduct cartProduct = createCartProductFromDto(cartProductDto, cart);

        CartProduct savedCartProduct = cartProductRepository.save(cartProduct);
        cartProductDto.setCartProductId(savedCartProduct.getCartProductId());
        cartProductDto.setCart(cartProduct.getCart());

        return cartProductDto;
    }

    public Cart getOrCreateCart(String userEmail) {
        Cart cart = cartRepository.findByUserEmail(userEmail);
        if (cart == null) {
            User user = User.builder().email(userEmail).build();
            cart = Cart.builder().user(user).cartProducts(new ArrayList<>()).build();
            cartRepository.save(cart);
        }
        return cart;
    }

    public CartProduct createCartProductFromDto(CartProductDto cartProductDto, Cart cart) {
        CartProduct cartProduct = CartProduct.builder()
                .product(cartProductDto.getProduct())
                .quantity(cartProductDto.getQuantity())
                .name(cartProductDto.getName())
                .description(cartProductDto.getDescription())
                .unitPrice(cartProductDto.getUnitPrice())
                .imageUrl(cartProductDto.getImageUrl())
                .cart(cart)
                .build();
        return cartProduct;
    }

    public CartProduct createCartProductFromDto(CartProductDto cartProductDto) {
        CartProduct cartProduct = CartProduct.builder()
                .product(cartProductDto.getProduct())
                .quantity(cartProductDto.getQuantity())
                .name(cartProductDto.getName())
                .description(cartProductDto.getDescription())
                .unitPrice(cartProductDto.getUnitPrice())
                .imageUrl(cartProductDto.getImageUrl())
                .cart(cartProductDto.getCart())
                .build();
        return cartProduct;
    }
}
