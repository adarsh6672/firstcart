package com.firstcart_ecommerce.firstcart.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Component;

@Entity
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String fullName;

    private String mobile;

    private String houseNo;

    private String street;

    private String pinCode;

    private String city;

    private String country;

    private String state;

    @Column(name = "is_default")
    private boolean defaultAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;



}
