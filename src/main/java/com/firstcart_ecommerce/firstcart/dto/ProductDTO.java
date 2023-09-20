package com.firstcart_ecommerce.firstcart.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private int subCategoryId;
    private double price;
    private int stockQuantity;
    private String description;
    private List<String> imageNames;
    private List<MultipartFile> images;
}
