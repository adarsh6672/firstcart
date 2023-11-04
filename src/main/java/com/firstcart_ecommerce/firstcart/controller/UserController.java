package com.firstcart_ecommerce.firstcart.controller;


import com.firstcart_ecommerce.firstcart.config.RazorPayConfig;
import com.firstcart_ecommerce.firstcart.model.*;
import com.firstcart_ecommerce.firstcart.repository.*;
import com.firstcart_ecommerce.firstcart.services.*;
import com.firstcart_ecommerce.firstcart.util.AddressConverter;
import com.firstcart_ecommerce.firstcart.util.InvoiceGenerator;
import com.firstcart_ecommerce.firstcart.util.OrderStatus;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import java.util.Map;
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
    private WalletRepo walletRepo;

    @Autowired
    private RazorPayConfig razorPayConfig;

    @Autowired
    private CategoryOfferRepo categoryOfferRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private CouponUsageRepo couponUsageRepo;

    @Autowired
    private CartService cartService;

    @Autowired
    private PaymentRepo paymentRepo;
    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private WalletService walletService;

    @Autowired
    private AddressRepo addressRepo;
    @Autowired
    private WishListRepo wishListRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CouponRepo couponRepo;

    @Autowired
    private InvoiceGenerator invoiceGenerator;

    @Autowired
    private AddressService addressService;

    @Autowired
    private WishListService wishListService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductOfferRepo productOfferRepo;
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
            m.addAttribute("wishListCount",wishListService.getNumberOfItemsInWishlist(user));

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
        /*model.addAttribute("productService",productService);*/
        return "user/userindex";
    }

    @GetMapping("/viewproduct/{productId}")
    public String viewProduct(Model model, @PathVariable Long productId, Principal principal) {
        Optional<Product> product = productService.getProductById(productId);
        User u=userRepo.findByEmail(principal.getName());
        Long cid=cartRepo.findByUser(u).getId();
        WishList wl=wishListService.getOrCreateUserCart(u);
        boolean isInCart = cartService.isProductInCartItem(cid, productId);
        boolean isInWL= wishListService.isProductInWishlist(wl,productId);
        ProductOffer productOffer=productOfferRepo.findByProduct_Id(productId);
        CategoryOffer categoryOffer=categoryOfferRepo.findBySubCategory_id(product.get().getSubCategory().getId());



        if (product.isPresent()) {
            model.addAttribute("isInCart",isInCart);
            model.addAttribute("isInWL",isInWL);
            model.addAttribute("product", product.get());
            if(productService.getOfferPrice(productId)!=product.get().getPrice()) {
                model.addAttribute("offerPrice", productService.getOfferPrice(productId));
            }
            model.addAttribute("productOffer",productOffer);
            model.addAttribute("categoryOffer",categoryOffer);
            model.addAttribute("pageTitle", "Product Details | Admin");


            return "user/product_open";
        } else {
            return "productNotFound";
        }
    }

    @GetMapping("/checkout")
    public String checkout(Principal p,Model m,
                           @RequestParam (required = false) Long selectedCoupon,
                           HttpSession session){

        String email = p.getName();
        User user = userRepo.findByEmail(email);
        Cart userCart=userService.getUserCart(user);
        List<CartItem> cartItems = cartService.getCartItems(userCart.getId());
        m.addAttribute("cartitem",cartItems);
        m.addAttribute("user", user);
        m.addAttribute("address",addressRepo.findByUserAndDeletedFalse(user));
        m.addAttribute("total",cartService.findTotalAfterOffer(cartItems)+40);
        m.addAttribute("deliveryCharge",40.0);
        m.addAttribute("coupons",couponRepo.findItemsNotInTable2(user.getId()));
        if(selectedCoupon!=null){
            long couponId = selectedCoupon.longValue();
            if(userCart.getTotalAmount()>couponRepo.getById(couponId).getMinimumAmount().longValue()) {
                m.addAttribute("coupenapplied", couponRepo.getById(couponId).getDiscountPercentage());
                m.addAttribute("selectedcouponId",couponId);
                m.addAttribute("total", cartService.findTotalAfterOffer(cartItems) + 40 - couponRepo.getById(couponId).getDiscountPercentage());
                boolean iscoupenApplied = true;
                m.addAttribute("isCouponApplied", iscoupenApplied);
                session.setAttribute("msg", "Coupon " + couponRepo.getById(couponId).getCouponCode() + " Applied Successfully");
            }else {
                session.setAttribute("msg","The minimum cart value should be "+couponRepo.getById(couponId).getMinimumAmount()+" for Applying this coupon");
            }

        }
        return "user/checkout";
    }

    @GetMapping("/buyNow")
    public String buyNow(Principal p,Model m,@RequestParam Long productId, @RequestParam int quantity,
    @RequestParam (required = false) Long selectedCoupon,
                         HttpSession session){
        String email = p.getName();
        User user = userRepo.findByEmail(email);
        Product product=productRepo.getById(productId);
        m.addAttribute("product",product);
        m.addAttribute("coupon",couponRepo.findAll());
        m.addAttribute("user", user);
        m.addAttribute("quantity",quantity);
        m.addAttribute("address",addressRepo.findByUserAndDeletedFalse(user));
        m.addAttribute("total",(productService.getOfferPrice(productId)*quantity)+40);
        m.addAttribute("deliveryCharge",40.0);
        m.addAttribute("coupons",couponRepo.findItemsNotInTable2(user.getId()));
        if(selectedCoupon!=null){
            long couponId = selectedCoupon.longValue();
            if(product.getPrice()*quantity>couponRepo.getById(couponId).getMinimumAmount().longValue()) {
                m.addAttribute("coupenapplied", couponRepo.getById(couponId).getDiscountPercentage());
                m.addAttribute("selectedcouponId",couponId);
                m.addAttribute("total", (productService.getOfferPrice(productId)*quantity)+40 - couponRepo.getById(couponId).getDiscountPercentage());
                boolean iscoupenApplied = true;
                m.addAttribute("isCouponApplied", iscoupenApplied);
                session.setAttribute("msg", "Coupon " + couponRepo.getById(couponId).getCouponCode() + " Applied Successfully");
            }else {
                session.setAttribute("msg","The minimum cart value should be "+couponRepo.getById(couponId).getMinimumAmount()+" for Applying this coupon");
            }

        }
        return "user/checkout_buy";
    }

    @PostMapping("/placeorder")
    public String orderplace(@RequestParam ("paymentMethod") String paymentMethod,
                                @RequestParam("selectedAddressId") Long selectedAddressId,
                                @RequestParam("paymentId") String paymentId,
                                @RequestParam(required = false,name = "couponId") Long couponId,
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
        Cart userCart=userService.getUserCart(user);
        List<CartItem> cartItems = cartService.getCartItems(userCart.getId());
        Order order=new Order();
        order.setOrderDateTime(LocalDateTime.now());
        order.setTotalAmount(cartService.findTotalAfterOffer(cartItems)+40);
        order.setShippingAddress(selectedAddress);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(OrderStatus.CONFIRMED);
        orderService.placeOrder(user,order);
        orderService.deleteCartItems(user);
        System.out.println(paymentId);
        if(paymentId != null && !paymentId.isEmpty()){
            Payment myorder = paymentRepo.findByOrderId(paymentId);
            myorder.setOrderNumber(order);
            paymentRepo.save(myorder);
        }

        if(couponId != null){
            Coupon coupon = couponRepo.getById(couponId);
            order.setCoupon(coupon);
            order.setTotalAmount(cartService.findTotalAfterOffer(cartItems)+40-couponRepo.getById(couponId).getDiscountPercentage());

            CouponUsage couponUsage=new CouponUsage();
            couponUsage.setCoupon(coupon);
            couponUsage.setUser(userRepo.findByEmail(p.getName()));
            couponUsage.setUsedAt(LocalDateTime.now());
            couponUsageRepo.save(couponUsage);
        }
        orderRepo.save(order);



        return "redirect:/user/orderconfirmed";
    }
    @PostMapping("/placebuyorder")
    public String orderbuyplace(@RequestParam ("paymentMethod") String paymentMethod,
                             @RequestParam("selectedAddressId") Long selectedAddressId,
                             @RequestParam("paymentId") String paymentId,
                             @RequestParam("quantity")int quantity,
                             @RequestParam("productId") Long productId,
                             @RequestParam("cartTotal") double total,
                                @RequestParam(required = false,name = "couponId") Long couponId,
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
        Product product=productRepo.getById(productId);
        Order order=new Order();
        order.setOrderDateTime(LocalDateTime.now());
        order.setTotalAmount(total);
        order.setShippingAddress(selectedAddress);
        order.setPaymentMethod(paymentMethod);
        order.setStatus(OrderStatus.CONFIRMED);
        orderService.placeOrderbuynow(product,order,user,quantity);

        System.out.println(paymentId);
        if(paymentId != null && !paymentId.isEmpty()){
            Payment myorder = paymentRepo.findByOrderId(paymentId);
            myorder.setOrderNumber(order);
            paymentRepo.save(myorder);
        }
        if(couponId != null){
            Coupon coupon = couponRepo.getById(couponId);
            order.setCoupon(coupon);

            CouponUsage couponUsage=new CouponUsage();
            couponUsage.setCoupon(coupon);
            couponUsage.setUser(userRepo.findByEmail(p.getName()));
            couponUsage.setUsedAt(LocalDateTime.now());
            couponUsageRepo.save(couponUsage);
        }
        orderRepo.save(order);


        return "redirect:/user/orderconfirmed";
    }

    @GetMapping("/orderconfirmed")
    public String orderConfirm(){

        return "user/ordersuccess";
    }

    @GetMapping("/orders/invoice/{orderId}")
    public void generateInvoice(@PathVariable Long orderId, HttpServletResponse response) {
        try {
            Order order = orderRepo.getById(orderId);
            if (order != null) {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=invoice_" + orderId + ".pdf");
                invoiceGenerator.generateInvoice(order, response.getOutputStream());
            }
        } catch (IOException e) {
            // Handle exceptions
        }
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
    public String orderCancel(@PathVariable ("id")Long orderId,Principal principal) throws RazorpayException {
        Order order = orderRepo.getById(orderId);
        orderService.changeStock(order);
        order.setStatus(OrderStatus.CANCELED);
        orderRepo.save(order);
        String paymentmethod="Online Payment";
        if (order.getPaymentMethod().equals(paymentmethod)){
            orderService.refundProcess(userRepo.findByEmail(principal.getName()),order);
        }
        return "redirect:/user/order/orderview/"+orderId;
    }

    @GetMapping("/order/return/{id}")
    public String orderReturn(@PathVariable ("id")Long orderId , Principal principal){
        Order order= orderRepo.getById(orderId);
        orderService.changeStock(order);
        order.setStatus(OrderStatus.RETURN);
        String paymentmethod="Online Payment";
        if (order.getPaymentMethod().equals(paymentmethod)){
           orderService.refundProcess(userRepo.findByEmail(principal.getName()),order);
        }


        orderRepo.save(order);


        return "redirect:/user/order/orderview/"+orderId;
    }

    @GetMapping("/store/{categoryid}")
    public String showStore(@PathVariable("categoryid")int catId,Model m){
        List<Product>products=productService.getProductsByCategory(catId);
        m.addAttribute("products",products);

        return "user/store";
    }

    @PostMapping("/create_order")
    @ResponseBody
    public String createOrder(@RequestBody Map<String, Object> data, Principal principal) throws Exception {
        System.out.println(data);

        double amt = Double.parseDouble(data.get("amount").toString());
        var client = new RazorpayClient(razorPayConfig.getKey_id(), razorPayConfig.getKey_secret());

        JSONObject ob = new JSONObject();
        ob.put("amount", amt*100);
        ob.put("currency", "INR");
        ob.put("receipt", "txn_123435");

        //create new order
        com.razorpay.Order order = client.orders.create(ob);
        System.out.println(order);

        //save the order in database
        Payment myOrder = new Payment();
        myOrder.setAmount(order.get("amount")+ "");
        myOrder.setOrderId(order.get("id"));
        myOrder.setPaymentId(null);
        myOrder.setPaymentStatus("created");
        myOrder.setUser(userRepo.findByEmail(principal.getName()));
        myOrder.setReceipt(order.get("receipt"));
        paymentRepo.save(myOrder);

        return order.toString();
    }

    @PostMapping("/update_order")
    public ResponseEntity<?> updateOrder(@RequestBody Map<String, Object> data){

        Payment myorder = this.paymentRepo.findByOrderId(data.get("order_id").toString());
        myorder.setPaymentId(data.get("payment_id").toString());
        myorder.setPaymentStatus(data.get("status").toString());
        this.paymentRepo.save(myorder);
        paymentRepo.deletePaymentWithNullPaymentId();

        System.out.println(data);
        return ResponseEntity.ok(Map.of("msg", "updated"));
    }

    @GetMapping("/wallet")
    public String openWallet(Model model,Principal principal){
        User user=userRepo.findByEmail(principal.getName());
        Wallet wallet=walletService.getOrCreateUserWallet(user);
        model.addAttribute("walletBalance",wallet.getAmount());
        return "/user/wallet";
    }





}
