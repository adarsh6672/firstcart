package com.firstcart_ecommerce.firstcart.services;

import com.amazonaws.services.alexaforbusiness.model.NotFoundException;
import com.firstcart_ecommerce.firstcart.model.*;
import com.firstcart_ecommerce.firstcart.repository.ProductRepo;
import com.firstcart_ecommerce.firstcart.repository.WishListRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.apache.tomcat.websocket.server.WsRemoteEndpointImplServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishListService {
    @Autowired
    private WishListRepo wishlistRepo;
    @Autowired
    private ProductRepo productRepo;

    @PersistenceContext
    EntityManager em;
    public void addProductToWishlist(Long wishlistId, Long productId) {
        WishList wishlist = wishlistRepo.findById(wishlistId).get();
        Product product = productRepo.findById(productId).get();
        wishlist.getProducts().add(product);
        wishlistRepo.save(wishlist);
    }

    public void removeProductFromWishlist(Long wishlistId, Long productId) {
        WishList wishlist = wishlistRepo.findById(wishlistId).get();
        Product product = productRepo.findById(productId).get();
        wishlist.getProducts().remove(product);
        wishlistRepo.save(wishlist);
    }

    public List<Product> getProductsInWishlist(Long wishlistId) {
        WishList wishlist = wishlistRepo.findById(wishlistId).get();
        return wishlist.getProducts();
    }
    public int getNumberOfItemsInWishlist(User user) {
        WishList wishList=getOrCreateUserCart(user);
        /*WishList wishlist = wishlistRepo.findById(wishlistId).orElseThrow(() -> new NotFoundException("Wishlist not found"));*/
        return wishList.getProducts().size();
    }

    public WishList getOrCreateUserCart(User user) {
        WishList wishList = wishlistRepo.findByUser(user);
        if (wishList == null) {
            wishList = new WishList();
            wishList.setUser(user);
            wishlistRepo.save(wishList);
        }
        return wishList;
    }

    public boolean isProductInWL(Long wishlistid, Long productId) {
        TypedQuery<WishList> query = em.createQuery(
                "SELECT c FROM WishList c WHERE c.id = :wlId AND c.product.id = :productId", WishList.class
        );
        query.setParameter("wlId", wishlistid);
        query.setParameter("productId", productId);
        return !query.getResultList().isEmpty();
    }

    public boolean isProductInWishlist(WishList wishlist,Long productId) {
        List<Product> products = wishlist.getProducts();
        for (Product p : products) {
            if (p.getId() == productId) {
                return true;
            }
        }
        return false;
    }

}
