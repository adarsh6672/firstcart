package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepo extends JpaRepository<ProductImage,Long> {
}
