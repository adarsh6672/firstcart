package com.firstcart_ecommerce.firstcart.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    private Product product;

    private String imageName;
}
