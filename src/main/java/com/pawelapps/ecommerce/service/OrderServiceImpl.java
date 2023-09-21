package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dao.OrderRepository;
import com.pawelapps.ecommerce.dao.ProductRepository;
import com.pawelapps.ecommerce.dto.OrderDto;
import com.pawelapps.ecommerce.entity.Order;
import com.pawelapps.ecommerce.entity.OrderProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private OrderRepository orderRepository;
    private ProductService productService;

    @Autowired
    OrderServiceImpl(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    @Override
    public OrderDto saveOrder(OrderDto orderDto) {
        Order order = this.mapOrderDtoToOrder(orderDto);

        for (OrderProduct op : order.getOrderProducts()) {
            productService.decreaseProductQuantity(op.getProductId(), op.getQuantity());
        }

        orderRepository.save(order);

        orderDto.setId(order.getId());

        return orderDto;
    }


    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<OrderDto> ordersDto = orders.stream().map(order -> this.mapOrderToOrderDto(order)).collect(Collectors.toList());
        return ordersDto;

    }

    @Override
    public List<OrderDto> findByCustomerEmail(String email) {
        List<Order> orders = orderRepository.findByCustomerEmail(email);
        List<OrderDto> ordersDto = orders.stream().map(order -> this.mapOrderToOrderDto(order)).collect(Collectors.toList());
        return ordersDto;
    }

    @Override
    public List<OrderDto> findByUserEmail(String userEmail) {
        List<Order> orders = orderRepository.findByUserEmail(userEmail);
        List<OrderDto> ordersDto = orders.stream().map(order -> this.mapOrderToOrderDto(order)).collect(Collectors.toList());
        return ordersDto;
    }

    @Override
    public OrderDto findById(Long id) {
        Order order = this.orderRepository.findById(id).orElseThrow();
        OrderDto orderDto = this.mapOrderToOrderDto(order);
        return orderDto;
    }

    private OrderDto mapOrderToOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setCustomer(order.getCustomer());
        orderDto.setShippingAddress(order.getShippingAddress());
        orderDto.setSummary(order.getSummary());
        orderDto.setUser(order.getUser());

        List<OrderProduct> orderProducts = order.getOrderProducts();
        orderProducts.forEach(orderDto::addOrderProduct);

        return orderDto;
    }

    private Order mapOrderDtoToOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setId(orderDto.getId());
        order.setCustomer(orderDto.getCustomer());
        order.setShippingAddress(orderDto.getShippingAddress());
        order.setSummary(orderDto.getSummary());
        order.setUser(orderDto.getUser());

        List<OrderProduct> orderProducts = orderDto.getOrderProducts();
        orderProducts.forEach(order::addOrderProduct);

        return order;
    }


}
