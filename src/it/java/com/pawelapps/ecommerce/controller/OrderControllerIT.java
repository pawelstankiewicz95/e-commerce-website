package com.pawelapps.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@AutoConfigureMockMvc
@Transactional
public class OrderControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private final String authorizedUser = "authorized@example.com";
    private final String unauthorizedUser = "unauthorized@example.com";
    private final String uri = "/api/orders";

    private Order order;
    private Customer customer;
    private ShippingAddress shippingAddress;
    private User user;
    private Summary summary;
    private OrderProduct orderProduct;

    @BeforeEach
    void setUp() {
        customer = Customer.builder().firstName("John").lastName("Smith").phoneNumber(123456789).email("email@example.com").build();

        shippingAddress = ShippingAddress.builder().city("Warsaw").country("Poland").zipCode("12-345").streetAddress("Example Street").build();

        user = User.builder().email("user.email@example.com").build();

        summary = Summary.builder().totalCartValue(BigDecimal.valueOf(45.23)).totalQuantityOfProducts(3).build();

        orderProduct = OrderProduct.builder().name("Test Product").description("Test Description").quantity(3).unitPrice(BigDecimal.valueOf(12.99)).build();

        order = Order.builder().customer(customer).shippingAddress(shippingAddress).user(user).summary(summary).build();

        order.addOrderProduct(orderProduct);

        entityManager.persist(order);
        entityManager.flush();

    }
}
