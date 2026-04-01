package com.jobportal.controllers;

import com.jobportal.models.Payment;
import com.jobportal.models.User;
import com.jobportal.services.PaymentService;
import com.jobportal.services.UserService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/premium")
public class PaymentController {
    
	@Autowired
    private final PaymentService paymentService;
	
	@Autowired
    private final UserService userService;
    
    @Value("${razorpay.key.id:rzp_test_XhwcKLapXCzK69}")
    private String razorpayKeyId;
    
    @Value("${razorpay.key.secret:GzZcBeUMwhjt9yQWrBF1RgE5}")
    private String razorpayKeySecret;
    
    public PaymentController(PaymentService paymentService, UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService;
    }
    
    @GetMapping("/upgrade")
public String upgradePage(Model model, RedirectAttributes redirectAttributes) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = userService.findUserByEmail(auth.getName());

    // Block ADMIN from accessing
    if ("ADMIN".equals(user.getUserType().name())) {
        redirectAttributes.addFlashAttribute("error", "Admin is not allowed to access the premium upgrade page.");
        return "redirect:/dashboard";
    }

    // Add premium status check
    boolean isPremiumActive = user.getIsPremium() != null && 
                             user.getIsPremium() && 
                             user.getPremiumExpiresAt() != null && 
                             user.getPremiumExpiresAt().isAfter(LocalDateTime.now());

    model.addAttribute("user", user);
    model.addAttribute("isPremiumActive", isPremiumActive);
    model.addAttribute("razorpayKeyId", razorpayKeyId);
    return "premium/upgrade";
}


    
    @PostMapping("/create-order")
    @ResponseBody
    public String createOrder(@RequestParam Double amount) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findUserByEmail(auth.getName());
            
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100); // Amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "premium_" + user.getId());
            
            Order order = razorpay.orders.create(orderRequest);
            
            // Save payment record
            Payment payment = new Payment();
            payment.setUser(user);
            payment.setRazorpayOrderId(order.get("id"));
            payment.setAmount(amount);
            payment.setStatus(Payment.PaymentStatus.PENDING);
            payment.setCreatedAt(LocalDateTime.now());
            paymentService.savePayment(payment);
            
            return order.toString();
        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }
    
    @PostMapping("/verify-payment")
    public String verifyPayment(@RequestParam String razorpay_order_id,
                               @RequestParam String razorpay_payment_id,
                               @RequestParam String razorpay_signature,
                               RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findUserByEmail(auth.getName());
            
            // Update payment status
            Payment payment = paymentService.findByOrderId(razorpay_order_id);
            payment.setRazorpayPaymentId(razorpay_payment_id);
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            paymentService.savePayment(payment);
            
            // Upgrade user to premium
            user.setIsPremium(true);
            user.setPremiumExpiresAt(LocalDateTime.now().plusMonths(1)); // 1 month premium
            userService.updateUser(user);
            
            redirectAttributes.addFlashAttribute("success", "Premium upgrade successful!");
            return "redirect:/dashboard";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Payment verification failed!");
            return "redirect:/premium/upgrade";
        }
    }
}
