package com.pawelapps.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.dto.OrderDto;
import com.pawelapps.ecommerce.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private final String anonymousUserEmail = "Anonymous";

    private final String uri = "/api/orders";

    private User authorizedUser;
    private Order order1;
    private Order order2;
    private Customer customer1;
    private Customer customer2;
    private ShippingAddress shippingAddress1;
    private ShippingAddress shippingAddress2;
    private Summary summary1;
    private Summary summary2;
    private OrderProduct orderProduct1;
    private OrderProduct orderProduct2;

    private Order getOrderFromDB(Long id) {
        Order order;
        TypedQuery<Order> query = entityManager.createQuery(
                "SELECT o FROM Order o WHERE o.id = :id", Order.class);
        query.setParameter("id", id);

        try {
            order = query.getSingleResult();
        } catch (NoResultException noResultException) {
            order = null;
        }

        entityManager.clear();

        return order;
    }

    @BeforeEach
    void setUp() {
        authorizedUser = User.builder()
                .email(authorizedUserEmail)
                .build();

        orderProduct1 = OrderProduct.builder()
                .name("Test Product One")
                .description("Test Description One")
                .quantity(3)
                .unitPrice(BigDecimal.valueOf(1))
                .build();

        orderProduct2 = OrderProduct.builder()
                .name("Test Product Two")
                .description("Test Description Two")
                .quantity(1)
                .unitPrice(BigDecimal.valueOf(2))
                .build();


        customer1 = Customer.builder()
                .firstName("First Name One")
                .lastName("Last Name One").phoneNumber(123456789)
                .email("email1@example.com")
                .build();

        shippingAddress1 = ShippingAddress.builder()
                .city("City One")
                .country("Country One")
                .zipCode("12-345")
                .streetAddress("Street One")
                .build();

        summary1 = Summary.builder()
                .totalCartValue(BigDecimal.valueOf(5))
                .totalQuantityOfProducts(3)
                .build();

        order1 = Order.builder()
                .user(authorizedUser)
                .customer(customer1)
                .shippingAddress(shippingAddress1)
                .summary(summary1)
                .build();

        order1.addOrderProduct(orderProduct1);
        order1.addOrderProduct(orderProduct2);


        customer2 = Customer.builder()
                .firstName("First Name Two")
                .lastName("Last Name Two")
                .phoneNumber(111222333)
                .email("email2@example.com")
                .build();

        shippingAddress2 = ShippingAddress.builder()
                .city("City Two")
                .country("Country Two")
                .zipCode("12-345")
                .streetAddress("Street Two")
                .build();

        summary2 = Summary.builder()
                .totalCartValue(BigDecimal.valueOf(5))
                .totalQuantityOfProducts(3)
                .build();

        order2 = Order.builder().user(authorizedUser).customer(customer2).shippingAddress(shippingAddress2).summary(summary2).build();
        order2.addOrderProduct(orderProduct1);
        order2.addOrderProduct(orderProduct2);

        entityManager.persist(order1);
        entityManager.persist(order2);
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
        void shouldNotGetOrdersForAnonymousUser() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

    }

    @Nested
    class SaveOrderTests {
        Customer customerForSave;

        private OrderDto authorizedUserOrderDtoForSave;
        private ShippingAddress shippingAddressForSave;
        private Summary summaryForSave;
        private OrderProduct orderProductForSave1;
        private OrderProduct orderProductForSave2;
        private List<OrderProduct> orderProductsForSave;


        @BeforeEach
        void setUp() {
            User authorizedUserForSave = User.builder()
                    .email(authorizedUserEmail)
                    .build();

            customerForSave = Customer.builder()
                    .firstName("Saved First Name")
                    .lastName("Saved Last Name")
                    .phoneNumber(123456789)
                    .email("email1@example.com")
                    .build();

            shippingAddressForSave = ShippingAddress.builder()
                    .city("Saved City")
                    .country("Saved Country")
                    .zipCode("12-345")
                    .streetAddress("Saved Street")
                    .build();

            summaryForSave = Summary.builder()
                    .totalCartValue(BigDecimal.valueOf(2))
                    .totalQuantityOfProducts(2)
                    .build();

            orderProductForSave1 = OrderProduct.builder()
                    .name("Saved Product One")
                    .description("Saved Description One")
                    .quantity(20)
                    .unitPrice(BigDecimal.valueOf(1))
                    .build();


            orderProductForSave2 = OrderProduct.builder()
                    .name("Saved Product Two")
                    .description("Saved Description Two")
                    .quantity(20)
                    .unitPrice(BigDecimal.valueOf(1)).build();

            entityManager.persist(orderProductForSave1);
            entityManager.persist(orderProductForSave2);

            orderProductsForSave = new ArrayList<>();
            orderProductsForSave.add(orderProductForSave1);
            orderProductsForSave.add(orderProductForSave2);

            authorizedUserOrderDtoForSave = OrderDto.builder()
                    .user(authorizedUserForSave)
                    .customer(customerForSave)
                    .shippingAddress(shippingAddressForSave)
                    .summary(summaryForSave)
                    .orderProducts(orderProductsForSave)
                    .build();

        }

        private void testUnauthorizedSave() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authorizedUserOrderDtoForSave)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(authorizedUserEmail)
        void shouldSaveOrderForAuthorizedUser() throws Exception {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authorizedUserOrderDtoForSave)))
                    .andExpect(status().isCreated()).andReturn();
            String content = result.getResponse().getContentAsString();

            Long id = JsonPath.parse(content).read("$.id", Long.class);

            assertNotNull(id);
            assertNotNull(getOrderFromDB(id));
        }

        @Test
        @WithMockUser(unauthorizedUserEmail)
        void shouldNotSaveOrderForUnauthorizedUser() throws Exception {
            testUnauthorizedSave();
        }

        @Test
        @WithAnonymousUser
        void shouldSaveOrderForAnonymousUser() throws Exception {
            User anonymousUserForSave = User.builder()
                    .email(anonymousUserEmail)
                    .build();

            OrderDto anonymousUserOrderDtoForSave = OrderDto.builder()
                    .user(anonymousUserForSave)
                    .customer(customerForSave)
                    .shippingAddress(shippingAddressForSave)
                    .summary(summaryForSave)
                    .orderProducts(orderProductsForSave)
                    .build();

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(anonymousUserOrderDtoForSave)))
                    .andExpect(status().isCreated()).andReturn();
            String content = result.getResponse().getContentAsString();

            Long id = JsonPath.parse(content).read("$.id", Long.class);

            assertNotNull(id);
            assertNotNull(getOrderFromDB(id));
        }
    }

    @Nested
    class findOrderByUserEmailTests {
        private String findOrderByUserEmailUri = "/api/orders/user";

        private void testAuthorizedFind() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(findOrderByUserEmailUri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("userEmail", authorizedUserEmail))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.[0].user.email").value(authorizedUserEmail))
                    .andExpect(jsonPath("$.[0].customer.firstName").value("First Name One"))
                    .andExpect(jsonPath("$.[0].shippingAddress.city").value("City One"))
                    .andExpect(jsonPath("$.[0].summary.totalCartValue").value("5"))
                    .andExpect(jsonPath("$.[0].orderProducts.[0].name").value("Test Product One"));
        }

        private void testUnauthorizedFind() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(findOrderByUserEmailUri)
                            .param("userEmail", authorizedUserEmail)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(authorities = "admin")
        void shouldFindOrdersForAdmin() throws Exception {
            testAuthorizedFind();
        }

        @Test
        @WithMockUser(authorizedUserEmail)
        void shouldFindOrdersForAuthorizedUser() throws Exception {
            testAuthorizedFind();
        }

        @Test
        @WithMockUser(unauthorizedUserEmail)
        void shouldNotFindOrderForUnauthorizedUser() throws Exception {
            testUnauthorizedFind();
        }

        @Test
        @WithAnonymousUser
        void shouldNotFindOrderForAnonymousUser() throws Exception {
            testUnauthorizedFind();
        }
    }

    @Nested
    class findOrderByIdTests {
        private final String findOrderByIdUri = "/api/orders/id";

        private void testAuthorizedFind() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(findOrderByIdUri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", order1.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.user.email").value(authorizedUserEmail))
                    .andExpect(jsonPath("$.customer.firstName").value("First Name One"))
                    .andExpect(jsonPath("$.shippingAddress.city").value("City One"))
                    .andExpect(jsonPath("$.summary.totalCartValue").value("5"))
                    .andExpect(jsonPath("$.orderProducts.[0].name").value("Test Product One"));
        }

        private void testUnauthorizedFind() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(findOrderByIdUri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("id", order1.getId().toString()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(authorities = "admin")
        void shouldFindOrderForAdmin() throws Exception {
            testAuthorizedFind();
        }

        @Test
        @WithMockUser(authorizedUserEmail)
        void shouldFindOrderForAuthorizedUser() throws Exception {
            testAuthorizedFind();
        }

        @Test
        @WithMockUser(unauthorizedUserEmail)
        void shouldNotFindOrderForUnauthorizedUser() throws Exception {
            testUnauthorizedFind();
        }

        @Test
        @WithAnonymousUser
        void shouldNotFindOrderForAnonymousUser() throws Exception {
            testUnauthorizedFind();
        }
    }

    @Nested
    class FindOrdersByCustomerEmailTests {
        private String findOrderByCustomerEmailUri = "/api/orders/customer";

        private void testUnauthorizedFind() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(findOrderByCustomerEmailUri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("customerEmail", "email1@example.com"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(authorities = "admin")
        void shouldFindOrderByCustomerEmailForAuthorizedUser() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get(findOrderByCustomerEmailUri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("customerEmail", "email1@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.[0].user.email").value(authorizedUserEmail))
                    .andExpect(jsonPath("$.[0].customer.firstName").value("First Name One"))
                    .andExpect(jsonPath("$.[0].shippingAddress.city").value("City One"))
                    .andExpect(jsonPath("$.[0].summary.totalCartValue").value("5"))
                    .andExpect(jsonPath("$.[0].orderProducts.[0].name").value("Test Product One"));
        }

        @Test
        @WithMockUser(unauthorizedUserEmail)
        void shouldNotFindOrderByCustomerEmailForUnauthorizedUser() throws Exception {
            testUnauthorizedFind();
        }

        @Test
        @WithAnonymousUser
        void shouldNotFindOrderByCustomerEmailForAnonymousUser() throws Exception {
            testUnauthorizedFind();
        }
    }
}
