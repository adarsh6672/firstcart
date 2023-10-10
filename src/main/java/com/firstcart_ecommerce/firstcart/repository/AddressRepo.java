package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.model.Address;
import com.firstcart_ecommerce.firstcart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepo extends JpaRepository<Address , Long> {

    List<Address> findByDeletedFalse();

    @Query("SELECT a FROM Address a WHERE a.user = :user AND a.deleted = false")
    List<Address> findByUserAndDeletedFalse(User user);
}
