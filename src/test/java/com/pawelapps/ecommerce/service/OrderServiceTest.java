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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    private OrderDto orderDto;

    private Product product1;

    private Product product2;
    private OrderProduct orderProduct1;
    private OrderProduct orderProduct2;

    @BeforeEach
    void setUp() {
        ShippingAddress shippingAddress = ShippingAddress.builder()
                .country("Sample Country")
                .city("Sample City")
                .streetAddress("Sample Street 24/36")
                .zipCode("12-345")
                .build();

        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Smith")
                .email("jonsmith@email.com")
                .phoneNumber(123456789)
                .build();

        ProductCategory productCategory = ProductCategory.builder().categoryName("Test Category").build();

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


        Summary summary = Summary.builder()
                .totalCartValue(BigDecimal.valueOf(2.59))
                .totalQuantityOfProducts(2)
                .build();

        order = Order.builder()
                .customer(customer)
                .summary(summary)
                .shippingAddress(shippingAddress)
                .build();
        order.addOrderProduct(orderProduct1);
        order.addOrderProduct(orderProduct2);

        orderDto = OrderDto.builder()
                .customer(customer)
                .shippingAddress(shippingAddress)
                .summary(summary)
                .build();

        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct1);
        orderProducts.add(orderProduct2);

        orderDto.setOrderProducts(orderProducts);

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
}
