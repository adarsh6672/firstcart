package com.firstcart_ecommerce.firstcart.services;

import com.firstcart_ecommerce.firstcart.model.*;
import com.firstcart_ecommerce.firstcart.repository.*;
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
}
