package com.pawelapps.ecommerce.dao;

import com.pawelapps.ecommerce.BaseIT;
import com.pawelapps.ecommerce.entity.Product;
import com.pawelapps.ecommerce.entity.ProductCategory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class ProductRepositoryIT extends BaseIT {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private  ProductCategoryRepository productCategoryRepository;
    private ProductCategory productCategory;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        productCategory = productCategoryRepository.save(ProductCategory.builder().categoryName("Test Category 1").build());
        product1 = productRepository.save(Product.builder()
                .sku("PRODUCT1")
                .name("Test Product 1")
                .productCategory(productCategory)
                .unitsInStock(3)
                .active(true)
                .build());

        product2 = productRepository.save(Product.builder()
                .sku("PRODUCT2")
                .name("Test Product 2")
                .productCategory(productCategory)
                .unitsInStock(4)
                .active(false)
                .build());

        entityManager.persist(productCategory);
        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.flush();
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
        entityManager.persist(savedProduct);
        entityManager.flush();

        assertNotNull(savedProduct.getId(), "Id should not be null");
        assertEquals("TEST1", savedProduct.getSku(), "Product SKU should match");
        assertEquals("Test Product", savedProduct.getName(), "Product name should match");
        assertTrue(savedProduct.isActive());
    }

    @Test
    void shouldFindProductById() {

        Product productFromDatabase = productRepository.findById(product1.getId()).orElse(null);

        assertNotNull(productFromDatabase, "productFromDatabase should not be null");
        assertEquals("PRODUCT1", productFromDatabase.getSku(), "Product SKU should match");
        assertEquals("Test Product 1", productFromDatabase.getName(), "Product name should match");
    }

    @Test
    void shouldGetAllProducts() {


        List<Product> products = productRepository.findAll();

        assertFalse(products.isEmpty(), "List should not be empty");
        assertTrue(products.size() > 1, "List should have at least two products");
        assertNotNull(products.stream().filter(p -> p.getId().equals(product1.getId())).findFirst(), "Should find product1 by id");
        assertNotNull(products.stream().filter(p -> p.getId().equals(product2.getId())).findFirst(), "Should find product2 by id");
    }

    @Test
    void updateProductTest() {

        Product productFromDataBase = productRepository.findById(product1.getId()).orElse(null);
        assertNotNull(productFromDataBase, "Object should not be null");
        productFromDataBase.setName("Updated product 1");
        Product updatedProduct = productRepository.save(productFromDataBase);

        assertEquals(productFromDataBase.getId(), updatedProduct.getId(), "Id's should be the same");
        assertEquals("Updated product 1", updatedProduct.getName(), "Product names should be the same");

    }

    @Test
    void deleteProductTest() {
        Product savedProduct = productRepository.save(Product.builder()
                .sku("DELETE_TEST1")
                .name("Delete test Product")
                .productCategory(productCategory)
                .unitsInStock(3)
                .active(true)
                .build());
        entityManager.persist(savedProduct);
        entityManager.flush();

        Product productFromDataBase = productRepository.findById(savedProduct.getId()).orElse(null);

        assertNotNull(productFromDataBase, "productFromDatabase should not be null");

        productRepository.delete(productFromDataBase);

        Optional<Product> optionalProduct = productRepository.findById(savedProduct.getId());
        Optional<ProductCategory> optionalProductCategory = productCategoryRepository.findById(productCategory.getId());

        assertTrue(optionalProduct.isEmpty(), "Optional product should be empty");
        assertFalse(optionalProductCategory.isEmpty(), "Product category should not be deleted");

    }

    @Test
    void findByName() {
        List<Product> products = productRepository.findByName("Test Product 1");

        assertFalse(products.isEmpty(), "list should not be empty");
    }

    @Nested
    class findByNameLikeOrSkuLikeTest {
        @Test
        void shouldFindProductByIncompleteName() {
            List<Product> productsFoundByUpperCaseName = productRepository.findByNameLikeOrSkuLike("EST");
            List<Product> productsFoundByLowerCaseName = productRepository.findByNameLikeOrSkuLike("est");

            assertFalse(productsFoundByUpperCaseName.isEmpty(), "list should not be empty");
            assertFalse(productsFoundByLowerCaseName.isEmpty(), "list should not be empty");
        }

        @Test
        void shouldFindProductByIncompleteSku() {
            List<Product> productsFoundByUpperCaseSku = productRepository.findByNameLikeOrSkuLike("UCT1");
            List<Product> productsFoundByLowerCaseSku = productRepository.findByNameLikeOrSkuLike("uct1");

            assertFalse(productsFoundByUpperCaseSku.isEmpty(), "list should not be empty");
            assertFalse(productsFoundByLowerCaseSku.isEmpty(), "list should not be empty");
        }
    }
}
