package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.dto.CategorySubCategoryDTO;
import com.firstcart_ecommerce.firstcart.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepo extends JpaRepository<Category ,Integer> {


}
