package com.pawelapps.ecommerce.dao;

import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.entity.ProductCategory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class ProductCategoryRepositoryIT extends BaseIT {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    private ProductCategory productCategory;

    @BeforeEach
    void setUp() {
        productCategory = ProductCategory.builder().categoryName("Test Category 1").build();
        entityManager.persist(productCategory);
        entityManager.flush();
    }

    @Test
    void shouldSaveCategory() {
        productCategoryRepository.save(productCategory);

        assertTrue(productCategory.getId() > 0, "Id should be greater than 0");
    }

    @Test
    void shouldGetAllCategories() {
        List<ProductCategory> categories = productCategoryRepository.findAll();

        assertFalse(categories.isEmpty(), "List should not be empty");
    }

    @Test
    void shouldFindCategoryById() {
        ProductCategory productCategoryFromDatabase = productCategoryRepository.findById(productCategory.getId()).orElse(null);

        assertNotNull(productCategoryFromDatabase, "productCategoryFromDatabase should not be null");
        assertEquals(productCategory.getId(), productCategoryFromDatabase.getId(), "IDs should be the same");
        assertEquals(productCategory.getCategoryName(), productCategoryFromDatabase.getCategoryName(), "Names should be equal");
    }

    @Test
    void shouldUpdateCategory() {
        ProductCategory productCategoryFromDatabase = productCategoryRepository.findById(productCategory.getId()).orElse(null);

        assertNotNull(productCategoryFromDatabase, "productCategoryFromDatabase should not be null");

        productCategoryFromDatabase.setCategoryName("Updated Category");
        productCategoryRepository.save(productCategoryFromDatabase);

        assertEquals(productCategoryFromDatabase.getId(), productCategory.getId(), "IDs should be the same");
        assertEquals(productCategoryRepository.findById(productCategory.getId()).orElse(null).getCategoryName(), "Updated Category", "Category names should match");
    }

    @Test
    void deleteProductCategoryTest() {
        ProductCategory productCategoryFromDatabase = productCategoryRepository.findById(productCategory.getId()).orElse(null);

        assertNotNull(productCategoryFromDatabase, "productCategoryFromDatabase should not be null");

        productCategoryRepository.delete(productCategoryFromDatabase);
        Optional<ProductCategory> optionalProductCategory = productCategoryRepository.findById(productCategory.getId());

        assertTrue(optionalProductCategory.isEmpty(), "optionalProductCategory should be empty");
    }

}
