package com.pawelapps.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawelapps.ecommerce.dto.OrderDto;
import com.pawelapps.ecommerce.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.awt.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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

        orderDto = OrderDto.builder()
                .customer(customer)
                .shippingAddress(shippingAddress)
                .summary(summary)
                .build();

        orderProducts.add(orderProduct);

        orderDto.setOrderProducts(orderProducts);
    }

    @Test
    void saveOrderTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customer.firstName").value("Jan"));
    }
}
