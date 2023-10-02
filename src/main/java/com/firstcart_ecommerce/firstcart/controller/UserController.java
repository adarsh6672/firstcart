package com.firstcart_ecommerce.firstcart.controller;


import com.firstcart_ecommerce.firstcart.model.Address;
import com.firstcart_ecommerce.firstcart.model.Cart;
import com.firstcart_ecommerce.firstcart.model.Product;
import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.AddressRepo;
import com.firstcart_ecommerce.firstcart.repository.CartRepo;
import com.firstcart_ecommerce.firstcart.repository.UserRepo;
import com.firstcart_ecommerce.firstcart.services.AddressService;
import com.firstcart_ecommerce.firstcart.services.CartService;
import com.firstcart_ecommerce.firstcart.services.ProductService;
import com.firstcart_ecommerce.firstcart.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    private CartService cartService;

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ProductService productService;
    @ModelAttribute
    public void profile(Principal p, Model m){
        if(p != null) {
            String email = p.getName();
            User user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
            Cart userCart = userService.getUserCart(user);
            m.addAttribute("cartProductCount", userCart.getItems().size());
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
        return "redirect:/user/profile/address";

    }
    @GetMapping("/profile/address/update/{id}")
    public String updateAddresses(@PathVariable (value = "id")Long id, Model m){
        Address address = addressRepo.getById(id);
        m.addAttribute("address",address);
        return "user/address_update";
    }
    @PostMapping("profile/address/update")
    public String updateAddress(@ModelAttribute Address address, Principal principal) throws IOException {
        addressService.addAddressToUser(principal.getName(), address);
        return "redirect:/user/profile/address";

    }

    @GetMapping("profile/address/delete/{id}")
    public String deleteAddress(@PathVariable (value = "id")Long id,Principal p){
        Address address= addressRepo.getById(id);
        String email = p.getName();
        User user = userRepo.findByEmail(email);
        try {
            if(address.isDefaultAddress()){
                addressService.deleteAddressById(id);
                Address firstAddress = user.getAddresses().stream().findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("address not found"));

                firstAddress.setDefaultAddress(true);
                addressRepo.save(firstAddress);

            }else {
                addressService.deleteAddressById(id);
            }
        }catch (EntityNotFoundException e){
            return "redirect:/user/profile/address";
        }
        return "redirect:/user/profile/address";
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
        User u=userRepo.findByEmail(principal.getName());
        Long cid=cartRepo.findByUser(u).getId();
        boolean isInCart = cartService.isProductInCartItem(cid, productId);
        if (product.isPresent()) {
            model.addAttribute("isInCart",isInCart);
            model.addAttribute("product", product.get());
            model.addAttribute("pageTitle", "Product Details | Admin");

            return "user/product_open";
        } else {
            return "productNotFound";
        }
    }

    @GetMapping("/checkout")
    public String test(Principal p,Model m){
        String email = p.getName();
        User user = userRepo.findByEmail(email);
        m.addAttribute("user", user);
        return "user/checkout";
    }







}
