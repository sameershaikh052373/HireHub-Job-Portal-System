package com.jobportal.controllers;

import com.jobportal.models.PasswordResetOtp;
import com.jobportal.models.User;
import com.jobportal.repositories.PasswordResetOtpRepository;
import com.jobportal.services.EmailService;
import com.jobportal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Random;

@Controller
public class ForgotPasswordController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordResetOtpRepository otpRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "forgot-password";
    }
    
    @PostMapping("/forgot-password")
    @Transactional
    public String sendOtp(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findUserByEmail(email);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Email not found");
                return "redirect:/forgot-password";
            }
            
            String otp = String.format("%06d", new Random().nextInt(999999));
            
            otpRepository.deleteByEmail(email);
            
            PasswordResetOtp resetOtp = new PasswordResetOtp(email, otp, LocalDateTime.now().plusMinutes(10));
            otpRepository.save(resetOtp);
            
            emailService.sendOtpEmail(email, otp);
            
            redirectAttributes.addFlashAttribute("success", "OTP sent to your email");
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/reset-password";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error sending OTP: " + e.getMessage());
            return "redirect:/forgot-password";
        }
    }
    
    @GetMapping("/reset-password")
    public String resetPasswordForm(Model model) {
        return "reset-password";
    }
    
    @PostMapping("/reset-password")
    @Transactional
    public String resetPassword(@RequestParam String email,
                               @RequestParam String otp,
                               @RequestParam String newPassword,
                               RedirectAttributes redirectAttributes) {
        try {
            PasswordResetOtp resetOtp = otpRepository.findByEmailAndOtpAndUsedFalse(email, otp)
                .orElse(null);
            
            if (resetOtp == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid OTP");
                redirectAttributes.addFlashAttribute("email", email);
                return "redirect:/reset-password";
            }
            
            if (resetOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("error", "OTP expired");
                redirectAttributes.addFlashAttribute("email", email);
                return "redirect:/reset-password";
            }
            
            User user = userService.findUserByEmail(email);
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.updateUser(user);
            
            resetOtp.setUsed(true);
            otpRepository.save(resetOtp);
            
            redirectAttributes.addFlashAttribute("success", "Password reset successfully");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error resetting password: " + e.getMessage());
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/reset-password";
        }
    }
}
