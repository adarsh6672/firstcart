package com.firstcart_ecommerce.firstcart.services;


import com.firstcart_ecommerce.firstcart.model.Address;
import com.firstcart_ecommerce.firstcart.model.Cart;
import com.firstcart_ecommerce.firstcart.model.Product;
import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.CartRepo;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductService productService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CartRepo cartRepo;
    @Override
    public User saveUser(User user) {
        String password=passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        user.setRole("ROLE_USER");
        user.setBlocked(false);
        User newuser = userRepo.save(user);
        return newuser;
    }



    @Override
    public void removeSessionMessage() {
      HttpSession session=((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest().getSession();
        session.removeAttribute("msg");
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findByRoleNot("ROLE_ADMIN");
    }

    @Override
    public User getUserById(int id) {
        Optional<User> optional=userRepo.findById(id);
        User user=null;
        if(optional.isPresent()){
            user=optional.get();
        }else {
            throw new RuntimeException("user not found");
        }
        return user;
    }

    @Override
    public void blockUser(int id) {
        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setBlocked(true);
            userRepo.save(user);

        }

    }

    @Override
    public void unblockUser(int id) {
        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setBlocked(false);
            userRepo.save(user);

        }
    }

    @Override
    public List<User> searchUser(String query) {
        List<User> us= userRepo.searchUser(query);
        return us;
    }
    @Override
    public User updatePassword(User user) {
        String password=passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        User newuser = userRepo.save(user);
        return newuser;
    }

    @Override
    public void addAddressToUser(String email, Address address) {
        List<Address> addresses = new ArrayList<>();
        User user = userRepo.findByEmail(email);
        if (user != null) {
            if(address.isDefaultAddress()){
                Address oldDefaultAddress = user.getAddresses().stream()
                        .filter(Address::isDefaultAddress)
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("Default address not found"));

                oldDefaultAddress.setDefaultAddress(false);
            } else if (user.getAddresses().isEmpty()) {
                address.setDefaultAddress(true);
            }
                

            address.setUser(user);
            user.getAddresses().add(address);
            userRepo.save(user);
        }
    }
    @Override
    public Cart getOrCreateUserCart(User user) {
        Cart cart = cartRepo.findByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepo.save(cart);
        }
        return cart;
    }

    @Override
    public void addToUserCart(User user, Product product) {
        Cart cart = getOrCreateUserCart(user);
        cart.getProducts().add(product);
        cartRepo.save(cart);
    }
    @Override
    public Cart getUserCart(User user) {
        return getOrCreateUserCart(user);
    }
    @Override
    public void removeFromUserCart(User user, Long productId) {
        Cart cart = getOrCreateUserCart(user);
        Optional<Product> product = productService.getProductById(productId);
        product.ifPresent(cart.getProducts()::remove);
        cartRepo.save(cart);
    }



}
