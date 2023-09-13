package com.pawelapps.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
public class OrderControllerIT extends BaseIT {

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private final String authorizedUserEmail = "authorized@example.com";
    private final String unauthorizedUserEmail = "unauthorized@example.com";
    private final String uri = "/api/orders";

    private Order authorizedUserOrder;
    private Order anonymousUserOrder;
    private Customer customer1;
    private Customer customer2;
    private ShippingAddress shippingAddress1;
    private ShippingAddress shippingAddress2;
    private User authorizedUser;
    private User unauthorizedUser;
    private Summary summary1;
    private Summary summary2;
    private OrderProduct orderProduct1;
    private OrderProduct orderProduct2;

    @BeforeEach
    void setUp() {
        orderProduct1 = OrderProduct.builder().name("Test Product One").description("Test Description One").quantity(3).unitPrice(BigDecimal.valueOf(1)).build();
        orderProduct2 = OrderProduct.builder().name("Test Product Two").description("Test Description Two").quantity(1).unitPrice(BigDecimal.valueOf(2)).build();

        authorizedUser = User.builder().email(authorizedUserEmail).build();
        customer1 = Customer.builder().firstName("First Name One").lastName("Last Name One").phoneNumber(123456789).email("email1@example.com").build();
        shippingAddress1 = ShippingAddress.builder().city("City One").country("Country One").zipCode("12-345").streetAddress("Street One").build();
        summary1 = Summary.builder().totalCartValue(BigDecimal.valueOf(5)).totalQuantityOfProducts(3).build();

        authorizedUserOrder = Order.builder().user(authorizedUser).customer(customer1).shippingAddress(shippingAddress1).summary(summary1).build();
        authorizedUserOrder.addOrderProduct(orderProduct1);
        authorizedUserOrder.addOrderProduct(orderProduct2);

        // unauthorizedUser = User.builder().email(unauthorizedUserEmail).build();
        customer2 = Customer.builder().firstName("First Name Two").lastName("Last Name Two").phoneNumber(111222333).email("email2@example.com").build();
        shippingAddress2 = ShippingAddress.builder().city("City Two").country("Country Two").zipCode("12-345").streetAddress("Street Two").build();
        summary2 = Summary.builder().totalCartValue(BigDecimal.valueOf(5)).totalQuantityOfProducts(3).build();

        anonymousUserOrder = Order.builder().customer(customer2).shippingAddress(shippingAddress2).summary(summary2).build();
        anonymousUserOrder.addOrderProduct(orderProduct1);
        anonymousUserOrder.addOrderProduct(orderProduct2);

        entityManager.persist(authorizedUserOrder);
        entityManager.persist(anonymousUserOrder);
        entityManager.flush();
    }

    @Nested
    class getAllOrdersTests {

        @Test
        @WithMockUser(authorities = "admin")
        void shouldGetAllOrdersForAuthorizedUser() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @WithMockUser(authorities = "user")
        void shouldNotGetOrdersForUnauthorizedUser() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());

        }

        @Test
        @WithAnonymousUser
        void shouldNotGetOrdersForAnonymousUser() throws Exception{
            mockMvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

    }
}
