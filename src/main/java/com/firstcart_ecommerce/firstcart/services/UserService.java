package com.firstcart_ecommerce.firstcart.services;


import com.firstcart_ecommerce.firstcart.model.Address;
import com.firstcart_ecommerce.firstcart.model.User;

import java.util.List;

public interface UserService {
    public User saveUser(User user);

    public User updatePassword(User user);

    public void removeSessionMessage();
    List<User> getAllUsers();

    User getUserById(int id);

    void blockUser(int id);

    void unblockUser(int id);

    List<User> searchUser(String query);

    void addAddressToUser(String email, Address address);


}
