package com.firstcart_ecommerce.firstcart.services;

import com.firstcart_ecommerce.firstcart.model.Cart;
import com.firstcart_ecommerce.firstcart.model.Product;
import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.CartRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepo cartRepo;
    public boolean isInCart(Long id , User user){
        Cart cart= cartRepo.findByUser(user);
        List<Product> productsInCart=cart.getProducts();
        for (Product product : productsInCart) {
            if (product.getId()==id) {
                return true;
            }
        }
        return false;
    }

}
