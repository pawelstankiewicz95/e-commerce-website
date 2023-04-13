package com.pawelapps.ecommerce.entity;

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

    @Column(name = "total_price")
    private BigDecimal totalCartValue;

    @Column(name = "total_quantity")
    private int totalQuantityOfProducts;

    @OneToOne(mappedBy = "summary")
    private Order order;
}
