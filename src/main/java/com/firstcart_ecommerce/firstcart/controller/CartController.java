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
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    private CartItemRepo cartItemRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserService userService;



    @GetMapping("/user/addToCart/{id}")
    public String addToCart(@PathVariable Long id, Principal principal) {
        Product product = productService.getProductById(id).orElse(null);
        if (product != null) {
            User user = userRepo.findByEmail(principal.getName());
            userService.addToUserCart(user, product);
        }
        return "redirect:/user/viewproduct/{id}";
    }

    @GetMapping("/user/cart")
    public String getCart(Model model, Principal principal) {
        User user = userRepo.findByEmail(principal.getName());
        Cart userCart = userService.getUserCart(user);
        Long a=userCart.getId();
        List<CartItem> cartItems = cartService.getCartItems(a);
        double totalCartAmount = userCart.getTotalCartAmount();

        userCart.setTotalCartAmount(totalCartAmount);
        cartRepo.save(userCart);

        model.addAttribute("cartCount", userCart.getItems().size());
        model.addAttribute("total", totalCartAmount);
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
