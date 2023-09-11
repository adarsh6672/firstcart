package com.firstcart_ecommerce.firstcart.controller;


import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @ModelAttribute
    public void profile(Principal p, Model m){
        if(p != null) {
            String email = p.getName();
            User user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
        }

    }
    @GetMapping("/profile")
    public String profile(){
        return "user/blank";
    }
    @GetMapping("/home")
    public String home(){
        return "user/userindex";
    }

}
