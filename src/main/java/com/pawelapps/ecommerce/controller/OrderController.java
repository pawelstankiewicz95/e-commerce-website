package com.pawelapps.ecommerce.controller;

import com.pawelapps.ecommerce.dto.OrderDto;
import com.pawelapps.ecommerce.entity.Order;
import com.pawelapps.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin("http://localhost:4200")
@Controller
@RequestMapping("/api")
public class OrderController {
    OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping("/purchase")
    public ResponseEntity<Order> saveOrder(@RequestBody OrderDto orderDto){
        Order order = orderService.saveOrder(orderDto);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}
