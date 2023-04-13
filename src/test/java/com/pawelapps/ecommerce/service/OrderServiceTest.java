package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dao.OrderRepository;
import com.pawelapps.ecommerce.dto.OrderDto;
import com.pawelapps.ecommerce.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class OrderServiceTest {

    @MockBean
    OrderRepository orderRepository;

    @Autowired
    OrderService orderService;

    Order order;

    OrderDto orderDto;

    @BeforeEach
    void setUp() {
        Set<OrderProduct> orderProducts = new HashSet<>();

        ShippingAddress shippingAddress = ShippingAddress.builder()
                .country("Polska")
                .city("Warszawa")
                .streetAddress("ul. Sezamkowa 24/36")
                .zipCode("12-345")
                .build();

        Customer customer = Customer.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .email("jankowalski@email.com")
                .phoneNumber(123456789)
                .build();

        OrderProduct orderProduct = OrderProduct.builder().name("Cup")
                .description("Cup for test")
                .unitPrice(BigDecimal.valueOf(2.59))
                .imageUrl("imagefortesting.com")
                .quantity(4).build();

        Summary summary = Summary.builder()
                .totalCartValue(BigDecimal.valueOf(2.59))
                .totalQuantityOfProducts(2)
                .build();

        order = Order.builder()
                .customer(customer)
                .summary(summary)
                .shippingAddress(shippingAddress)
                .build();
        order.addOrderProduct(orderProduct);

        orderDto = OrderDto.builder()
                .customer(customer)
                .shippingAddress(shippingAddress)
                .summary(summary)
                .build();

        orderProducts.add(orderProduct);

        orderDto.setOrderProducts(orderProducts);

    }

    @Test
    void saveOrderTest() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        assertEquals(order.getId(), orderService.saveOrder(orderDto).getId(),"IDs should be equal");
    }
}
