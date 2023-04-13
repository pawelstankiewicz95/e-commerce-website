package com.pawelapps.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipping_address")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "zip_code")
    private String zipCode;

    @OneToOne(mappedBy = "shippingAddress")
    private Order order;
}
