package com.firstcart_ecommerce.firstcart.services;

import com.firstcart_ecommerce.firstcart.model.Cart;
import com.firstcart_ecommerce.firstcart.model.Product;
import com.firstcart_ecommerce.firstcart.model.User;
import com.firstcart_ecommerce.firstcart.model.WishList;
import com.firstcart_ecommerce.firstcart.repository.ProductRepo;
import com.firstcart_ecommerce.firstcart.repository.WishListRepo;
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

    public WishList getOrCreateUserCart(User user) {
        WishList wishList = wishlistRepo.findByUser(user);
        if (wishList == null) {
            wishList = new WishList();
            wishList.setUser(user);
            wishlistRepo.save(wishList);
        }
        return wishList;
    }
}
