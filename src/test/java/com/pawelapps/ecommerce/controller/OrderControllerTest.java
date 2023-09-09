package com.pawelapps.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawelapps.ecommerce.dto.OrderDto;
import com.pawelapps.ecommerce.entity.Customer;
import com.pawelapps.ecommerce.entity.OrderProduct;
import com.pawelapps.ecommerce.entity.ShippingAddress;
import com.pawelapps.ecommerce.entity.Summary;
import com.pawelapps.ecommerce.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        List<OrderProduct> orderProducts = new ArrayList<>();

        ShippingAddress shippingAddress = ShippingAddress.builder()
                .country("Sample Country")
                .city("Sample City")
                .streetAddress("Sample Street 24/36")
                .zipCode("12-345")
                .build();

        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Smith")
                .email("johnsmith@email.com")
                .phoneNumber(123456789)
                .build();

        OrderProduct orderProduct = OrderProduct.builder()
                .name("Test Product")
                .description("Product for testing purposes")
                .unitPrice(BigDecimal.valueOf(2.59))
                .imageUrl("assets/images/test-image")
                .quantity(4)
                .build();

        Summary summary = Summary.builder()
                .totalCartValue(BigDecimal.valueOf(10.36))
                .totalQuantityOfProducts(4)
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
    void shouldSaveOrder() throws Exception {
        when(orderService.saveOrder(any(OrderDto.class))).thenReturn(orderDto); // Return the orderDto instead of 'order'

        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customer.firstName").value("John"))
                .andExpect(jsonPath("$.summary.totalCartValue").value(10.36))
                .andExpect(jsonPath("$.summary.totalQuantityOfProducts").value(4));

        verify(orderService, times(1)).saveOrder(any(OrderDto.class));
    }
}
