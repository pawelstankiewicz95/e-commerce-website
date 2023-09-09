package com.pawelapps.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class ProductCategory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "category_name")
    private String categoryName;

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.PERSIST}, mappedBy = "productCategory")
    @JsonIgnoreProperties("productCategory")
    private List<Product> products;

    public void addProduct(Product tempProduct) {
        if (products == null) {
            products = new ArrayList<>() {
            };
        }
        tempProduct.setProductCategory(this);
        products.add(tempProduct);
    }
}
