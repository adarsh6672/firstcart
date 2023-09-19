package com.firstcart_ecommerce.firstcart.controller;


import com.firstcart_ecommerce.firstcart.dto.CategorySubCategoryDTO;
import com.firstcart_ecommerce.firstcart.dto.ProductDTO;
import com.firstcart_ecommerce.firstcart.model.Category;
import com.firstcart_ecommerce.firstcart.model.SubCategory;
import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.CategoryRepo;
import com.firstcart_ecommerce.firstcart.repository.SubCategoryRepo;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import com.firstcart_ecommerce.firstcart.services.CategoryService;
import com.firstcart_ecommerce.firstcart.services.SubCategoryService;
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
    private SubCategoryService subCategoryService;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubCategoryRepo subCategoryRepo;




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


    @GetMapping("/categories")
    public String listCategories(Model model) {
        List<CategorySubCategoryDTO> categorySubCategoryDTOList = subCategoryRepo.getCategorySubCategoryJoin();
        model.addAttribute("categorySubCategoryDTOList", categorySubCategoryDTOList);

        return "admin/category-list"; // Thymeleaf view name
    }


    @GetMapping("/subcategories/new")
        public String showAddForm(Model model) {
            model.addAttribute("subcategory", new SubCategory());
            model.addAttribute("categories", categoryService.getAllCategory());
        return "admin/add_subcat";
    }

    @PostMapping("/addsubcategory")
    public String addSubcategory(@ModelAttribute SubCategory subcategory,
                                 @RequestParam("category_id") int categoryId) {
        Category category = categoryRepo.findById(categoryId).orElseThrow();
        subcategory.setCategory(category);
        subCategoryRepo.save(subcategory);
        return "redirect:/admin/subcategories/new";
    }
    @PostMapping("/category/delete/{id}")
    public String deleteCat(@PathVariable (value = "id")int id) {
        subCategoryRepo.deleteById(id);
        return "redirect:/admin/categories";
    }

    @GetMapping("/product/add")
    public String addproduct(Model model){
        model.addAttribute("categories",subCategoryService.getAllSubCategories());
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("newCategory", new SubCategory());

        return "admin/add_product";
    }
}

