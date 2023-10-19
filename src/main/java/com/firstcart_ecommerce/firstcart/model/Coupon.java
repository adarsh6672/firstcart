package com.firstcart_ecommerce.firstcart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.boot.registry.selector.spi.StrategyCreator;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
@Entity
@Data
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String couponCode;

    private BigDecimal minimumAmount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date expiryDate;

    private int discountPercentage;


}
