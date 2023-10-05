package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dto.OrderDto;
import com.pawelapps.ecommerce.entity.Order;

import java.util.List;

public interface OrderService {
    OrderDto saveOrder(OrderDto orderDto);

    List<OrderDto> getAllOrders();

    List<OrderDto> findByCustomerEmail(String customerEmail);

    List<OrderDto> findByUserEmail(String userEmail);

    OrderDto findById(Long id);

    OrderDto mapOrderToOrderDto(Order order);

    Order mapOrderDtoToOrder(OrderDto orderDto);
}
