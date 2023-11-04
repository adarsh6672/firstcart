package com.firstcart_ecommerce.firstcart.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
public class ProductOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private double discountPercentage;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expiryDate;

    private String offerDetails;

}
