package com.firstcart_ecommerce.firstcart.model;

import com.firstcart_ecommerce.firstcart.util.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "order_date_time")
    private LocalDateTime orderDateTime;

    public String getFormattedOrderDate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return orderDateTime.format(dateFormatter);
    }
    public String getDelivreyDate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate orderDate = LocalDate.parse(orderDateTime.format(dateFormatter), dateFormatter);
        LocalDate newDate = orderDate.plusDays(4); // Adding 4 days
        return newDate.format(dateFormatter);
    }

    public String getFormattedOrderTime() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return orderDateTime.format(timeFormatter);
    }



    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "payment_method")
    private String paymentMethod;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address shippingAddress;

    public List<OrderItem> getItems() {
        return this.orderItems;
    }

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
