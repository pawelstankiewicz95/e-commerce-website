package com.pawelapps.ecommerce.dto;

import com.pawelapps.ecommerce.entity.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private Customer customer;
    private ShippingAddress shippingAddress;
    private Summary summary;
    private User user;
    private List<OrderProduct> orderProducts;

    public void addOrderProduct(OrderProduct orderProduct) {
        if (orderProduct != null){
            if (orderProducts == null){
                orderProducts = new ArrayList<>();
            }
            orderProducts.add(orderProduct);
        }
    }
}
