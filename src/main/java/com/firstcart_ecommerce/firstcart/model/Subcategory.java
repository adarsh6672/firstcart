package com.firstcart_ecommerce.firstcart.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.validator.constraints.UniqueElements;

@Entity
public class Subcategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @UniqueElements
    private String name;
}
