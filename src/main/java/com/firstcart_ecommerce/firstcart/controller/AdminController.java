package com.firstcart_ecommerce.firstcart.controller;


import com.firstcart_ecommerce.firstcart.model.Category;
import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import com.firstcart_ecommerce.firstcart.services.CategoryService;
import com.firstcart_ecommerce.firstcart.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
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
    private CategoryService categoryService;

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
    @GetMapping("/manage")
    public String viewData(Model model){

        model.addAttribute("userdata",userService.getAllUsers());
        return "admin/users";

    }

    @GetMapping("/adminpanel")
    public String adminpanel(){
        return "admin/adminpanel";
    }
    /*users crud operations*/

        @PostMapping("/blockuser/{id}")
        public String blockUsr(@PathVariable (value ="id")int id ){
            userService.blockUser(id);

            return "redirect:/admin/manage";
        }

        @PostMapping("/unblockuser/{id}")
        public String unblockUsr(@PathVariable (value ="id")int id ){
            userService.unblockUser(id);

            return "redirect:/admin/manage";
        }



    @GetMapping("/deleteuser/{id}")
    public String deleteUser(@PathVariable (value = "id")int id){
        userRepo.deleteById(id);
        return "redirect:/admin/usermanage";
    }
/*user crud operation end*/

/*
 category crud operation start
*/

    @GetMapping("/category")
    public String categories(Model model){
        model.addAttribute("category",categoryService.getAllCategory());
        return "admin/categories";
    }


    @GetMapping("/category/add")
    public String addCat(Model model){
        model.addAttribute("category" ,new Category());
        return "admin/add_Parentcat";
    }

    @PostMapping("/category/adding")
    public String postCat(@ModelAttribute("category")Category category){
        categoryService.addCategory(category);
        return "redirect:/admin/category";
    }





    @GetMapping("/testing")
    public String tester(){
        return "admin/test";
    }
}

