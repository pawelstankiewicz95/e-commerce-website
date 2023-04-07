package com.pawelapps.ecommerce.dto;

import com.pawelapps.ecommerce.entity.Customer;
import com.pawelapps.ecommerce.entity.OrderItem;
import com.pawelapps.ecommerce.entity.ShippingAddress;
import com.pawelapps.ecommerce.entity.Summary;

import java.util.Set;

public class OrderDto {
    private Customer customer;
    private ShippingAddress shippingAddress;
    private Summary summary;
    private Set<OrderItem> orderItems;
}
