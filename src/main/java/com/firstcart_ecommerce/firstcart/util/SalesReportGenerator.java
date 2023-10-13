package com.firstcart_ecommerce.firstcart.util;


import com.firstcart_ecommerce.firstcart.model.Order;
import com.firstcart_ecommerce.firstcart.model.OrderItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.List;

public class SalesReportGenerator {
    public void generateSalesReport(List<Order> orders) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.beginText();
        contentStream.showText("Date\t\tProduct\t\tTotal");
        contentStream.endText();
        contentStream.beginText();
        contentStream.showText("\n");
        contentStream.endText();

        for (Order order : orders) {
            contentStream.beginText();
            contentStream.showText(order.getOrderDateTime().toString() + "\t\t" + order.getOrderItems().get(1) + "\t\t" + order.getTotalAmount());
            for (OrderItem item : order.getOrderItems()) {
                contentStream.showText(order.getOrderItems().get(1).toString());
            }
            contentStream.endText();
            contentStream.beginText();
            contentStream.showText("\n");
            contentStream.endText();
        }

        contentStream.close();
        document.save("sales_report.pdf");
        document.close();
    }
}