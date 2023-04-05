package com.pawelapps.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private int phoneNumber;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "customer")
    private Set<Order> orders = new HashSet<>();

    public void addOrder(Order order) {
        if (order != null) {
            if (orders == null) {
                orders = new HashSet<>();
            }
            order.setCustomer(this);
            orders.add(order);
        }
    }
}
