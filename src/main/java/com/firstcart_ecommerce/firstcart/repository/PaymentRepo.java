package com.firstcart_ecommerce.firstcart.repository;

import com.firstcart_ecommerce.firstcart.model.Payment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepo extends JpaRepository<Payment , Long> {

    public Payment findByOrderId(String orderId);

    @Modifying
    @Transactional
    @Query("delete from Payment o where o.paymentId is null")
    void deletePaymentWithNullPaymentId();
}
