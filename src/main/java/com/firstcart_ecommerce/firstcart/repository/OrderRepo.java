package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.model.Order;
import com.firstcart_ecommerce.firstcart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderRepo extends JpaRepository<Order , Long> {
    List<Order> findByUser(User user);
    List<Order> findByUserOrderByOrderDateTimeDesc(User user);

    List<Order> findAllByOrderByOrderDateTimeDesc();

    @Query("SELECT o FROM Order o WHERE DATE(o.orderDateTime) = :date")
    List<Order> findByDate(@Param("date") LocalDate date);

    @Query("SELECT DATE(o.orderDateTime) as date, SUM(o.totalAmount) as total FROM Order o WHERE o.orderDateTime BETWEEN :start AND :end GROUP BY DATE(o.orderDateTime)")
    List<Map<String, Object>> findDailyTotals(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.orderDateTime BETWEEN :start AND :end")
    List<Order> findOrdersInDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    @Query("SELECT oi.product, SUM(oi.quantity), SUM(oi.quantity * p.price) " +
            "FROM Order o JOIN o.orderItems oi JOIN oi.product p " +
            "WHERE MONTH(o.orderDateTime) = ?1 AND YEAR(o.orderDateTime) = ?2 " +
            "GROUP BY oi.product")
    List<Object[]> findProductSales(int month, int year);

    @Query("SELECT oi.product, SUM(oi.quantity), SUM(oi.quantity * oi.product.price) " +
            "FROM Order o JOIN o.orderItems oi " +
            "WHERE YEAR(o.orderDateTime) = ?1 " +
            "GROUP BY oi.product")
    List<Object[]> findYearlyProductSales(int year);

    @Query("SELECT oi.product, SUM(oi.quantity), SUM(oi.quantity * oi.product.price) " +
            "FROM Order o JOIN o.orderItems oi " +
            "WHERE DATE(o.orderDateTime) = ?1 " +
            "GROUP BY oi.product")
    List<Object[]> findDailyProductSales(Date date);
}
