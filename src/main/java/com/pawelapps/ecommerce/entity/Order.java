package com.pawelapps.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @OneToOne
    @JoinColumn(name = "shipping_address_id", referencedColumnName = "id")
    private ShippingAddress shippingAddress;

    @OneToOne
    @JoinColumn(name = "summary_id", referencedColumnName = "id")
    private Summary summary;

    @OneToMany(mappedBy = "order")
    private Set<OrderItem> orderItems = new HashSet<>();

    public void addOrderItem(OrderItem orderItem) {
        if (orderItem != null){
            if (orderItems == null){
                orderItems = new HashSet<>();
            }
            orderItem.setOrder(this);
            orderItems.add(orderItem);
        }
    }

}
