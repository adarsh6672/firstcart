package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.dto.CategorySubCategoryDTO;
import com.firstcart_ecommerce.firstcart.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubCategoryRepo extends JpaRepository<SubCategory ,Integer> {
    @Query("SELECT NEW com.firstcart_ecommerce.firstcart.dto.CategorySubCategoryDTO(c.id, c.name, s.id, s.name,s.isListed) " +
            "FROM Category c " +
            "INNER JOIN SubCategory s ON c.id = s.category.id")
    List<CategorySubCategoryDTO> getCategorySubCategoryJoin();

    List<SubCategory> findByIsListedTrue();

    List<SubCategory> findByIsListedFalse();
}
