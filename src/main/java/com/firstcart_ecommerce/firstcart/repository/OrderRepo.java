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


    @Query("SELECT DATE(o.orderDateTime) as date, SUM(o.totalAmount) as total FROM Order o WHERE o.orderDateTime BETWEEN :start AND :end AND o.status NOT IN ('RETURN', 'CANCELED') GROUP BY DATE(o.orderDateTime)")
    List<Map<String, Object>> findDailyTotals(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


    /*@Query(value = "SELECT MONTH(o.orderDateTime) AS month, SUM(o.totalAmount) AS totalAmount FROM 'orders' o " +
            "WHERE YEAR(o.orderDateTime) = :year " +
            "GROUP BY MONTH(o.orderDateTime)", nativeQuery = true)
    List<Map<String, Object>> findMonthlyTotals(@Param("year") int year);*/

    @Query("SELECT YEAR(o.orderDateTime) AS year, MONTH(o.orderDateTime) AS month, SUM(o.totalAmount) AS totalAmount " +
            "FROM Order o " +
            "WHERE o.orderDateTime BETWEEN :start AND :end " +
            "AND o.status NOT IN ('RETURN', 'CANCELED') " +
            "GROUP BY YEAR(o.orderDateTime), MONTH(o.orderDateTime)")
    List<Map<String, Object>> findMonthlyTotals(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT MONTHNAME(o.orderDateTime) AS month, SUM(o.totalAmount) AS totalAmount "
            + "FROM Order o "
            + "WHERE YEAR(o.orderDateTime) = YEAR(CURRENT_DATE()) "
            + "GROUP BY MONTHNAME(o.orderDateTime)")
    List<Map<String, Object>> getTotalAmountByMonth();

   List<Order> findAllByUserId(int userId);
}
