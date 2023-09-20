package com.pawelapps.ecommerce.controller;

import com.pawelapps.ecommerce.dto.OrderDto;
import com.pawelapps.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PreAuthorize("(#principal?.name == #orderDto.user.email) or (isAnonymous() and #orderDto.user.email == 'Anonymous')")
    @PostMapping("/orders")
    public ResponseEntity<OrderDto> saveOrder(@RequestBody OrderDto orderDto, Principal principal) {
        OrderDto savedOrderDto = orderService.saveOrder(orderDto);
        return new ResponseEntity<>(savedOrderDto, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> ordersDto = orderService.getAllOrders();
        return new ResponseEntity<>(ordersDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/orders/customer")
    public ResponseEntity<List<OrderDto>> findOrdersByCustomerEmail(@RequestParam("customerEmail") String customerEmail, Principal principal){
        List<OrderDto> ordersDto = orderService.findByCustomerEmail(customerEmail);
        return new ResponseEntity<>(ordersDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('admin') or #principal?.name == #userEmail")
    @GetMapping("/orders/user")
    public ResponseEntity<List<OrderDto>> findOrdersByUserEmail(@RequestParam("userEmail") String userEmail, Principal principal){
        List<OrderDto> ordersDto = orderService.findByUserEmail(userEmail);
        return new ResponseEntity<>(ordersDto, HttpStatus.OK);
    }

    @PostAuthorize("hasAuthority('admin') or #principal?.name == returnObject.body.user.email")
    @GetMapping("/orders/id")
    public ResponseEntity<OrderDto> findOrderById(@RequestParam("id") Long id, Principal principal) {
        OrderDto orderDto = this.orderService.findById(id);
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }
}
