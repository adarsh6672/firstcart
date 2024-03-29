package com.firstcart_ecommerce.firstcart.services;

import com.firstcart_ecommerce.firstcart.config.RazorPayConfig;
import com.firstcart_ecommerce.firstcart.model.*;
import com.firstcart_ecommerce.firstcart.repository.*;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private RazorPayConfig razorPayConfig;

    @Autowired
    private WalletRepo walletRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private OrderItemRepo orderItemRepo;

    public Order placeOrder(User user,Order order) {
        order.setUser(user);
        Cart cart=userService.getUserCart(user);

        List<CartItem> cartItems = cartService.getCartItems(cart.getId());
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            Product product=cartItem.getProduct();
            product.setStockQuantity(cartItem.getProduct().getStockQuantity()-cartItem.getQuantity());
            productRepo.save(product);
            order.getItems().add(orderItem);
        }


        orderRepo.save(order);

        return order;
    }
    public Order placeOrderbuynow(Product product,Order order,User user ,int quantity) {
        order.setUser(user);
        Cart cart=userService.getUserCart(user);

        /*List<CartItem> cartItems = cartService.getCartItems(cart.getId());
        for (CartItem cartItem : cartItems) {*/
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            product.setStockQuantity(product.getStockQuantity()-quantity);
            productRepo.save(product);
            order.getItems().add(orderItem);



        orderRepo.save(order);

        return order;
    }

    public void deleteCartItems(User user){
        Cart cart=cartRepo.findByUser(user);
        List<CartItem>cartItems=cartService.getCartItems(cart.getId());
        for(CartItem cartItem:cartItems){
            cartItemRepo.delete(cartItem);
        }


    }
    public int getTotalOrders() {
        return (int) orderRepo.count();
    }
    /*public int countOrdersCreatedToday() {
        LocalDate today = LocalDate.now();
        return (int) orderRepo.countByorderDateTimeAfter(today);
    }*/

    public List<Order> getRecentOrders() {
        // Create a Pageable object to fetch the top 5 recent orders sorted by orderDateTime
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "orderDateTime"));

        // Fetch the recent orders
        return orderRepo.findAll(pageable).getContent();
    }

    public List<Order> orderItemFind(User user){
        return orderRepo.findByUserOrderByOrderDateTimeDesc(user);
    }

    public void changeStock(Order order) {

       List<OrderItem> oi=order.getOrderItems();
       for(OrderItem orderItem:oi){
           Product p=orderItem.getProduct();
           p.setStockQuantity(orderItem.getProduct().getStockQuantity()+orderItem.getQuantity());
           productRepo.save(p);
       }
    }

    public void refundProcess(User user,Order order){
        Wallet wallet = walletService.getOrCreateUserWallet(user);
        wallet.setAmount(wallet.getAmount()+order.getTotalAmount());
        walletRepo.save(wallet);
       /* try {
            Payment payment=paymentRepo.findByOrderNumber(order);
            RazorpayClient razorpayClient = new RazorpayClient(razorPayConfig.getKey_id(), razorPayConfig.getKey_secret());
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", order.getTotalAmount()*100); // Refund amount in paise (e.g., 10000 paise = ₹100)
            refundRequest.put("speed", "optimum"); // Set the refund speed to "optimum"
            refundRequest.put("receipt","Receipt No."+order.getId());
            razorpayClient.payments.refund(payment.getPaymentId(),refundRequest);


        } catch (RazorpayException e) {
            e.printStackTrace();
        }*/
    }
}
