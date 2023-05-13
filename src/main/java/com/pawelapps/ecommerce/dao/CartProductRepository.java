package com.pawelapps.ecommerce.dao;

import com.pawelapps.ecommerce.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    @Query("SELECT cp FROM CartProduct cp JOIN cp.cart c JOIN c.user u WHERE u.email = :email")
    List<CartProduct> findCartProductsByUserEmail(String email);

    @Modifying
    @Query("UPDATE CartProduct cp SET cp.quantity = cp.quantity + 1 WHERE cp.id = :productId AND cp.cart.id IN (SELECT c.id FROM Cart c JOIN c.user u WHERE u.email = :email)")
    Integer increaseCartProductQuantityByOne(@Param("email") String email, @Param("productId") Long cartProductId);

    @Modifying
    @Query("UPDATE CartProduct cp SET cp.quantity = cp.quantity -1 WHERE cp.id = :productId AND cp.cart.id IN (SELECT c.id FROM Cart c JOIN c.user u WHERE u.email = :email)")
    Integer decreaseCartProductQuantityByOne(@Param("email") String email, @Param("productId") Long cartProductId);

    @Modifying
    @Query("DELETE FROM CartProduct cp WHERE cp.id = :productId AND cp.cart.id IN (SELECT c.id FROM Cart c JOIN c.user u WHERE u.email = :email)")
    void deleteCartProduct(@Param("email")String email, @Param("productId") Long productId);

    @Modifying
    @Query("DELETE FROM CartProduct cp WHERE cp.cart.id IN (SELECT c.id FROM Cart c JOIN c.user u WHERE u.email = :email)")
    void deleteAllCartProductsByUserEmail(@Param("email") String email);
}
