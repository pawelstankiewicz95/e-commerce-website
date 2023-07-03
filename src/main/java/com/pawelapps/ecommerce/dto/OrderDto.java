package com.pawelapps.ecommerce.dto;

import com.pawelapps.ecommerce.entity.Customer;
import com.pawelapps.ecommerce.entity.OrderProduct;
import com.pawelapps.ecommerce.entity.ShippingAddress;
import com.pawelapps.ecommerce.entity.Summary;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Customer customer;
    private ShippingAddress shippingAddress;
    private Summary summary;
    private List<OrderProduct> orderProducts;
}
