package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dto.OrderDto;
import com.pawelapps.ecommerce.entity.Order;

public interface OrderService {
    Order saveOrder(OrderDto orderDto);

    Order getAllOrders();
}
