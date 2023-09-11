package com.firstcart_ecommerce.firstcart.controller;


import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import com.firstcart_ecommerce.firstcart.services.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {
    @Autowired
    private UserServiceImpl userservice;
    @Autowired
    private UserRepo userRepo;


    @GetMapping("/")
    @Secured({"ROLE_ADMIN", "ROLE_USER"}) // Define the roles that can access this endpoint
    public String log() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "index"; // If not authenticated, show the login page.
        } else if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                return "redirect:/admin/home"; // Redirect regular users to the user home page.
        }
        return "redirect:/user/home";
    }


    @GetMapping("/register")
    @Secured({"ROLE_ADMIN", "ROLE_USER"}) // Define the roles that can access this endpoint
    public String register() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "register"; // If not authenticated, show the login page.
        } else if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/home"; // Redirect admins to the admin home page.
        }
        // Redirect to a default page if no matching role is found.
        return "redirect:/user/home";
    }




    @GetMapping("/login")
    @Secured({"ROLE_ADMIN", "ROLE_USER"}) // Define the roles that can access this endpoint
    public String login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login"; // If not authenticated, show the login page.
        }// Check user roles and redirect accordingly.
        else if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/home"; // Redirect admins to the admin home page.
        }
        // Redirect to a default page if no matching role is found.
        return "redirect:/user/home";
    }



    @PostMapping("/saveuser")
    public String saveUser(@ModelAttribute User user, HttpSession session){
        User u=userservice.saveUser(user);

        if(u!=null){
            System.out.println("successfuly saved");
            session.setAttribute("msg","REGISTRATION SUCCESSFUL..!");
        }else {
            System.out.println("something went wrong");
            session.setAttribute("msg","OOPS..! SOMETHING WENT WRONG");
        }
        return "redirect:/register";
    }







}
