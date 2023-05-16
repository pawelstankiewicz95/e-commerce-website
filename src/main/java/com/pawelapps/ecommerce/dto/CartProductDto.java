package com.pawelapps.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pawelapps.ecommerce.entity.Cart;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartProductDto {
    private Long id;
    private int quantity;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private String imageUrl;
    private Cart cart;
}
