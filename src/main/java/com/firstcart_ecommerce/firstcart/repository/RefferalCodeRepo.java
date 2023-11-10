package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.model.RefferalCode;
import com.firstcart_ecommerce.firstcart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefferalCodeRepo extends JpaRepository<RefferalCode, Long> {

    RefferalCode findByUser(User user);

    RefferalCode findByCode(String code);

}
