package com.firstcart_ecommerce.firstcart.controller;


import com.firstcart_ecommerce.firstcart.model.Order;
import com.firstcart_ecommerce.firstcart.model.Product;
import com.firstcart_ecommerce.firstcart.repository.OrderRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("admin/sales")
public class SalesController {

    @Autowired
    private OrderRepo orderRepo;

    @GetMapping("/download/{id}")
    @ResponseBody
    public void downloadSalesReport(@PathVariable ("id") int id
            ,HttpServletResponse response) throws IOException {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        LocalDate localDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
        List<Object[]> salesData;
        List<String> formattedSalesData = new ArrayList<>();

        if (id == 1) {
           salesData = orderRepo.findDailyProductSales(currentDate);
            formattedSalesData.add("Daily-Sales_Report, ,"+currentDate);
        } else if (id==2) {

            salesData = orderRepo.findProductSales(month, year);
            formattedSalesData.add("Month-Sales_Report, ,"+localDate.format(formatter)+"-"+year);
        }else {
           salesData = orderRepo.findYearlyProductSales(year);
            formattedSalesData.add("Year-Sales_Report, ,"+year);
        }



        BigDecimal totalRevenue = BigDecimal.ZERO;


        formattedSalesData.add("Product,Order Count,Total Amount");
        formattedSalesData.add(" , , ");
        for (Object[] record : salesData) {
            Product product = (Product) record[0];
            String productName = product.getName();
            long orderCount = (Long) record[1];
            double totalAmount = (double) record[2];
            totalRevenue = totalRevenue.add(BigDecimal.valueOf(totalAmount));
            String formattedRecord = productName + "," + orderCount + "," + totalAmount;
            formattedSalesData.add(formattedRecord);
        }
        formattedSalesData.add(" , , ");
        formattedSalesData.add("Total Revenue, ,"+totalRevenue);

        try (FileWriter writer = new FileWriter("sales_report.csv")) {
            for (String line : formattedSalesData) {
                writer.append(line).append("\n");
            }
        } catch (IOException e) {
            // Handle file creation error
            e.printStackTrace();
        }

        // Configure the response to allow file download
        // Set appropriate headers
        // You may need to adjust the content type and headers based on your file type (e.g., Excel)
        response.setHeader("Content-Disposition", "attachment; filename=sales_report.csv");
        response.setContentType("text/csv");

        // Copy the file content to the response output stream
        try (FileInputStream fileInputStream = new FileInputStream("sales_report.csv");
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // Handle file download error
            e.printStackTrace();
        }
    }
}

