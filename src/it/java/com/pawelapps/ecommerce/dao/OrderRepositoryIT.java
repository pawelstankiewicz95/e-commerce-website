package com.pawelapps.ecommerce.dao;

import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class OrderRepositoryIT extends BaseIT {

    @Autowired
    OrderRepository orderRepository;

    @Test
    void saveOrderTest() {
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

        Order order = Order.builder()
                .customer(customer)
                .summary(summary)
                .shippingAddress(shippingAddress)
                .build();
        order.addOrderProduct(orderProduct);

        Order savedOrder = orderRepository.save(order);
        assertTrue(savedOrder.getId() > 0, "Id should be greater than 0");
        assertNotNull(savedOrder.getCustomer(), "Customer object should not be null");
        assertNotNull(savedOrder.getShippingAddress(), "ShippingAddress object should not be null");
        assertNotNull(savedOrder.getSummary(), "Summary object should not be null");
        assertTrue(savedOrder.getOrderProducts().size() > 0, "orderProducts set size should be greater than 0");

    }
}
