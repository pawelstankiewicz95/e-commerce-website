package com.pawelapps.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "summary")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Summary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "total_cart_value")
    private BigDecimal totalCartValue;

    @Column(name = "total_quantity")
    private int totalQuantityOfProducts;

    @Column(name = "shipping_price")
    private BigDecimal shippingPrice;

    @OneToOne(mappedBy = "summary")
    @JsonIgnoreProperties("summary")
    private Order order;
}
