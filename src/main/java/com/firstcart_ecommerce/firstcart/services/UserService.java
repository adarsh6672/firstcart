package com.firstcart_ecommerce.firstcart.services;


import com.firstcart_ecommerce.firstcart.model.User;

import java.util.List;

public interface UserService {
    public User saveUser(User user);
    public void removeSessionMessage();
    List<User> getAllUsers();

}
