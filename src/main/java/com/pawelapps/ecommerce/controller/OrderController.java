package com.pawelapps.ecommerce.controller;

import com.pawelapps.ecommerce.dto.OrderDto;
import com.pawelapps.ecommerce.entity.Order;
import com.pawelapps.ecommerce.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/purchase")
    public ResponseEntity<OrderDto> saveOrder(@RequestBody OrderDto orderDto) {
        orderService.saveOrder(orderDto);
        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/orders/{customerEmail}")
    public ResponseEntity<List<OrderDto>> findOrdersByCustomerEmail(@PathVariable("customerEmail") String customerEmail) throws AccessDeniedException {
        List<OrderDto> orderDtos = orderService.findByCustomerEmail(customerEmail);
        return new ResponseEntity<>(orderDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('admin') or #principal?.userName == #userEmail")
    @GetMapping("/orders/{userEmail}")
    public ResponseEntity<List<OrderDto>> findOrdersByUserEmail(@PathVariable("userEmail") String userEmail, Principal principal) throws AccessDeniedException {
        List<OrderDto> ordersDto = orderService.findByUserEmail(userEmail);
        return new ResponseEntity<>(ordersDto, HttpStatus.OK);
    }
}
