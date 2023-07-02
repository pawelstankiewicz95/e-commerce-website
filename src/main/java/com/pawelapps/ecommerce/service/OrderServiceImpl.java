package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dao.OrderRepository;
import com.pawelapps.ecommerce.dto.OrderDto;
import com.pawelapps.ecommerce.entity.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;
    private ModelMapper modelMapper;

    @Autowired
    OrderServiceImpl(OrderRepository orderRepository, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;

    }

    @Override
    public Order saveOrder(OrderDto orderDto) {
        Order order = modelMapper.map(orderDto, Order.class);
        return orderRepository.save(order);
    }



    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<OrderDto> findByCustomerEmail(String email) {
        List<Order> orders = orderRepository.findByCustomerEmail(email);
        List<OrderDto> ordersDto = orders.stream().map(o -> modelMapper.map(o, OrderDto.class)).collect(Collectors.toList());
        return ordersDto;
    }

    @Override
    public List<OrderDto> findByUserEmail(String userEmail) {
        List<Order> orders = orderRepository.findByUserEmail(userEmail);
        List <OrderDto> ordersDto = orders.stream().map(o -> modelMapper.map(o, OrderDto.class)).collect(Collectors.toList());
        return ordersDto;
    }

}
