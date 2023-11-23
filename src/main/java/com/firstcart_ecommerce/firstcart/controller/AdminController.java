package com.firstcart_ecommerce.firstcart.controller;


import com.firstcart_ecommerce.firstcart.dto.CategorySubCategoryDTO;
import com.firstcart_ecommerce.firstcart.dto.ProductDTO;
import com.firstcart_ecommerce.firstcart.model.*;
import com.firstcart_ecommerce.firstcart.repository.*;
import com.firstcart_ecommerce.firstcart.services.*;
import com.firstcart_ecommerce.firstcart.util.InvoiceGenerator;
import com.firstcart_ecommerce.firstcart.util.OrderStatus;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private CategoryOfferRepo categoryOfferRepo;

    @Autowired
    private CouponRepo couponRepo;

    @Autowired
    ProductRepo productRepo;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SubCategoryService subCategoryService;

    @Autowired
    private ProductOfferRepo productOfferRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubCategoryRepo subCategoryRepo;

    @Autowired
    private ProductService productService;

    @Autowired
    private WishListService wishListService;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ProductImageRepo productImageRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WalletService walletService;

    @ModelAttribute
    public void profiler(Principal p, Model m){
        if(p != null) {
            String email = p.getName();
            User user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
            Cart userCart = userService.getUserCart(user);
            Wallet wallet=walletService.getOrCreateUserWallet(user);
            m.addAttribute("cartProductCount", userCart.getItems().size());
            m.addAttribute("wishListCount",wishListService.getNumberOfItemsInWishlist(user));


        }

    }







    @GetMapping("/profile")

    public String profile(Principal p, Model m){
        if(p != null) {
            String email = p.getName();
            User user = userRepo.findByEmail(email);
            m.addAttribute("user", user);
        }
        return "admin_profile";
    }
    @GetMapping("/home")
    public String home(Model model,Principal principal){
        User user= userRepo.findByEmail(principal.getName());
        model.addAttribute("wl",wishListService.getOrCreateUserCart(user));
        model.addAttribute("cart",userService.getUserCart(user).getId());
        model.addAttribute("categories",subCategoryRepo.findByIsListedTrue());
        model.addAttribute("listedproducts",productRepo.findListedProducts());
        model.addAttribute("romance",subCategoryRepo.getById(1));
        model.addAttribute("horror",subCategoryRepo.getById(3));
        model.addAttribute("trading",subCategoryRepo.getById(2));
        model.addAttribute("pageTitle", "Home | Admin");
        return "user/userindex";
    }
    @GetMapping("/manage")
    public String viewData(Model model){

        model.addAttribute("userdata",userService.getAllUsers());
        model.addAttribute("pageTitle", "User Details | Admin");
        return "admin/users";

    }

    @GetMapping("/adminpanel")
    public String adminpanel(Model model){
        List<Order> orders = orderRepo.findAll();
        Double sum = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(0.0, Double::sum);

        LocalDate today = LocalDate.now();
        List<Order> ordertoday = orderRepo.findByDate(today);
        Double sumtoday = ordertoday.stream()
                .filter(order -> !order.getStatus().toString().equals("CANCELED") && !order.getStatus().toString().equals("RETURN"))
                .mapToDouble(Order::getTotalAmount)
                .sum();
        model.addAttribute("todayrevenue",sumtoday);
        model.addAttribute("totalrevenue",sum);
        int totalUsers = userService.getTotalUsers();
        model.addAttribute("totalUsers", totalUsers);
        int totalProducts = productService.getTotalProducts();
        model.addAttribute("totalProducts", totalProducts);
       /* model.addAttribute("todaysSales",orderService.countOrdersCreatedToday());*/
        int totalCategories = subCategoryService.getTotalCategories();
        model.addAttribute("totalCategories", totalCategories);
        int totalOrders = orderService.getTotalOrders();
        model.addAttribute("totalOrders", totalOrders);

        model.addAttribute("lowStockProducts", productService.getAllProductsSortedByQuantity());
        List<Order> recentOrders = orderService.getRecentOrders();
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("pageTitle", "Admin Dashboard | Admin");

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        LocalDateTime start = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = LocalDateTime.now().with(TemporalAdjusters.lastDayOfYear()).withHour(23).withMinute(59).withSecond(59);
        List<Map<String, Object>> dailyTotals = orderRepo.findDailyTotals(startOfMonth, endOfMonth);
        List<Map<String, Object>> monthlyTotals = orderRepo.findMonthlyTotals(start,end);

        model.addAttribute("dailyTotals", dailyTotals);



        model.addAttribute("monthlyTotals", orderRepo.getTotalAmountByMonth());
        model.addAttribute("pageTitle", "Admin Panel | Admin");


        return "admin/adminpanel";
    }



        @PostMapping("/blockuser/{id}")
        public String blockUsr(@PathVariable (value ="id")int id ){
            userService.blockUser(id);

            return "redirect:/admin/manage";
        }

        @PostMapping("/unblockuser/{id}")
        public String unblockUsr(@PathVariable (value ="id")int id ){
            userService.unblockUser(id);

            return "redirect:/admin/manage";
        }



    @GetMapping("/deleteuser/{id}")
    public String deleteUser(@PathVariable (value = "id")int id){
        userRepo.deleteById(id);
        return "redirect:/admin/usermanage";
    }
