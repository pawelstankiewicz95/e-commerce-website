package com.pawelapps.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "email")
    private String email;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "cart_id")
    private Cart cart;
}
