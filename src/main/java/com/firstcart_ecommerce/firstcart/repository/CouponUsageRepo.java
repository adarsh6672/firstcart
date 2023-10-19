package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.model.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUsageRepo extends JpaRepository<CouponUsage , Long> {
}
