package com.firstcart_ecommerce.firstcart.services;

import com.firstcart_ecommerce.firstcart.model.Cart;
import com.firstcart_ecommerce.firstcart.model.CartItem;
import com.firstcart_ecommerce.firstcart.model.Product;
import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.repository.CartItemRepo;
import com.firstcart_ecommerce.firstcart.repository.CartRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartRepo cartRepo;
    @PersistenceContext
    EntityManager em;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private ProductService productService;
    

    public List<CartItem> getCartItems(Long cartId) {
        TypedQuery<CartItem> query = em.createQuery(
                "SELECT c FROM CartItem c WHERE c.cart.id = :cartId", CartItem.class
        );
        query.setParameter("cartId", cartId);
        return query.getResultList();
    }

    public boolean isProductInCartItem(Long cartId, Long productId) {
        TypedQuery<CartItem> query = em.createQuery(
                "SELECT c FROM CartItem c WHERE c.cart.id = :cartId AND c.product.id = :productId", CartItem.class
        );
        query.setParameter("cartId", cartId);
        query.setParameter("productId", productId);
        return !query.getResultList().isEmpty();
    }

    public double findTotalAfterOffer(List<CartItem> cartItems){
        double totalAfterOffer=0;
        for (CartItem item : cartItems) {
            totalAfterOffer+= productService.getOfferPrice(item.getProduct().getId())*item.getQuantity();
        }
        return totalAfterOffer;
    }

    public double findDiscountAmount(List<CartItem> cartItems){
        double offer=0;
        for (CartItem item : cartItems) {
            offer+= (productService.getOfferPrice(item.getProduct().getId())*item.getQuantity())-(item.getProduct().getPrice()* item.getQuantity());
        }
        return offer;
    }





}
