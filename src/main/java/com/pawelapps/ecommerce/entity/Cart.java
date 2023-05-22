package com.pawelapps.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonManagedReference
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<CartProduct> cartProducts = new HashSet<>();

    public void addCartProduct(CartProduct cartProduct) {
        if (cartProduct != null) {
            if (cartProducts == null) {
                cartProducts = new HashSet<>();
            }
            cartProduct.setCart(this);
            cartProducts.add(cartProduct);
        }
    }
}
