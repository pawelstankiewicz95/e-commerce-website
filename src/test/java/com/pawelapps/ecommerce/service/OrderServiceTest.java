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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class OrderServiceTest {

    @MockBean
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @MockBean
    private ProductService productService;

    private Order order;
    private List<Order> orders;

    private OrderDto orderDto;

    private Product product1;
    private Product product2;

    private Summary summary;

    private OrderProduct orderProduct1;
    private OrderProduct orderProduct2;

    private List<OrderProduct> orderProducts;

    private User user;
    private String userEmail;

    private ProductCategory productCategory;

    private ShippingAddress shippingAddress;

    private Customer customer;

    @BeforeEach
    void setUp() {
        shippingAddress = ShippingAddress.builder()
                .country("Sample Country")
                .city("Sample City")
                .streetAddress("Sample Street 24/36")
                .zipCode("12-345")
                .build();

        customer = Customer.builder()
                .firstName("Test Customer First Name")
                .lastName("Test Customer Last Name")
                .email("customer@email.com")
                .phoneNumber(123456789)
                .build();

        userEmail = "user@email.com";
        user = User.builder().email(userEmail).build();

        productCategory = ProductCategory.builder().categoryName("Test Category").build();

        product1 = Product.builder().id(1L).name("Test Product 1").productCategory(productCategory).unitsInStock(10).build();
        product2 = Product.builder().id(2L).name("Test Product 1").productCategory(productCategory).unitsInStock(5).build();

        orderProduct1 = OrderProduct.builder()
                .product(product1)
                .name("Test Product")
                .description("Product for test purposes")
                .unitPrice(BigDecimal.valueOf(2.59))
                .imageUrl("assets/images/test-img.jpg")
                .quantity(4).build();

        orderProduct2 = OrderProduct.builder()
                .product(product2)
                .name("Test Product")
                .description("Product for test purposes")
                .unitPrice(BigDecimal.valueOf(2.59))
                .imageUrl("assets/images/test-img.jpg")
                .quantity(4).build();

        summary = Summary.builder()
                .totalCartValue(BigDecimal.valueOf(2.59))
                .totalQuantityOfProducts(2)
                .build();

        order = Order.builder()
                .user(user)
                .customer(customer)
                .summary(summary)
                .shippingAddress(shippingAddress)
                .build();

        order.addOrderProduct(orderProduct1);
        order.addOrderProduct(orderProduct2);

        orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct1);
        orderProducts.add(orderProduct2);

        orderDto = OrderDto.builder()
                .user(user)
                .customer(customer)
                .shippingAddress(shippingAddress)
                .summary(summary)
                .build();

        orderDto.setOrderProducts(orderProducts);

        orders = new ArrayList<>();
        orders.add(order);
    }

    @Test
    void shouldMapOrderDtoToOrder() {
        OrderDto orderDto = OrderDto.builder()
                .id(1L)
                .customer(customer)
                .shippingAddress(shippingAddress)
                .summary(summary)
                .user(user)
                .orderProducts(orderProducts)
                .build();

        Order order = orderService.mapOrderDtoToOrder(orderDto);

        assertEquals(orderDto.getId(), order.getId());
        assertEquals(orderDto.getCustomer(), order.getCustomer());
        assertEquals(orderDto.getShippingAddress(), order.getShippingAddress());
        assertEquals(orderDto.getSummary(), order.getSummary());
        assertEquals(orderDto.getUser(), order.getUser());
        assertEquals(orderDto.getOrderProducts(), order.getOrderProducts());
        assertEquals(orderDto.getOrderProducts().size(), order.getOrderProducts().size());
    }

    @Test
    void shouldMapOrderToOrderDto() {
        OrderDto orderDto = orderService.mapOrderToOrderDto(this.order);

        assertEquals(order.getId(), orderDto.getId());
        assertEquals(order.getCustomer(), orderDto.getCustomer());
        assertEquals(order.getShippingAddress(), orderDto.getShippingAddress());
        assertEquals(order.getSummary(), orderDto.getSummary());
        assertEquals(order.getUser(), orderDto.getUser());
        assertEquals(order.getOrderProducts(), orderDto.getOrderProducts());
        assertEquals(order.getOrderProducts().size(), orderDto.getOrderProducts().size());

    }

    @Test
    void shouldSaveOrder() {
        when(productService.decreaseProductQuantity(eq(orderProduct1.getProduct().getId()), eq(orderProduct1.getQuantity())))
                .thenAnswer((invocation) -> {
                    int quantityToDecrease = invocation.getArgument(1);
                    if (product1.getUnitsInStock() >= quantityToDecrease) {
                        product1.setUnitsInStock(product1.getUnitsInStock() - quantityToDecrease);
                    }
                    return product1;
                });
        when(productService.decreaseProductQuantity(eq(orderProduct2.getProduct().getId()), eq(orderProduct2.getQuantity())))
                .thenAnswer((invocation) -> {
                    int quantityToDecrease = invocation.getArgument(1);
                    if (product2.getUnitsInStock() >= quantityToDecrease) {
                        product2.setUnitsInStock(product2.getUnitsInStock() - quantityToDecrease);
                    }
                    return product2;
                });

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.saveOrder(orderDto);

        verify(productService).decreaseProductQuantity(eq(product1.getId()), anyInt());
        verify(productService).decreaseProductQuantity(eq(product2.getId()), anyInt());

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldFindOrdersByUserEmail() {
        when(orderRepository.findByUserEmail(userEmail)).thenReturn(orders);

        List<OrderDto> ordersDto = orderService.findByUserEmail(userEmail);

        assertEquals(ordersDto.size(), orders.size());

        verify(orderRepository).findByUserEmail(userEmail);
    }

    @Test
    void shouldFindOrdersByCustomerEmail() {
        when(orderRepository.findByCustomerEmail(customer.getEmail())).thenReturn(orders);

        List<OrderDto> ordersDto = orderService.findByCustomerEmail(customer.getEmail());

        assertEquals(ordersDto.size(), orders.size());

        verify(orderRepository).findByCustomerEmail(customer.getEmail());
    }

    @Test
    void shouldFindOrderById() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        OrderDto orderDtoAfterMethodCall = orderService.findById(order.getId());

        assertNotNull(orderDtoAfterMethodCall);

        verify(orderRepository).findById(order.getId());


    }


}


