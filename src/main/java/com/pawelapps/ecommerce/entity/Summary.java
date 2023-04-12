package com.pawelapps.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "summary")
@Setter
@Getter
@NoArgsConstructor
public class Summary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "total_price")
    private int totalCartValue;

    @Column(name = "total_quantity")
    private int totalQuantityOfProducts;

    @OneToOne(mappedBy = "summary")
    private Order order;
}
