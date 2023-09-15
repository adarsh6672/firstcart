package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category ,Integer> {

}
