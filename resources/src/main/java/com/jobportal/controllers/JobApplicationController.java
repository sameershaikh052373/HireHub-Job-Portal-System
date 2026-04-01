package com.jobportal.controllers;

import com.jobportal.models.Job;
import com.jobportal.models.JobApplication;
import com.jobportal.models.User;
import com.jobportal.services.JobApplicationService;
import com.jobportal.services.JobService;
import com.jobportal.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class JobApplicationController {
    
    private final JobApplicationService applicationService;
    private final JobService jobService;
    private final UserService userService;
    
    public JobApplicationController(JobApplicationService applicationService, JobService jobService, UserService userService) {
        this.applicationService = applicationService;
        this.jobService = jobService;
        this.userService = userService;
    }
    
    @GetMapping("/jobs/{jobId}/apply")
    public String showApplyForm(@PathVariable Long jobId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User jobSeeker = userService.findUserByEmail(auth.getName());
        Job job = jobService.findJobById(jobId);
        
        // Check application limit
        if (!canApplyForJob(jobSeeker)) {
            model.addAttribute("error", "You have reached your application limit (5 applications). Upgrade to Premium for unlimited applications!");
            model.addAttribute("showUpgrade", true);
            model.addAttribute("job", job);
            return "jobs/view";
        }
        
        // Check if already applied
        if (applicationService.hasApplied(job, jobSeeker)) {
            model.addAttribute("error", "You have already applied for this job.");
            model.addAttribute("job", job);
            return "jobs/view";
        }
        
        model.addAttribute("job", job);
        return "applications/apply";
    }
    
    @PostMapping("/jobs/{jobId}/apply")
    public String applyForJob(@PathVariable Long jobId,
                             @RequestParam String coverLetter,
                             @RequestParam(required = false) MultipartFile resume,
                             RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User jobSeeker = userService.findUserByEmail(auth.getName());
            
            // Check application limit
            if (!canApplyForJob(jobSeeker)) {
                redirectAttributes.addFlashAttribute("error", 
                    "You have reached your application limit (5 applications). Upgrade to Premium for unlimited applications!");
                redirectAttributes.addFlashAttribute("showUpgrade", true);
                return "redirect:/jobs/" + jobId;
            }
            
            Job job = jobService.findJobById(jobId);
            
            // Check if already applied
            if (applicationService.hasApplied(job, jobSeeker)) {
                redirectAttributes.addFlashAttribute("error", "You have already applied for this job.");
                return "redirect:/jobs/" + jobId;
            }
            
            // Create application
            JobApplication application = new JobApplication();
            application.setJob(job);
            application.setApplicant(jobSeeker);
            application.setCoverLetter(coverLetter);
            application.setStatus(JobApplication.ApplicationStatus.APPLIED);
            application.setCreatedAt(LocalDateTime.now());
            
            // Handle resume upload if provided
            if (resume != null && !resume.isEmpty()) {
                String resumePath = saveResumeFile(resume, jobSeeker.getId());
                application.setResumePath(resumePath);
            }
            
            applicationService.saveApplication(application);
            
            // Increment application count for free users
            if (jobSeeker.getIsPremium() == null || !jobSeeker.getIsPremium() || 
                jobSeeker.getPremiumExpiresAt() == null || 
                jobSeeker.getPremiumExpiresAt().isBefore(LocalDateTime.now())) {
                
                jobSeeker.setJobsAppliedCount((jobSeeker.getJobsAppliedCount() != null ? jobSeeker.getJobsAppliedCount() : 0) + 1);
                userService.updateUser(jobSeeker);
            }
            
            redirectAttributes.addFlashAttribute("success", "Application submitted successfully!");
            return "redirect:/jobs/" + jobId;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error submitting application: " + e.getMessage());
            return "redirect:/jobs/" + jobId;
        }
    }
    
    @GetMapping("/jobseeker/applications")
    public String listMyApplications(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User jobSeeker = userService.findUserByEmail(auth.getName());
        
        List<JobApplication> applications = applicationService.findApplicationsByApplicant(jobSeeker);
        model.addAttribute("applications", applications);
        
        return "applications/my-applications";
    }
    
    @GetMapping("/employer/jobs/{jobId}/applications")
    public String listJobApplications(@PathVariable Long jobId, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User employer = userService.findUserByEmail(auth.getName());
            
            Job job = jobService.findJobById(jobId);
            
            if (!job.getEmployer().getId().equals(employer.getId())) {
                return "redirect:/employer/jobs?error=unauthorized";
            }
            
            List<JobApplication> applications = applicationService.findApplicationsByJob(job);
            
            model.addAttribute("job", job);
            model.addAttribute("applications", applications);
            
            return "applications/job-applications";
            
        } catch (Exception e) {
            return "redirect:/employer/jobs?error=notfound";
        }
    }
    
    @GetMapping("/employer/applications/{id}")
    public String viewApplication(@PathVariable Long id, Model model) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User employer = userService.findUserByEmail(auth.getName());
            
            JobApplication application = applicationService.findApplicationById(id);
            
            if (!application.getJob().getEmployer().getId().equals(employer.getId())) {
                return "redirect:/employer/jobs?error=unauthorized";
            }
            
            model.addAttribute("application", application);
            return "applications/view";
            
        } catch (Exception e) {
            return "redirect:/employer/jobs?error=notfound";
        }
    }
    
    @PostMapping("/employer/applications/{id}/update-status")
public String updateApplicationStatus(@PathVariable Long id, 
                                    @RequestParam String status,
                                    RedirectAttributes redirectAttributes) {
    try {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User employer = userService.findUserByEmail(auth.getName());
        
        JobApplication application = applicationService.findApplicationById(id);
        
        if (!application.getJob().getEmployer().getId().equals(employer.getId())) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access");
            return "redirect:/employer/jobs";
        }
        
        applicationService.updateApplicationStatus(application, status);
        
        redirectAttributes.addFlashAttribute("success", "Application status updated successfully!");
        return "redirect:/employer/jobs/" + application.getJob().getId() + "/applications";
        
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error updating application status");
        return "redirect:/employer/jobs";
    }
}

  
    
   
    private boolean canApplyForJob(User jobSeeker) {
        if (jobSeeker.getIsPremium() != null && jobSeeker.getIsPremium() && 
            jobSeeker.getPremiumExpiresAt() != null && 
            jobSeeker.getPremiumExpiresAt().isAfter(LocalDateTime.now())) {
            return true;
        }
        
     
        List<JobApplication> userApplications = applicationService.findApplicationsByApplicant(jobSeeker);
        return userApplications.size() < 5;
    }
    
   
    private String saveResumeFile(MultipartFile file, Long userId) throws IOException {
        String uploadDir = "uploads/resumes/";
        Path uploadPath = Paths.get(uploadDir);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        
        return filePath.toString();
    }

    
}
