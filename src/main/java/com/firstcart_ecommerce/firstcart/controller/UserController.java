package com.firstcart_ecommerce.firstcart.controller;


import com.firstcart_ecommerce.firstcart.model.Address;
import com.firstcart_ecommerce.firstcart.model.Product;
import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import com.firstcart_ecommerce.firstcart.services.ProductService;
import com.firstcart_ecommerce.firstcart.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;
    @ModelAttribute
    public void profile(Principal p, Model m){
        if(p != null) {
            String email = p.getName();
            User user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
        }

    }
    @GetMapping("/profile")
    public String profileview(Principal p, Model m){
        if(p != null) {
            String email = p.getName();
            User user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
        }
        return "user/pro";
    }
    @GetMapping("/profile/edit")
    public String editProfile(Principal p, Model m){
        String email = p.getName();
        User user = userRepo.findByEmail(email);
        m.addAttribute("user", user);
        return "user/Edit_Details";
    }
    @PostMapping("/saveuser")
    public String saveUser(@ModelAttribute User user, HttpSession session){

        User u=userRepo.save(user);

        if(u!=null){
            System.out.println("successfuly saved");
            session.setAttribute("msg","UPDATION SUCCESSFUL..!");
        }else {
            System.out.println("something went wrong");
            session.setAttribute("msg","UPDATION FAILED.! SOMETHING WENT WRONG");
        }
        return "redirect:/user/profile";
    }
    @GetMapping("/profile/address")
    public String editaddress(Principal p, Model m){
        String email = p.getName();
        User user = userRepo.findByEmail(email);
        m.addAttribute("user", user);
        return "user/addressestest";
    }
    @GetMapping("/profile/address/add")
    public String addAddress(Principal p, Model m){
        String email = p.getName();
        User user = userRepo.findByEmail(email);
        m.addAttribute("user", user);
        return "user/add_address";
    }

    @PostMapping("profile/add-address")
    public String addAddress(@ModelAttribute Address address, Principal principal) {
        userService.addAddressToUser(principal.getName(), address);
        return "redirect:/user/profile";

    }
    @GetMapping("/profile/password")
    public String changepassword(Principal p, Model m){
        String email = p.getName();
        User user = userRepo.findByEmail(email);
        m.addAttribute("user", user);
        return "user/change_password";
    }
    @PostMapping("/profile/updatepassword")
    public String updatePassword(@ModelAttribute User user,HttpSession session,@RequestParam String oldPassword,@RequestParam String newPassword){

        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            userRepo.save(user);
            session.setAttribute("msg","PASSWORD UPDATED SUCCESSFULLY....!");
            return "redirect:/user/profile";
        } else {
            session.setAttribute("msg","CURRENT PASSWORD IS INCORRECT...TRY AGAIN...!");
            return "redirect:/user/profile/password";
        }

        /*return "redirect:/user/profile";*/
    }


    @GetMapping("/home")
    public String home(Model model){
        List<Product> product1=productService.getAllProduct();
        model.addAttribute("products",product1);
        return "user/userindex";
    }

    @GetMapping("/viewproduct/{productId}")
    public String viewProduct(Model model, @PathVariable Long productId, Principal principal) {
        Optional<Product> product = productService.getProductById(productId);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("pageTitle", "Product Details | Admin");
            return "user/product_open";
        } else {
            return "productNotFound";
        }
    }





}
