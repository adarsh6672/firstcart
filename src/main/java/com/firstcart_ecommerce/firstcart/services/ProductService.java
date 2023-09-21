package com.firstcart_ecommerce.firstcart.services;

import com.firstcart_ecommerce.firstcart.model.Product;
import com.firstcart_ecommerce.firstcart.model.ProductImage;
import com.firstcart_ecommerce.firstcart.repository.ProductImageRepo;
import com.firstcart_ecommerce.firstcart.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    ProductRepo productRepo;

    @Autowired
    ProductImageRepo productImageRepo;

    public boolean isProductNameExists(String productName){
        return productRepo.existsByName(productName);
    }

    public boolean isProductIdExist(Long id){

        return productRepo.existsById(id);}

    public void saveProduct(Product product){
        productRepo.save(product);
    }




    public void addProduct(Product product) {
        for (ProductImage image : product.getImages()) {
            image.setProduct(product);
        }
        productRepo.save(product);
    }



    public Long saveImageAndGetId(String imageName) {
        ProductImage image = new ProductImage();
        image.setImageName(imageName);
        ProductImage savedImage = productImageRepo.save(image);

        return savedImage.getId();
    }

    public List<Product> getAllProduct(){
        return productRepo.findAll();
    }

    public Optional<Product> getProductById(Long id){
        return productRepo.findById(id);
    }

    public void removeProductById(Long id){
        productRepo.deleteById(id);
    }
}
