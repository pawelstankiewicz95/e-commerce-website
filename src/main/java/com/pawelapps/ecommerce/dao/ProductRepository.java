package com.pawelapps.ecommerce.dao;

import com.pawelapps.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByName(String name);
    List<Product> findByProductCategoryId(Long id);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:nameOrSku% OR p.sku LIKE %:nameOrSku%")
    List<Product> findByNameLikeOrSkuLike(@Param("nameOrSku") String nameOrSku);
}
