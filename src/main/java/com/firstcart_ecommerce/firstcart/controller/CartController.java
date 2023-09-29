package com.firstcart_ecommerce.firstcart.controller;

import com.firstcart_ecommerce.firstcart.model.Cart;
import com.firstcart_ecommerce.firstcart.model.Product;
import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import com.firstcart_ecommerce.firstcart.services.ProductService;
import com.firstcart_ecommerce.firstcart.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @GetMapping("user/addToCart/{id}")
    public String addToCart(@PathVariable Long id, Principal principal) {
        Product product = productService.getProductById(id).orElse(null);
        if (product != null) {
            User user = userRepo.findByEmail(principal.getName());
            userService.addToUserCart(user, product);
        }
        return "redirect:/user/home";
    }

    @GetMapping("user/cart")
    public String getCart(Model model, Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        Cart userCart = userService.getUserCart(user);

        double totalCartAmount = userCart.getTotalCartAmount();

        model.addAttribute("cartCount", userCart.getProducts().size());
        model.addAttribute("total", totalCartAmount);
        model.addAttribute("cart", userCart.getProducts());

        return "user/cart";
    }
}
