package com.firstcart_ecommerce.firstcart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategorySubCategoryDTO {
    private Integer categoryId;
    private String categoryName;
    private Integer subCategoryId;
    private String subCategoryName;
}
