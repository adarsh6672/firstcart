package com.firstcart_ecommerce.firstcart.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Entity
@Data
public class RefferalCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private User user;

    private String code;

    private int usagecount=0;

}
