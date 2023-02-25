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
import java.util.Optional;

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
        assertTrue(product.getId() > 0, "Id should be greater than 0");
    }

    @Test
    void getProductTest() {
        Product tempProduct = productRepository.findById(Long.valueOf(1)).get();
        assertTrue(product.equals(tempProduct), "Objects should be the same");
        assertEquals("123456", tempProduct.getSku());
    }

    @Test
    void getProductsTest(){
        List<Product> products = productRepository.findAll();
        assertTrue(products.size() > 0, "List should be greater than zero");
    }

    @Test
    void updateProductTest(){
        Product productFromDataBase = productRepository.findById(Long.valueOf(1)).get();
        productFromDataBase.setName("updated Cup");
        Product updatedProduct = productRepository.save(productFromDataBase);

        assertEquals(productFromDataBase.getId(), updatedProduct.getId(), "Id's should be the same");
        assertEquals(updatedProduct.getName(), "updated Cup", "Product names should be the same");
    }

    @Test
    void deleteProductTest(){
        Product tempProduct = productRepository.findById(Long.valueOf(1)).get();
        productRepository.delete(tempProduct);
        Optional<Product> optionalProduct = productRepository.findById(Long.valueOf(1));

        assertTrue(optionalProduct.isEmpty(), "Optional product should be empty");
    }
}
