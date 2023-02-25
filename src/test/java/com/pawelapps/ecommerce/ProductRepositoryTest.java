package com.pawelapps.ecommerce;

import com.pawelapps.ecommerce.dao.ProductRepository;
import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.entity.ProductCategory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@TestPropertySource("/test-application.properties")
class ProductRepositoryTest {

    @Autowired
    EntityManager entityManager;
    @Autowired
    ProductRepository productRepository;
    ProductCategory productCategory;
    Product product;


    @BeforeEach
    void setUpDataBase() {
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
        entityManager.persist(product);
        entityManager.flush();

    }

    @Test
    void saveProductTest() {
        productRepository.save(product);
        assertTrue(product.getId() > 0, "should be greater than 0");
    }

    @Test
    void getProductTest() {
        Product tempProduct = productRepository.findById(Long.valueOf(1)).get();
        assertTrue(product.equals(tempProduct), "Should be the same");
        assertEquals("123456", tempProduct.getSku());
    }

    @Test
    void getProductsTest(){
        List<Product> products = productRepository.findAll();
        assertTrue(products.size() > 0, "Should be greater than zero");
    }
}
