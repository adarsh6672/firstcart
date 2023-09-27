package com.firstcart_ecommerce.firstcart.config;


import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException,DisabledException {
        User user=userRepo.findByEmail(username);
        if (user== null){
            throw new UsernameNotFoundException("userNotFound");
        }else if (user.isBlocked()) {
            throw new DisabledException("userBlocked");
        }

        else{
            return new CustomUser(user);
        }

    }
}
