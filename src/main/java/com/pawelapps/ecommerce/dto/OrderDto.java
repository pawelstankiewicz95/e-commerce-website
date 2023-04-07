package com.pawelapps.ecommerce.dto;

import com.pawelapps.ecommerce.entity.Customer;
import com.pawelapps.ecommerce.entity.OrderItem;
import com.pawelapps.ecommerce.entity.ShippingAddress;
import com.pawelapps.ecommerce.entity.Summary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class OrderDto {
    private Customer customer;
    private ShippingAddress shippingAddress;
    private Summary summary;
    private Set<OrderItem> orderItems;
}
