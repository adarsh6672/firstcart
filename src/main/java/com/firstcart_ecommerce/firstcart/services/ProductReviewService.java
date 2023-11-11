package com.firstcart_ecommerce.firstcart.services;

import com.firstcart_ecommerce.firstcart.model.Order;
import com.firstcart_ecommerce.firstcart.model.OrderItem;
import com.firstcart_ecommerce.firstcart.model.ProductReview;
import com.firstcart_ecommerce.firstcart.repository.OrderRepo;
import com.firstcart_ecommerce.firstcart.repository.ProductReviewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductReviewService {

    @Autowired
    private ProductReviewRepo productReviewRepository;

    @Autowired
    private OrderRepo orderRepo;

    public int getTotalReviewCount(Long productId) {
        return productReviewRepository.countByProductId(productId);
    }

    public List<ProductReview> getAllReviewsByProductId(Long productId) {
        return productReviewRepository.findByProductId(productId);
    }

    public int averageReview(Long productId){
        List<ProductReview> reviews=productReviewRepository.findByProductId(productId);
        if (reviews.isEmpty()) {
            return 0;
        }
        int sum = reviews.stream().mapToInt(ProductReview::getRating).sum();
        return sum/reviews.size();
    }

    public Map<Integer, Integer> getReviewCountByRating(Long productId) {
        List<Object[]> results = productReviewRepository.getReviewCountByRating(productId);

        Map<Integer, Integer> reviewCountByRating = new HashMap<>();
        for (Object[] result : results) {
            int rating = (int) result[0];
            long count = (long) result[1];
            reviewCountByRating.put(rating, (int) count);
        }
        for (int i = 1; i <= 5; i++) {
            reviewCountByRating.putIfAbsent(i, 0);
        }

        return reviewCountByRating.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
    }

    public boolean hasOrderedProduct(int userId, Long productId) {
        List<Order> orders = orderRepo.findAllByUserId(userId);

        for (Order order : orders) {
            for (OrderItem orderItem : order.getItems()) {
                if (orderItem.getProduct().getId()==productId) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasUserAddedReview(Long productId, int userId) {
        ProductReview review = productReviewRepository.findByProductIdAndUserId(productId, userId);
        return review != null;
    }
}
