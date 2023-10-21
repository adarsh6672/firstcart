package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.model.Wallet;
import com.firstcart_ecommerce.firstcart.model.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepo extends JpaRepository<Wallet , Long> {

    Wallet findByUser(User user);
}
