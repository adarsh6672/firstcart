package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Long , Order> {
}
