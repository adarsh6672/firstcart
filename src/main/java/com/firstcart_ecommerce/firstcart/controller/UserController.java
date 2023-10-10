package com.firstcart_ecommerce.firstcart.controller;


import com.firstcart_ecommerce.firstcart.model.*;
import com.firstcart_ecommerce.firstcart.repository.*;
import com.firstcart_ecommerce.firstcart.services.*;
import com.firstcart_ecommerce.firstcart.util.AddressConverter;
import com.firstcart_ecommerce.firstcart.util.OrderStatus;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private ProductRepo productRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private AddressService addressService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemRepo orderItemRepo;

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
        m.addAttribute("address",addressRepo.findByUserAndDeletedFalse(user));
        return "user/addressestest";
    }
    @GetMapping("/profile/address/add")
    public String addAddress(Principal p, Model m,@RequestParam(value = "fromCheckout", required = false) boolean fromCheckout){
        String email = p.getName();
        User user = userRepo.findByEmail(email);
        m.addAttribute("user", user);
        if (fromCheckout) {
            m.addAttribute("fromCheckout", true);
        }
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
    public String updateAddress(@ModelAttribute Address address, Principal principal,@RequestParam(value = "fromCheckout", required = false) boolean fromCheckout) throws IOException {
        addressService.addAddressToUser(principal.getName(), address);

        if (fromCheckout) {
            return "redirect:/user/checkout";
        } else {
            return "redirect:/user/profile/address";
        }

    }

    @GetMapping("profile/address/delete/{id}")
    public String deleteAddress(@PathVariable (value = "id")Long id,Principal p){
        Address address= addressRepo.getById(id);
        String email = p.getName();
        User user = userRepo.findByEmail(email);
        try {
            if(address.isDefaultAddress()){
                addressService.deleteAddressById(id);
                Address firstAddress = user.getAddresses().stream().filter(addres -> !addres.isDeleted()).findFirst()
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
        Cart userCart=userService.getUserCart(user);
        m.addAttribute("cartitem",cartService.getCartItems(userCart.getId()));
        m.addAttribute("user", user);
        m.addAttribute("address",addressRepo.findByUserAndDeletedFalse(user));
        m.addAttribute("total",userCart.getTotalAmount()+40);
        m.addAttribute("deliveryCharge",40.0);
        return "user/checkout";
    }

    @PostMapping("/placeorder")
    public String orderplace(@RequestParam ("paymentMethod") String paymentMethod,
                                @RequestParam("selectedAddressId") Long selectedAddressId,
                                Principal p,HttpSession session){
        if(paymentMethod==null){
            session.setAttribute("msg","PLEASE SELECT PAYMENT METHOD");
            return "redirect:/user/checkout";
        }
        if(selectedAddressId==null||selectedAddressId==0){
            session.setAttribute("msg","PLEASE SELECT SHIPPING ADDRESS");
            return "redirect:/user/checkout";
        }
        Address selectedAddress=addressRepo.findById(selectedAddressId).orElse(null);
        User user = userRepo.findByEmail(p.getName());
        Order order=new Order();
        order.setOrderDateTime(LocalDateTime.now());
        order.setTotalAmount(userService.getUserCart(user).getTotalAmount()+40);
        order.setShippingAddress(selectedAddress);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(OrderStatus.CONFIRMED);
        orderService.placeOrder(user,order);
        orderRepo.save(order);
        orderService.deleteCartItems(user);


        return "redirect:/user/orderconfirmed";
    }

    @GetMapping("/orderconfirmed")
    public String orderConfirm(){

        return "user/ordersuccess";
    }

    @GetMapping("/orderlist")
    public String orderList(Principal p,Model m){
        User user=userRepo.findByEmail(p.getName());
        List <Order> orders= orderService.orderItemFind(user);
        m.addAttribute("orders",orders);
        return "user/OrderList";
    }

    @GetMapping("/order/orderview/{id}")
    public String orderView(@PathVariable ("id") Long orderId,Model m){
        m.addAttribute("order",orderRepo.getById(orderId));
        m.addAttribute("address",orderRepo.getById(orderId).getShippingAddress());
        m.addAttribute("deliveryCharge",40);
        return "user/Orderview";
    }
    @GetMapping("/order/cancel/{id}")
    public String orderCancel(@PathVariable ("id")Long orderId){
        Order order= orderRepo.getById(orderId);
        orderService.changeStock(order);
        order.setStatus(OrderStatus.CANCELED);
        orderRepo.save(order);
        return "redirect:/user/order/orderview/"+orderId;
    }

    @GetMapping("/order/return/{id}")
    public String orderReturn(@PathVariable ("id")Long orderId){
        Order order= orderRepo.getById(orderId);
        orderService.changeStock(order);
        order.setStatus(OrderStatus.RETURN);
        orderRepo.save(order);
        return "redirect:/user/order/orderview/"+orderId;
    }

    @GetMapping("/store/{categoryid}")
    public String showStore(@PathVariable("categoryid")int catId,Model m){
        List<Product>products=productService.getProductsByCategory(catId);
        m.addAttribute("products",products);

        return "user/store";
    }





}
