package com.pawelapps.ecommerce.service;

import com.pawelapps.ecommerce.dao.ProductRepository;
import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.entity.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProductServiceTest {

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    Product product;
    ProductCategory productCategory;
    List<Product> products;


    @BeforeEach
    void setUp() {
        productCategory = ProductCategory.builder().categoryName("Cup").build();
        product = Product.builder()
                .sku("123456")
                .name("TestCup")
                .description("Just testing cup")
                .unitPrice(BigDecimal.valueOf(24.35))
                .imageUrl("www.test.com")
                .active(true)
                .unitsInStock(10)
                .dateCreated(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .productCategory(productCategory)
                .build();
    }

    @Test
    void createProductTest() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        assertEquals(product, productService.createProduct(new Product()), "product objects should be equal");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void getProductByIdTest() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        assertEquals(Optional.of(product), Optional.of(productService.getProductById(1L)), "product objects should be equal");
        verify(productRepository).findById(1L);
    }

    @Test
    void getAllProductsTest() {
        products = new ArrayList<>();
        products.add(product);

        when(productRepository.findAll()).thenReturn(products);

        assertEquals(products.size(), productService.getAllProducts().size(), "list sizes should be equal");
        verify(productRepository).findAll();
    }

    @Test
    void updateProductTest() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        assertEquals(product, productService.updateProduct(new Product()), "product objects should be equal");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void deleteProductByIdTest() {
        doNothing().when(productRepository).deleteById(anyLong());

        productService.deleteProductById(anyLong());

        verify(productRepository).deleteById(anyLong());
    }

    @Test
    void getProductsByNameLikeOrSkuLike() {
        products = new ArrayList<>();
        products.add(product);

        when(productRepository.findByNameLikeOrSkuLike(anyString())).thenReturn(products);

        assertEquals(products.size(), productService.getProductsByNameOrSku(anyString()).size(), "list sizes should be equal");
        verify(productRepository).findByNameLikeOrSkuLike(anyString());
    }
}
