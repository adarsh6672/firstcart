package com.firstcart_ecommerce.firstcart.services;

import com.firstcart_ecommerce.firstcart.model.CategoryOffer;
import com.firstcart_ecommerce.firstcart.model.Product;
import com.firstcart_ecommerce.firstcart.model.ProductImage;
import com.firstcart_ecommerce.firstcart.model.ProductOffer;
import com.firstcart_ecommerce.firstcart.repository.CategoryOfferRepo;
import com.firstcart_ecommerce.firstcart.repository.ProductImageRepo;
import com.firstcart_ecommerce.firstcart.repository.ProductOfferRepo;
import com.firstcart_ecommerce.firstcart.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    ProductRepo productRepo;

    @Autowired
    ProductImageRepo productImageRepo;

    @Autowired
    private CategoryOfferRepo categoryOfferRepo;

    @Autowired
    private ProductOfferRepo productOfferRepo;



    public boolean isProductNameExists(String productName) {
        return productRepo.existsByName(productName);
    }

    public boolean isProductIdExist(Long id) {

        return productRepo.existsById(id);
    }

    public void saveProduct(Product product) {
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

    public List<Product> getAllProduct() {
        return productRepo.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepo.findById(id);
    }

    public void removeProductById(Long id) {
        productRepo.deleteById(id);
    }

    public List<Product> getAllProductsSortedByQuantity() {
        Sort sort = Sort.by(Sort.Direction.ASC, "stockQuantity");
        return productRepo.findAll(sort);
    }

    public List<Product> getProductsByCategory(int categoryId) {
        return productRepo.findBySubCategoryId(categoryId);
    }

    public int getTotalProducts() {
        return (int) productRepo.count();
    }

    public List<Product> getProductsLowStock() {
        return productRepo.findLowStockProducts();
    }

    public List<Product> searchProducts(String searchQuery) {
        // Use the search query to filter the products
        List<Product> filteredProducts = productRepo.findByNameContainingIgnoreCase(searchQuery);
        return filteredProducts;
    }

    public double getOfferPrice(Long productId){
            Optional<Product> product=getProductById(productId);
            double productPrice = product.get().getPrice();
            ProductOffer productOffer=productOfferRepo.findByProduct_Id(product.get().getId());
            CategoryOffer categoryOffer=categoryOfferRepo.findBySubCategory_id(product.get().getSubCategory().getId());
            if(productOffer != null && categoryOffer != null){
                double discountPercentage=productOffer.getDiscountPercentage()+categoryOffer.getDiscountPercentage();
                return productPrice-(productPrice*(discountPercentage/100));
            }
            if(productOffer != null){
                return productPrice-(productPrice*(productOffer.getDiscountPercentage()/100));
            }
            if(categoryOffer !=null){
                return productPrice-(productPrice*(categoryOffer.getDiscountPercentage()/100));
            }

        return productPrice;
    }
}