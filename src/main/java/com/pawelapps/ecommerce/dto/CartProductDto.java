package com.pawelapps.ecommerce.dto;

import com.pawelapps.ecommerce.entity.Cart;
import com.pawelapps.ecommerce.entity.Product;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartProductDto {
    private Long cartProductId;
    private Product product;
    private int quantity;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private String imageUrl;
    private Cart cart;
}
