package com.jobportal.controllers;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.jobportal.models.Job;
import com.jobportal.models.JobApplication;
import com.jobportal.models.User;
import com.jobportal.services.ContactMessageService;
import com.jobportal.services.JobApplicationService;
import com.jobportal.services.JobService;
import com.jobportal.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {
    
    private final UserService userService;
    private final JobService jobService;
    private final JobApplicationService applicationService;
    private final ContactMessageService contactMessageService;

    public DashboardController(UserService userService, JobService jobService, JobApplicationService applicationService, ContactMessageService contactMessageService) {
        this.userService = userService;
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.contactMessageService = contactMessageService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        
        model.addAttribute("user", user);
        
        if (user.getUserType() == User.UserType.JOB_SEEKER) {
            return jobSeekerDashboard(model, user);
        } else if (user.getUserType() == User.UserType.EMPLOYER) {
            return employerDashboard(model, user);
        } else if (user.getUserType() == User.UserType.ADMIN) {
            return adminDashboard(model, user);
        }
        
        return "dashboard/index";
    }
    
     private String jobSeekerDashboard(Model model, User jobSeeker) {
    List<JobApplication> applications = applicationService.findApplicationsByApplicant(jobSeeker);
    model.addAttribute("applications", applications);
    
    List<Job> recommendedJobs = jobService.findActiveJobs();
    model.addAttribute("recommendedJobs", recommendedJobs);
    
    // Add premium status check
    boolean isPremiumActive = jobSeeker.getIsPremium() != null && jobSeeker.getIsPremium() && 
                             jobSeeker.getPremiumExpiresAt() != null && 
                             jobSeeker.getPremiumExpiresAt().isAfter(LocalDateTime.now());
    model.addAttribute("isPremiumActive", isPremiumActive);
    
    return "dashboard/jobseeker";
}

 private String employerDashboard(Model model, User employer) {
    List<Job> jobs = jobService.findJobsByEmployer(employer);
    model.addAttribute("jobs", jobs);
    
    int actualJobCount = jobs.size();
    if (employer.getJobsPostedCount() != actualJobCount) {
        employer.setJobsPostedCount(actualJobCount);
        userService.updateUser(employer);
    }
    
    
    boolean isPremiumActive = employer.getIsPremium() != null && employer.getIsPremium() && 
                             employer.getPremiumExpiresAt() != null && 
                             employer.getPremiumExpiresAt().isAfter(LocalDateTime.now());
    model.addAttribute("isPremiumActive", isPremiumActive);
    
    List<JobApplication> recentApplications = new ArrayList<>();
    for (Job job : jobs) {
        List<JobApplication> jobApplications = applicationService.findApplicationsByJob(job);
        recentApplications.addAll(jobApplications);
    }
    
    recentApplications.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
    if (recentApplications.size() > 5) {
        recentApplications = recentApplications.subList(0, 5);
    }
    
    model.addAttribute("recentApplications", recentApplications);
    model.addAttribute("totalApplications", recentApplications.size());
    
    return "dashboard/employer";
}

 

    private String adminDashboard(Model model, User admin) {
        long totalUsers = userService.findAllUsers().size();
        model.addAttribute("totalUsers", totalUsers);
        
        long jobSeekersCount = userService.findAllJobSeekers().size();
        model.addAttribute("jobSeekersCount", jobSeekersCount);
        
        long employersCount = userService.findAllEmployers().size();
        model.addAttribute("employersCount", employersCount);
        
        long activeJobsCount = jobService.findActiveJobs().size();
        model.addAttribute("activeJobsCount", activeJobsCount);
        
        List<User> allUsers = userService.findAllUsers();
        model.addAttribute("allUsers", allUsers);
        
        List<Job> allJobs = jobService.findAllJobs();
        model.addAttribute("allJobs", allJobs);
        
        long unreadMessages = contactMessageService.getUnreadCount();
        model.addAttribute("unreadMessages", unreadMessages);
        
        return "dashboard/admin";
    }
    
    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        
        model.addAttribute("user", user);
        
       
        boolean isPremiumActive = user.getIsPremium() != null && user.getIsPremium() && 
                                 user.getPremiumExpiresAt() != null && 
                                 user.getPremiumExpiresAt().isAfter(LocalDateTime.now());
        model.addAttribute("isPremiumActive", isPremiumActive);
        
        if (user.getUserType() == User.UserType.JOB_SEEKER) {
            return "profile/jobseeker";
        } else if (user.getUserType() == User.UserType.EMPLOYER) {
            return "profile/employer";
        }
        
        return "profile/index";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam(required = false) String firstName,
                       @RequestParam(required = false) String lastName,
                       @RequestParam(required = false) String companyName,
                       @RequestParam(required = false) String industry,
                       @RequestParam(required = false) String companyWebsite,
                       @RequestParam(required = false) String phone,
                       @RequestParam(required = false) String companyDescription,
                       @RequestParam(required = false) String skills,
                       @RequestParam(required = false) String experience,
                       @RequestParam(required = false) String education,
                       @RequestParam(required = false) MultipartFile profileImage,
                       @RequestParam(required = false) MultipartFile companyLogo,
                       RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findUserByEmail(auth.getName());
            
           
            if (firstName != null) user.setFirstName(firstName);
            if (lastName != null) user.setLastName(lastName);
            if (skills != null) user.setSkills(skills);
            if (experience != null) user.setExperience(experience);
            if (education != null) user.setEducation(education);
            if (phone != null) user.setPhone(phone);
            
         
            if (companyName != null) user.setCompanyName(companyName);
            if (industry != null) user.setIndustry(industry);
            if (companyWebsite != null) user.setCompanyWebsite(companyWebsite);
            if (companyDescription != null) user.setCompanyDescription(companyDescription);
        
         
            if (profileImage != null && !profileImage.isEmpty()) {
                String imagePath = saveFile(profileImage, "profiles");
                user.setProfileImage("/uploads/profiles/" + imagePath);
            }
            
         
            if (companyLogo != null && !companyLogo.isEmpty()) {
                String logoPath = saveFile(companyLogo, "logos");
                user.setCompanyLogo("/uploads/logos/" + logoPath);
            }
            
            userService.updateUser(user);
            
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
        }
        
        return "redirect:/profile";
    }
    
    private String saveFile(MultipartFile file, String folder) throws IOException {
        String uploadDir = "uploads/" + folder + "/";
        Path uploadPath = Paths.get(uploadDir);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        
        return fileName;
    }

    @PostMapping("/profile/update-description")
    public String updateDescription(@RequestParam String companyDescription,
                           RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findUserByEmail(auth.getName());
            
            user.setCompanyDescription(companyDescription);
            userService.updateUser(user);
            
            redirectAttributes.addFlashAttribute("success", "Company description updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating description: " + e.getMessage());
        }
        
        return "redirect:/profile";
    }

  

   
}