/*user crud operation end*/

/*
 category crud operation start
*/

    @GetMapping("/category")
    public String categories(Model model){
        model.addAttribute("category",categoryService.getAllCategory());
        model.addAttribute("pageTitle", "Category Details | Admin");
        return "admin/categories";
    }


    @GetMapping("/category/add")
    public String addCat(Model model){
        model.addAttribute("category" ,new Category());
        model.addAttribute("pageTitle", "Add Category | Admin");
        return "admin/add_Parentcat";
    }

    @PostMapping("/category/adding")
    public String postCat(@ModelAttribute("category")Category category){
        categoryService.addCategory(category);
        return "redirect:/admin/categories";
    }





    @GetMapping("/testing")
    public String tester(){
        return "admin/test";
    }


    @GetMapping("/categories")
    public String listCategories(Model model) {
        List<CategorySubCategoryDTO> categorySubCategoryDTOList = subCategoryRepo.getCategorySubCategoryJoin();
        model.addAttribute("categorySubCategoryDTOList", categorySubCategoryDTOList);
        model.addAttribute("categoey",new Category());
        model.addAttribute("subcategory", new SubCategory());
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("pageTitle", "Categories | Admin");


        return "admin/category-list"; // Thymeleaf view name
    }


    @GetMapping("/subcategories/new")
        public String showAddForm(Model model) {
            model.addAttribute("subcategory", new SubCategory());
            model.addAttribute("categories", categoryService.getAllCategory());
            model.addAttribute("pageTitle", "Sub Categories | Admin");
        return "admin/add_subcat";
    }

    @PostMapping("/addsubcategory")
    public String addSubcategory(@ModelAttribute SubCategory subcategory,
                                 @RequestParam("productImages") MultipartFile file,
                                 @RequestParam("category_id") int categoryId) {
        Category category = categoryRepo.findById(categoryId).orElseThrow();
        subcategory.setCategory(category);
        subcategory.setListed(true);

        if (!file.isEmpty()) {
            subcategory.setImageName(file.getOriginalFilename());
            s3Service.saveFile(file);
        }
        subCategoryRepo.save(subcategory);
        return "redirect:/admin/categories";
    }
    @GetMapping("/category/delete/{id}")
    public String deleteCat(@PathVariable (value = "id")int id) {
        subCategoryRepo.deleteById(id);
        return "redirect:/admin/categories";
    }
    @GetMapping("/category/unlist/{id}")
    public String makeUnlist(@PathVariable ("id") int id){
        SubCategory s=subCategoryRepo.getById(id);
        s.setListed(false);
        subCategoryRepo.save(s);
        return "redirect:/admin/categories";
    }
    @GetMapping("/category/list/{id}")
    public String makelist(@PathVariable ("id") int id){
        SubCategory s=subCategoryRepo.getById(id);
        s.setListed(true);
        subCategoryRepo.save(s);
        return "redirect:/admin/categories";
    }

    @GetMapping("/product/add")
    public String addproduct(Model model){
        model.addAttribute("categories",subCategoryService.getAllSubCategories());
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("newCategory", new SubCategory());
        model.addAttribute("pageTitle", "Product Add | Admin");

        return "admin/add_product";
    }

    @PostMapping("/product/add")
    public String addProductIn(@ModelAttribute("productDTO") ProductDTO productDTO,
                               @RequestParam("productImages") List<MultipartFile> files,
                               @RequestParam("imgNames") List<String> imgNames,
                                Model model)throws IOException {
        Product product=new Product();
        product.setName(productDTO.getName());
        product.setSubCategory(subCategoryService.getSubCategoryById(productDTO.getSubCategoryId()).get());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setDescription(productDTO.getDescription());


        List<ProductImage> images = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String imageUUID;

            if (!file.isEmpty()) {
                imageUUID = file.getOriginalFilename();
                s3Service.saveFile(file);


            } else {
                if (i < imgNames.size()) {
                    imageUUID = imgNames.get(i);
                } else {
                    imageUUID = "img/logo.png";
                }
            }

            ProductImage image = new ProductImage();
            image.setImageName(imageUUID);
            images.add(image);
        }

        /*for (ProductImage image : images) {
            Long imageId = productService.saveImageAndGetId(image.getImageName());
        }*/

        product.setImages(images);
        productService.addProduct(product);
        return "redirect:/admin/products";
    }


    @GetMapping("/products")
    public String getProducts(Model model){
        model.addAttribute("products", productService.getAllProduct());
        model.addAttribute("pageTitle", "Products | Admin");


        return "admin/products";
    }
    @ModelAttribute("imageUrl")
    public String getUrl(String filename) {
        return s3Service.getImageUrl(filename);

    }
    @GetMapping("/products/details/{productId}")
    public String getProductDetails(@PathVariable Long productId, Model model) {
        Optional<Product> product = productService.getProductById(productId);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("pageTitle", "Product Details | Admin");
            return "admin/productdetails";
        } else {
            return "404";
        }
    }

    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable long id){
            productService.removeProductById(id);
            return "redirect:/admin/products";

    }


    @GetMapping("/product/update/{id}")
    public String updateProduct(@PathVariable long id, Model model){
        Product product = productService.getProductById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(id);
        productDTO.setName(product.getName());
        SubCategory subCategory = product.getSubCategory();
        if (subCategory != null) {
            productDTO.setSubCategoryId(subCategory.getId());
        }
        productDTO.setPrice(product.getPrice());
        productDTO.setStockQuantity(product.getStockQuantity());
        productDTO.setDescription(product.getDescription());
        productDTO.setImageNames(product.getImages().stream().map(ProductImage::getImageName).collect(Collectors.toList()));

        model.addAttribute("categories", subCategoryService.getAllSubCategories());
        model.addAttribute("productDTO", productDTO);
        model.addAttribute("pageTitle", "Update Product | Admin");

        return "admin/update_product";
    }

    @PostMapping("/product/update")
    public String updateProductIn(@ModelAttribute("productDTO") ProductDTO productDTO,
                              /* @RequestParam("productImages") List<MultipartFile> files,*/
                                  @RequestParam("croppedImage") List<MultipartFile> croppedfile,
                               @RequestParam("imgNames") List<String> imgNames,
                               Model model)throws IOException {
        Product product=new Product();
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setSubCategory(subCategoryService.getSubCategoryById(productDTO.getSubCategoryId()).get());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setDescription(productDTO.getDescription());


        List<ProductImage> images = new ArrayList<>();

        for (int i = 0; i < croppedfile.size(); i++) {
            /*MultipartFile file = files.get(i);*/
            MultipartFile file=croppedfile.get(i);
            String imageUUID;

            if (!file.isEmpty()) {
                /*imageUUID = file.getOriginalFilename();*/
                /*s3Service.saveFile(file);*/
                s3Service.saveFile(file);
                imageUUID=file.getOriginalFilename();


            } else {
                if (i < imgNames.size()) {
                    imageUUID = imgNames.get(i);
                } else {
                    imageUUID = "img/logo.png";
                }
            }

            ProductImage image = new ProductImage();
            image.setImageName(imageUUID);
            images.add(image);
        }

        /*for (ProductImage image : images) {
            Long imageId = productService.saveImageAndGetId(image.getImageName());
        }*/

        product.setImages(images);
        productService.addProduct(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/stockmanagement")
    public String getStock(Model m){
        m.addAttribute("products" ,productService.getAllProductsSortedByQuantity());
        m.addAttribute("pageTitle", "Stock Management | Admin");
        return "admin/stockManagement";
    }
    @GetMapping("/searchproduct")
    public String getprod(@RequestParam(value = "query", required = false) String query, Model model) {
        List<Product> products;
        if (query != null && !query.isEmpty()) {
            products = productRepo.searchProduct(query);
        } else {
            products =productService.getAllProductsSortedByQuantity();
        }
        model.addAttribute("products", products);
        return "admin/stockManagement";
    }

    @PostMapping("/stock/set/{id}")
    public String setStock(@RequestParam("newqty") int qty,
                           @PathVariable("id") Long id , HttpSession session) {
        Product product= productRepo.getById(id);
        int old=product.getStockQuantity();
        int n=old+qty;
        product.setStockQuantity(n);
        productRepo.save(product);
        session.setAttribute("msg","ADDED STOCK SUCCESSFULLY.......!");
        return "redirect:/admin/stockmanagement";
    }

    @PostMapping("/stock/add/{id}")
    public String addStock(@RequestParam("newqty") int qty,
                           @PathVariable("id") Long id , HttpSession session) {
        Product product= productRepo.getById(id);
        product.setStockQuantity(qty);
        productRepo.save(product);
        session.setAttribute("msg","SET STOCK SUCCESSFULLY.......!");
        return "redirect:/admin/stockmanagement";
    }

    @GetMapping("orderManage")
    public String orderManage(Model m){
        List<Order> orders=orderRepo.findAllByOrderByOrderDateTimeDesc();
        m.addAttribute("orders",orders);
        m.addAttribute("pageTitle", "Order Manage | Admin");
        return "admin/OrderManager";
    }

    @PostMapping("/orders/updatestatus/{id}")
    public String updateStatus(@PathVariable("id") Long id,
                               @RequestParam ("status")OrderStatus status){
        Order order= orderRepo.getById(id);
        if(status == OrderStatus.CANCELED||status==OrderStatus.RETURN){
            orderService.changeStock(order);
        }
        order.setStatus(status);
        orderRepo.save(order);
        return "redirect:/admin/orderManage";
    }
    @GetMapping("/coupon")
    public String coupen(Model model){
        model.addAttribute("coupons",couponRepo.findByIsDeletedFalse());
        model.addAttribute("pageTitle", "Coupon Management | Admin");
        return "admin/coupen";
    }

    @PostMapping("/coupon/add")
    public String addCoupen(@ModelAttribute Coupon coupon , Model m,HttpSession session){
        List<Coupon>coupons=couponRepo.findAll();
        for(Coupon coupon1:coupons){
            if(coupon1.getCouponCode().equals(coupon.getCouponCode())){
                session.setAttribute("error","Coupon Code Already Exist ...Try Different Code ");
                return "redirect:/admin/coupon";
            }
        }
        couponRepo.save(coupon);
        return "redirect:/admin/coupon";
    }
    @GetMapping("/coupon/edit/{id}")
    public String editCoupon(@PathVariable ("id") Long id,Model model){
        model.addAttribute("coupon",couponRepo.getById(id));
        model.addAttribute("pageTitle", "Coupon Edit | Admin");
        return  "admin/couponEdit";
    }

    @PostMapping("/coupon/update")
    public String editCoupons(@ModelAttribute("coupon") Coupon coupon){
        couponRepo.save(coupon);
        return "redirect:/admin/coupon";
    }
    @GetMapping("/coupon/delete/{id}")
    public String deletecoupon(@PathVariable ("id")Long id){
        Coupon c=couponRepo.getById(id);
        c.setDeleted(true);
        couponRepo.save(c);
        return "redirect:/admin/coupon";
    }

    @GetMapping("/offer/manage")
    public String offerManagement(Model model){
        model.addAttribute("categories",subCategoryService.getAllSubCategories());
        model.addAttribute("products", productService.getAllProduct());
        model.addAttribute("categoryOffers",categoryOfferRepo.findAll());
        model.addAttribute("productOffers",productOfferRepo.findAll());
        model.addAttribute("pageTitle", "Offer Details Details | Admin");
        return "admin/offerManagement";
    }

    @PostMapping("/offer/category/add")
    public String categoryOfferAdd(@ModelAttribute CategoryOffer categoryOffer,HttpSession session){
        List<CategoryOffer> categoryOffers=categoryOfferRepo.findAll();
        for(CategoryOffer categoryOffer1:categoryOffers){
            if(categoryOffer.getSubCategory().getId()==categoryOffer1.getSubCategory().getId()){
                session.setAttribute("error",categoryOffer.getSubCategory().getName()+"  Already Having an Offer..! Please Delete Existing Offer.");
                return "redirect:/admin/offer/manage";
            }
        }
        categoryOfferRepo.save(categoryOffer);
        return "redirect:/admin/offer/manage";
    }

    @PostMapping("/offer/product/add")
    public String productOfferAdd(@ModelAttribute ProductOffer productOffer,HttpSession session){
        List<ProductOffer> productOffers=productOfferRepo.findAll();
        for(ProductOffer productOffer1:productOffers){
            if(productOffer1.getProduct().getId()==productOffer1.getProduct().getId()){
                session.setAttribute("error",productOffer.getProduct().getName()+"  Already Having An Offer ...! Try After Deleting Existing Offer.");
                return "redirect:/admin/offer/manage";
            }
        }
        productOfferRepo.save(productOffer);
        return "redirect:/admin/offer/manage";
    }

    @PostMapping("/search/product")
    public List<Product> searchProducts(@RequestParam("searchQuery") String searchQuery) {
        // Call the service method to retrieve the filtered product list
        List<Product> filteredProducts = productService.searchProducts(searchQuery);
        return filteredProducts;
    }

    @GetMapping("/offer/category/delete/{id}")
    public String deleteCatOffer(@PathVariable("id") Long id){
        categoryOfferRepo.deleteById(id);
        return "redirect:/admin/offer/manage";
    }

    @GetMapping("/offer/product/delete/{id}")
    public String deleteProOffer(@PathVariable("id") Long id){
        productOfferRepo.deleteById(id);
        return "redirect:/admin/offer/manage";

    }



}

