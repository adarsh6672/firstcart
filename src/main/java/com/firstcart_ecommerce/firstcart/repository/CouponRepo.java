package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponRepo extends JpaRepository<Coupon ,Long> {
    @Query("SELECT c FROM Coupon c WHERE c.id NOT IN (SELECT cu.coupon FROM CouponUsage cu WHERE cu.user.id =:userid)AND c.isDeleted = false")
    List<Coupon> findItemsNotInTable2(@Param("userid") int userId);

    /*@Query("SELECT c FROM Coupon c WHERE c.id NOT IN (SELECT cu.coupon FROM CouponUsage cu WHERE cu.user.id = :userid AND cu.coupen.id = :couponid)")
    List<Coupon> findCoupenNotUsed(@Param("userid") Long userId, @Param("couponid") Long CouponId);*/

    List<Coupon> findByIsDeletedFalse();
}
