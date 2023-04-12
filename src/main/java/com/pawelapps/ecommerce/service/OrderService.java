package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dto.OrderDto;
import com.pawelapps.ecommerce.entity.Order;

import java.util.List;

public interface OrderService {
    Order saveOrder(OrderDto orderDto);

    List<Order> getAllOrders();
}
