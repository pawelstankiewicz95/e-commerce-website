package com.pawelapps.ecommerce.dao;

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
public class ProductCategoryRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    Product product;
    ProductCategory productCategory;

    @BeforeEach
    void setUpDataBase() {
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
        productCategory = ProductCategory.builder().categoryName("Cup").build();
        productCategory.addProduct(product);
        entityManager.persist(productCategory);
        entityManager.flush();
    }

    @Test
    void saveProductCategoryTest() {
        productCategoryRepository.save(productCategory);

        assertTrue(productCategory.getId() > 0, "Id should be greater than 0");
    }

    @Test
    void getCategoriesTest() {
        List<ProductCategory> categories = productCategoryRepository.findAll();

        assertFalse(categories.isEmpty(), "List should not be empty");
    }

    @Test
    void getCategoryTest() {
        ProductCategory productCategoryFromDatabase = productCategoryRepository.findById(1L).orElse(null);

        assertNotNull(productCategoryFromDatabase, "productCategoryFromDatabase should not be null");

        assertEquals(productCategory, productCategoryFromDatabase, "Objects should be the same");
        assertEquals("Cup", productCategoryFromDatabase.getCategoryName());
    }

    @Test
    void updateProductCategoryTest() {
        ProductCategory productCategoryFromDatabase = productCategoryRepository.findById(1L).orElse(null);

        assertNotNull(productCategoryFromDatabase, "productCategoryFromDatabase should not be null");

        productCategoryFromDatabase.setCategoryName("updated category : Cup");
        ProductCategory updatedProductCategory = productCategoryRepository.save(productCategoryFromDatabase);

        assertEquals(productCategoryFromDatabase.getId(), updatedProductCategory.getId(), "Id's should be the same");
        assertEquals(updatedProductCategory.getCategoryName(), "updated category : Cup", "Category names should be the same");

    }

    @Test
    void deleteProductCategoryTest() {
        ProductCategory productCategoryFromDatabase = productCategoryRepository.findById(1L).orElse(null);

        assertNotNull(productCategoryFromDatabase, "productCategoryFromDatabase should not be null");

        productCategoryRepository.delete(productCategoryFromDatabase);
        Optional<ProductCategory> optionalProductCategory = productCategoryRepository.findById(1L);

        assertTrue(optionalProductCategory.isEmpty(), "optionalProductCategory should be empty");

    }

}
