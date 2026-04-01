package com.jobportal.controllers;

import com.jobportal.models.ContactMessage;
import com.jobportal.services.ContactMessageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class ContactController {
    
    private final ContactMessageService contactMessageService;
    
    public ContactController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }
    
    @GetMapping("/contact")
    public String contactForm(Model model) {
        model.addAttribute("contactMessage", new ContactMessage());
        return "contact";
    }
    
    @PostMapping("/contact")
    public String submitContact(@Valid @ModelAttribute("contactMessage") ContactMessage contactMessage,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "contact";
        }
        
        try {
            contactMessageService.saveMessage(contactMessage);
            redirectAttributes.addFlashAttribute("success", "Thank you for your message! We'll get back to you soon.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error sending message. Please try again.");
        }
        
        return "redirect:/contact";
    }
}