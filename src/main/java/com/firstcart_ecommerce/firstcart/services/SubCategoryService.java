package com.firstcart_ecommerce.firstcart.services;

import com.firstcart_ecommerce.firstcart.model.SubCategory;
import com.firstcart_ecommerce.firstcart.repository.SubCategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubCategoryService {
    @Autowired
    SubCategoryRepo subCategoryRepo;
    public List<SubCategory> getAllSubCategories(){
      return subCategoryRepo.findAll();
    }
}
