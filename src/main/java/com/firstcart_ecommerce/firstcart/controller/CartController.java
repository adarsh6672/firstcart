package com.firstcart_ecommerce.firstcart.controller;

import com.firstcart_ecommerce.firstcart.model.Cart;
import com.firstcart_ecommerce.firstcart.model.CartItem;
import com.firstcart_ecommerce.firstcart.model.Product;
import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.CartItemRepo;
import com.firstcart_ecommerce.firstcart.repository.CartRepo;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import com.firstcart_ecommerce.firstcart.services.CartService;
import com.firstcart_ecommerce.firstcart.services.ProductService;
import com.firstcart_ecommerce.firstcart.services.UserService;
import com.firstcart_ecommerce.firstcart.services.WishListService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartService cartService;

    @Autowired
    private WishListService wishListService;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void profile(Principal p, Model m){
        if(p != null) {
            String email = p.getName();
            User user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
            Cart userCart = userService.getUserCart(user);
            m.addAttribute("cartProductCount", userCart.getItems().size());
            m.addAttribute("wishListCount",wishListService.getNumberOfItemsInWishlist(user));
        }

    }



    @GetMapping("/user/addToCart/{id}")
    public String addToCart(@PathVariable Long id, Principal principal,@RequestParam(value = "fromIndex", required = false) boolean fromCheckout) throws IOException {
        Product product = productService.getProductById(id).orElse(null);
        if (product != null) {
            User user = userRepo.findByEmail(principal.getName());
            userService.addToUserCart(user, product);
        }
        if(fromCheckout){
            return "redirect:/user/home";
        }
        return "redirect:/user/viewproduct/{id}";
    }

    @GetMapping("/user/cart")
    public String getCart(Model model, Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        Cart userCart = userService.getUserCart(user);
        Long a=userCart.getId();
        List<CartItem> cartItems = cartService.getCartItems(a);
        /*double totalAfterOffer=0;
        for (CartItem item : cartItems) {
            totalAfterOffer=totalAfterOffer+productService.getOfferPrice(item.getProduct().getId());
        }
        double totalCartAmount = userCart.getTotalCartAmount();*/
        double totalAfterOffer=cartService.findTotalAfterOffer(cartItems);


        userCart.setTotalCartAmount(totalAfterOffer);
        cartRepo.save(userCart);

        model.addAttribute("cartCount", userCart.getItems().size());
        model.addAttribute("total", totalAfterOffer);
        model.addAttribute("cartItems", cartItems);

        return "user/cart";
    }

    @GetMapping("/user/cart/delete/{id}")
    public String deleteFromCart(@PathVariable Long id ,Principal principal){
        User user = userRepo.findByEmail(principal.getName());
        cartItemRepo.deleteById(id);
        return "redirect:/user/cart";
    }

    @GetMapping("/user/cart/addqty/{id}")
    public String addQty(@PathVariable Long id, HttpSession session){
        CartItem ci=cartItemRepo.getById(id);
        if(ci.getProduct().getStockQuantity()>ci.getQuantity()) {
            try {
                ci.setQuantity(ci.getQuantity() + 1);
                cartItemRepo.save(ci);
                return "redirect:/user/cart";
            } catch (Exception e) {
                session.setAttribute("msg", "Maximum Quantity is 5");
                return "redirect:/user/cart";
            }
        }else {
            session.setAttribute("msg","product out of stock....You can purchase "+ci.getQuantity()+"item");
            return "redirect:/user/cart";
        }

    }

    @GetMapping("/user/cart/minusqty/{id}")
    public String reduceQty(@PathVariable Long id, HttpSession session){
        CartItem ci=cartItemRepo.getById(id);
       try {
           ci.setQuantity(ci.getQuantity()-1);
           cartItemRepo.save(ci);
           return "redirect:/user/cart";

       }catch (Exception e){
           session.setAttribute("msg","Minimum Quantity is 1");
           return "redirect:/user/cart";
       }
    }


}
