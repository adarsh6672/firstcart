package com.firstcart_ecommerce.firstcart.config;


import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepo.findByEmail(username);
        if (user== null){
            throw new UsernameNotFoundException("user not foud");
        }else{
            return new CustomUser(user);
        }

    }
}
