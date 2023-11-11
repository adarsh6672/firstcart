package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.model.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductReviewRepo extends JpaRepository<ProductReview , Long> {

    int countByProductId(Long productId);

    List<ProductReview> findByProductId(Long productId);

    @Query("SELECT pr.rating, COUNT(pr) FROM ProductReview pr WHERE pr.product.id = :productId GROUP BY pr.rating")
    List<Object[]> getReviewCountByRating(@Param("productId") Long productId);

    ProductReview findByProductIdAndUserId(Long productId, int userId);
}
