package com.pawelapps.ecommerce.dao;

import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.entity.ProductCategory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryIT extends BaseIT {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    static ProductCategoryRepository productCategoryRepository;
    static ProductCategory productCategory;

    @BeforeAll
    static void setUp(){
        productCategory = productCategoryRepository.save(ProductCategory.builder().categoryName("Test Category 1").build());
    }

    @Test
    void shouldSaveProduct() {
        Product savedProduct = productRepository.save(Product.builder()
                .sku("TEST1")
                .name("Test Product")
                .productCategory(productCategory)
                .unitsInStock(3)
                .active(true)
                .build());

        assertNotNull(savedProduct.getId(), "Id should not be null");
        assertEquals("TEST1", savedProduct.getSku(), "Product SKU should match");
        assertEquals("Test Product", savedProduct.getName(), "Product name should match");
        assertTrue(savedProduct.isActive());
    }

    @Test
    void shouldGetProduct() {

        Product savedProduct = productRepository.save(Product.builder()
                .sku("TEST2")
                .name("Test Product 2")
                .productCategory(productCategory)
                .unitsInStock(3)
                .active(true)
                .build());

        Product productFromDatabase = productRepository.findById(savedProduct.getId()).orElse(null);

        assertNotNull(productFromDatabase, "productFromDatabase should not be null");
        assertEquals("TEST2", productFromDatabase.getSku(), "Product SKU should match");
        assertEquals("TEST2", productFromDatabase.getName(), "Product name should match");
    }

    @Test
    void shouldGetAllProducts() {
        Product savedProduct1 = productRepository.save(Product.builder()
                .sku("TEST3")
                .name("Test Product 3")
                .productCategory(productCategory)
                .unitsInStock(3)
                .active(true)
                .build());
        Product savedProduct2 = productRepository.save(Product.builder()
                .sku("TEST4")
                .name("Test Product 4")
                .productCategory(productCategory)
                .unitsInStock(4)
                .active(false)
                .build());

        List<Product> products = productRepository.findAll();

        assertFalse(products.isEmpty(), "List should not be empty");
        assertTrue(products.size() > 1, "List should have at least two products");
        assertNotNull(products.stream().filter(p -> p.getId().equals(savedProduct1.getId())).findFirst(),"Should find savedProduct1 by id");
    }

    @Test
    void updateProductTest() {
        Product productFromDataBase = productRepository.findById(1L).orElse(null);
        assertNotNull(productFromDataBase, "Object should not be null");
        productFromDataBase.setName("updated Cup");
        Product updatedProduct = productRepository.save(productFromDataBase);

        assertEquals(productFromDataBase.getId(), updatedProduct.getId(), "Id's should be the same");
        assertEquals("updated Cup", updatedProduct.getName(), "Product names should be the same");
    }

    @Test
    void deleteProductTest() {
        Product productFromDataBase = productRepository.findById(1L).orElse(null);

        assertNotNull(productFromDataBase, "productFromDatabase should not be null");

        productRepository.delete(productFromDataBase);

        Optional<Product> optionalProduct = productRepository.findById(1L);
        Optional<ProductCategory> optionalProductCategory = productCategoryRepository.findById(1L);

        assertTrue(optionalProduct.isEmpty(), "Optional product should be empty");
        assertFalse(optionalProductCategory.isEmpty(), "Product category should not be deleted");

    }

    @Test
    void findByName() {
        List<Product> products = productRepository.findByName("TestCup");
        assertFalse(products.isEmpty(), "list should not be empty");
    }

    @Nested
    @DisplayName("findByNameLikeOrSkuLike method")
    class findByNameLikeOrSkuLikeTest {
        @Test
        @DisplayName("when finding by name")
        void findByNameLikeTest() {
            List<Product> products = productRepository.findByNameLikeOrSkuLike("est");
            assertFalse(products.isEmpty(), "list should not be empty");
        }

        @Test
        @DisplayName("when finding by sku")
        void findBySkuLikeTest() {
            List<Product> products = productRepository.findByNameLikeOrSkuLike("45");
            assertFalse(products.isEmpty(), "list should not be empty");
        }
    }
}
