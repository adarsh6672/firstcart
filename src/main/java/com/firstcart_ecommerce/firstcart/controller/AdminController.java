package com.firstcart_ecommerce.firstcart.controller;


import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import com.firstcart_ecommerce.firstcart.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;


    @Autowired
    private UserRepo userRepo;






    @GetMapping("/profile")

    public String profile(Principal p, Model m){
        if(p != null) {
            String email = p.getName();
            User user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
        }
        return "admin_profile";
    }
    @GetMapping("/home")
    public String adhome(){
        return "user/userindex";
    }
    @GetMapping("/management")
    public String viewData(Model model){
        model.addAttribute("userdata",userService.getAllUsers());
        return "management";

    }
    @GetMapping("/userform")
    public String userForm(){
        return "newregistration";
    }
    @PostMapping("/savenewuser")
    public String saveUser(@ModelAttribute User nuser){
        userService.saveUser(nuser);

        return "redirect:management";
    }
        @GetMapping("/updateForm/{id}")
        public String updateForm(@PathVariable (value ="id")int id ,Model model){
            Optional<User> user=userRepo.findById(id);
            User usr=user.get();
            model.addAttribute("users" , usr);
            return "updationform";
        }

        @GetMapping("/updationform")
        public String updateuser(){
        return "updationform";
        }

    @PostMapping("/updateuser")
    public String updateUser(@ModelAttribute User users){


        return "redirect:management";
    }
    @GetMapping("/deleteuser/{id}")
    public String deleteUser(@PathVariable (value = "id")int id){
        userRepo.deleteById(id);
        return "redirect:/admin/management";
    }

    @GetMapping("/adminpanel")
    public String adminpanel(){
        return "admin/index";
    }

}

