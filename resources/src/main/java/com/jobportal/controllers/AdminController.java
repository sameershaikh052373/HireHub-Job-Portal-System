package com.jobportal.controllers;

import com.jobportal.models.ContactMessage;
import com.jobportal.models.Job;
import com.jobportal.models.User;
import com.jobportal.services.ContactMessageService;
import com.jobportal.services.JobService;
import com.jobportal.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final UserService userService;
    private final JobService jobService;
    private final ContactMessageService contactMessageService;
    
    public AdminController(UserService userService, JobService jobService, ContactMessageService contactMessageService) {
        this.userService = userService;
        this.jobService = jobService;
        this.contactMessageService = contactMessageService;
    }
    
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }
    
    @GetMapping("/jobs")
    public String listJobs(Model model) {
        List<Job> jobs = jobService.findAllJobs();
        model.addAttribute("jobs", jobs);
        return "admin/jobs";
    }
    
    @PostMapping("/users/{id}/toggle-status")
public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
        User user = userService.findUserById(id);
        
        // Check if user has enabled field, if not, skip this operation
        if (user.getEnabled() != null) {
            user.setEnabled(!user.getEnabled());
            userService.updateUser(user);
            
            String status = user.getEnabled() ? "enabled" : "disabled";
            redirectAttributes.addFlashAttribute("success", "User " + status + " successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "User status toggle not supported");
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error updating user status: " + e.getMessage());
    }
    return "redirect:/admin/users";
}

    
    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            jobService.deleteJob(id);
            redirectAttributes.addFlashAttribute("success", "Job deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting job: " + e.getMessage());
        }
        return "redirect:/admin/jobs";
    }
    
    @PostMapping("/jobs/{id}/toggle-status")
    public String toggleJobStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Job job = jobService.findJobById(id);
            if (job.getStatus() == Job.JobStatus.ACTIVE) {
                job.setStatus(Job.JobStatus.CLOSED);
            } else {
                job.setStatus(Job.JobStatus.ACTIVE);
            }
            jobService.updateJob(job);
            
            redirectAttributes.addFlashAttribute("success", "Job status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating job status: " + e.getMessage());
        }
        return "redirect:/admin/jobs";
    }
    
    @GetMapping("/messages")
    public String listMessages(Model model) {
        List<ContactMessage> messages = contactMessageService.findAllMessages();
        model.addAttribute("messages", messages);
        model.addAttribute("unreadCount", contactMessageService.getUnreadCount());
        return "admin/messages";
    }
    
    @GetMapping("/messages/{id}")
    public String viewMessage(@PathVariable Long id, Model model) {
        ContactMessage message = contactMessageService.findMessageById(id);
        if (message != null) {
            contactMessageService.markAsRead(id);
            model.addAttribute("message", message);
            return "admin/message-detail";
        }
        return "redirect:/admin/messages";
    }
    
    @PostMapping("/messages/{id}/delete")
    public String deleteMessage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            contactMessageService.deleteMessage(id);
            redirectAttributes.addFlashAttribute("success", "Message deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting message: " + e.getMessage());
        }
        return "redirect:/admin/messages";
    }
}
