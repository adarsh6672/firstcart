package com.firstcart_ecommerce.firstcart.controller;

import com.firstcart_ecommerce.firstcart.model.Product;
import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.model.WishList;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import com.firstcart_ecommerce.firstcart.services.UserService;
import com.firstcart_ecommerce.firstcart.services.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user/wishlist")
public class WishListController {

    @Autowired
    private WishListService wishlistService;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo userRepo;



    @GetMapping("/add/{productId}")
    public String addProductToWishlist(Principal principal, @PathVariable Long productId) {
        User user=userRepo.findByEmail(principal.getName());
        WishList wishList=wishlistService.getOrCreateUserCart(user);
        wishlistService.addProductToWishlist(wishList.getId(), productId);
        return "redirect:/user/viewproduct/{productId}";
    }

    @GetMapping("/remove/{productId}")
    public String removeProductFromWishlist(Principal principal, @PathVariable Long productId) {
        User user=userRepo.findByEmail(principal.getName());
        WishList wishList=wishlistService.getOrCreateUserCart(user);
        wishlistService.removeProductFromWishlist(wishList.getId(), productId);
        return "redirect:/user/wishlist/products";
    }

    @GetMapping("/products")
   public String getProductsInWishlist(Model model,Principal principal) {
        User user=userRepo.findByEmail(principal.getName());
        WishList wishList=wishlistService.getOrCreateUserCart(user);
         List<Product> products= wishlistService.getProductsInWishlist(wishList.getId());
         model.addAttribute("products" ,products);

        return "user/wishlist";
    }

}
