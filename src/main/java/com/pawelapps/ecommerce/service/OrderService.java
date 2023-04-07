package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.entity.Order;

public interface OrderService {
    Order saveOrder(Order order);

    Order getAllOrders();
}
