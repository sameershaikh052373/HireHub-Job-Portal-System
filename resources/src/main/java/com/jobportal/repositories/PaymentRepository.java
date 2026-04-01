package com.jobportal.repositories;

import com.jobportal.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByRazorpayOrderId(String orderId);
}
