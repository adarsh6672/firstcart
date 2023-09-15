package com.firstcart_ecommerce.firstcart.services;

import com.firstcart_ecommerce.firstcart.model.Category;
import com.firstcart_ecommerce.firstcart.repository.CategoryRepo;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    CategoryRepo categoryRepo;
    public List<Category> getAllCategory(){
        return categoryRepo.findAll();
    }

    public void addCategory(Category category){
        categoryRepo.save(category);
    }
}
