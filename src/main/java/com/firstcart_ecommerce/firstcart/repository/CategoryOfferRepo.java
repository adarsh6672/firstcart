package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.model.CategoryOffer;
import com.firstcart_ecommerce.firstcart.model.ProductOffer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryOfferRepo extends JpaRepository<CategoryOffer,Long> {

    CategoryOffer findBySubCategory_id(int subCategoryId);
}
