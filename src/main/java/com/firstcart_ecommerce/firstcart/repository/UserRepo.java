package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepo extends JpaRepository<User,Integer> {
    public User findByEmail(String email);
    @Query(value = "SELECT * FROM user p WHERE "+
            "p.name LIKE CONCAT('%',:query,'%' )",nativeQuery = true)
    List<User> searchUser(String query);
}
