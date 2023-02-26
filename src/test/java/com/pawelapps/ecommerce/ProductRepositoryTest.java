package com.pawelapps.ecommerce;

import com.pawelapps.ecommerce.dao.ProductRepository;
import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.entity.ProductCategory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@TestPropertySource("/test-application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
        Product productFromDataBase = productRepository.findById(1L).orElse(null);
        if (productFromDataBase != null) {
            assertEquals(product, productFromDataBase, "Objects should be the same");
            assertEquals("123456", productFromDataBase.getSku());
        } else fail("Object should not be null");
    }

    @Test
    void getProductsTest() {
        List<Product> products = productRepository.findAll();
        assertTrue(products.size() > 0, "List should be greater than zero");
    }

    @Test
    void updateProductTest() {
        Product productFromDataBase = productRepository.findById(1L).orElse(null);
        if (productFromDataBase != null) {
            productFromDataBase.setName("updated Cup");
            Product updatedProduct = productRepository.save(productFromDataBase);

            assertEquals(productFromDataBase.getId(), updatedProduct.getId(), "Id's should be the same");
            assertEquals(updatedProduct.getName(), "updated Cup", "Product names should be the same");
        } else fail("object should not be null");
    }

    @Test
    void deleteProductTest() {
        Product productFromDataBase = productRepository.findById(1L).orElse(null);
        if (productFromDataBase != null) {
            productRepository.delete(productFromDataBase);
            Optional<Product> optionalProduct = productRepository.findById(1L);

            assertTrue(optionalProduct.isEmpty(), "Optional product should be empty");
        } else fail("object should not be null");
    }

    @Test
    void findByName(){
        List<Product> products = productRepository.findByName("TestCup");
        assertFalse(products.isEmpty(), "list should not be empty");
    }
}
