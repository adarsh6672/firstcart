package com.firstcart_ecommerce.firstcart.controller;


import com.firstcart_ecommerce.firstcart.dto.CategorySubCategoryDTO;
import com.firstcart_ecommerce.firstcart.dto.ProductDTO;
import com.firstcart_ecommerce.firstcart.model.*;
import com.firstcart_ecommerce.firstcart.repository.CategoryRepo;
import com.firstcart_ecommerce.firstcart.repository.SubCategoryRepo;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import com.firstcart_ecommerce.firstcart.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
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

    @Autowired
    private ProductService productService;

    @Autowired
    S3Service s3Service;




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

    @PostMapping("/product/add")
    public String addProductIn(@ModelAttribute("productDTO") ProductDTO productDTO,
                               @RequestParam("productImages") List<MultipartFile> files,
                               @RequestParam("imgNames") List<String> imgNames,
                                Model model)throws IOException {
        Product product=new Product();
        product.setName(productDTO.getName());
        product.setSubCategory(subCategoryService.getSubCategoryById(productDTO.getSubCategoryId()).get());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setDescription(productDTO.getDescription());

        if(productService.isProductNameExists(product.getName())){
            model.addAttribute("error", "Product name already exists");
            return "admin/add_product";
        }
        List<ProductImage> images = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String imageUUID;

            if (!file.isEmpty()) {
                imageUUID = file.getOriginalFilename();
                s3Service.saveFile(file);


            } else {
                if (i < imgNames.size()) {
                    imageUUID = imgNames.get(i);
                } else {
                    imageUUID = "img/logo.png";
                }
            }

            ProductImage image = new ProductImage();
            image.setImageName(imageUUID);
            images.add(image);
        }

        /*for (ProductImage image : images) {
            Long imageId = productService.saveImageAndGetId(image.getImageName());
        }*/

        product.setImages(images);
        productService.addProduct(product);
        return "redirect:/admin/product/add";
    }
}

