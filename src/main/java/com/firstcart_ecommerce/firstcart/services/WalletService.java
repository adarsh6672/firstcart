package com.firstcart_ecommerce.firstcart.services;

import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.model.Wallet;
import com.firstcart_ecommerce.firstcart.model.WishList;
import com.firstcart_ecommerce.firstcart.repository.WalletRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    private WalletRepo walletRepo;

    public Wallet getOrCreateUserWallet(User user) {
        Wallet wallet = walletRepo.findByUser(user);
        if (wallet == null) {
            Wallet newwallet = new Wallet();
            newwallet.setAmount(0.0);
            newwallet.setUser(user);
            walletRepo.save(newwallet);
            return newwallet;
        }
        return wallet;
    }
}
