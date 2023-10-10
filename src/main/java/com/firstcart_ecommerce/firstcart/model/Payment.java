package com.firstcart_ecommerce.firstcart.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Payment {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;


        private String orderId;

        private String amount;

        private String receipt;

        private String paymentStatus;

        private String paymentId;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        private User user;
}
