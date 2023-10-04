package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.model.Order;
import com.firstcart_ecommerce.firstcart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepo extends JpaRepository<Order , Long> {
    List<Order> findByUser(User user);
    List<Order> findByUserOrderByOrderDateTimeDesc(User user);

    List<Order> findAllByOrderByOrderDateTimeDesc();
}
