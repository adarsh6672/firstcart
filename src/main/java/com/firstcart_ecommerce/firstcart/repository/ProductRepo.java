package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.model.Product;
import com.firstcart_ecommerce.firstcart.model.SubCategory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product,Long> {
    boolean existsByName(String name);

    List<Product> findAll(Sort sort);

    List<Product> findBySubCategoryId(int subCategoryId);

    @Query(value = "SELECT * FROM product p WHERE "+
            "p.name LIKE CONCAT('%',:query,'%' )",nativeQuery = true)
    List<Product> searchProduct(String query);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity < 10")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p JOIN p.subCategory sc WHERE sc.isListed = true")
    List<Product> findListedProducts();
    List<Product> findByNameContainingIgnoreCase(String searchQuery);
}
