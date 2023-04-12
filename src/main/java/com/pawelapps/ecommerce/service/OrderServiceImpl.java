package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dao.OrderRepository;
import com.pawelapps.ecommerce.dto.OrderDto;
import com.pawelapps.ecommerce.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;

    @Autowired
    OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order saveOrder(OrderDto orderDto) {
        Customer customer = orderDto.getCustomer();
        ShippingAddress shippingAddress = orderDto.getShippingAddress();
        Summary summary = orderDto.getSummary();
        Set<OrderProduct> orderProducts = orderDto.getOrderProducts();
        Order order = Order.builder()
                .customer(customer)
                .shippingAddress(shippingAddress)
                .summary(summary).build();
        orderProducts.forEach(product -> order.addOrderProduct(product));
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
