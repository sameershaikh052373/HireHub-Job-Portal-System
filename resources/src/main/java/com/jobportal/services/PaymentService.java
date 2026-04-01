package com.jobportal.services;

import com.jobportal.models.Payment;
import com.jobportal.repositories.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }
    
    public Payment findByOrderId(String orderId) {
        return paymentRepository.findByRazorpayOrderId(orderId);
    }
}
