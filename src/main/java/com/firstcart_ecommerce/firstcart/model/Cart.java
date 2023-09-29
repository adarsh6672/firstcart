package com.firstcart_ecommerce.firstcart.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToMany
    private List<Product> products = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "user_cart_selected_products", joinColumns = @JoinColumn(name = "cart_id"))
    @Column(name = "product_id")
    private List<Long> selectedProductIds = new ArrayList<>();


    private double totalAmount;
    public double getTotalCartAmount() {
        double totalAmount = 0.0;
        for (Product product : products) {
            totalAmount += product.getPrice();
        }
        this.totalAmount = totalAmount;

        return totalAmount;
    }

    public void setTotalCartAmount( double totalAmount){
        this.totalAmount = totalAmount;

    }

    public Cart() {
    }



}
