package com.jobportal.controllers;

import com.jobportal.models.Job;
import com.jobportal.models.JobApplication;
import com.jobportal.models.User;
import com.jobportal.services.JobApplicationService;
import com.jobportal.services.JobService;
import com.jobportal.services.UserService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class JobController {
    
    private final JobService jobService;
    private final UserService userService;
    private final JobApplicationService applicationService;

    public JobController(JobService jobService, UserService userService, JobApplicationService applicationService) {
        this.jobService = jobService;
        this.userService = userService;
        this.applicationService = applicationService;
    }
    
    @GetMapping("/jobs")
    public String listJobs(Model model, 
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String category,
                          @RequestParam(defaultValue = "newest") String sortBy) {
        
        Sort sort;
        switch (sortBy) {
            case "title":
                sort = Sort.by("title").ascending();
                break;
            case "oldest":
                sort = Sort.by("createdAt").ascending();
                break;
            case "salary_high":
                sort = Sort.by("salary").descending();
                break;
            case "salary_low":
                sort = Sort.by("salary").ascending();
                break;
            default:
                sort = Sort.by("createdAt").descending();
        }
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Job> jobPage;
        
        if (category != null && !category.isEmpty()) {
            jobPage = jobService.findJobsByCategory(category, pageable);
        } else {
            jobPage = jobService.findAllJobs(pageable);
        }
        
        model.addAttribute("jobs", jobPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobPage.getTotalPages());
        model.addAttribute("totalItems", jobPage.getTotalElements());
        model.addAttribute("category", category);
        model.addAttribute("sortBy", sortBy);
        
        return "jobs/list";
    }
    
    @GetMapping("/jobs/search")
    public String searchJobs(Model model, 
                            @RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String location) {
        List<Job> searchResults;
        
        if (keyword != null && !keyword.isEmpty() && location != null && !location.isEmpty()) {
            searchResults = jobService.searchJobsByKeywordAndLocation(keyword, location);
        } else if (keyword != null && !keyword.isEmpty()) {
            searchResults = jobService.searchJobs(keyword);
        } else if (location != null && !location.isEmpty()) {
            searchResults = jobService.findJobsByLocation(location);
        } else {
            searchResults = jobService.findActiveJobs();
        }
        
        model.addAttribute("jobs", searchResults);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        
        return "jobs/search-results";
    }
    
    @GetMapping("/jobs/{id}")
    public String viewJob(@PathVariable Long id, Model model) {
        Job job = jobService.findJobById(id);
        model.addAttribute("job", job);
        
    
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            try {
                User currentUser = userService.findUserByEmail(auth.getName());
                boolean hasApplied = applicationService.hasApplied(job, currentUser);
                model.addAttribute("hasApplied", hasApplied);
                model.addAttribute("isJobSeeker", currentUser.getUserType() == User.UserType.JOB_SEEKER);
                model.addAttribute("isEmployer", currentUser.getUserType() == User.UserType.EMPLOYER);
                model.addAttribute("isOwner", job.getEmployer().getId().equals(currentUser.getId()));
            } catch (Exception e) {
                model.addAttribute("hasApplied", false);
            }
        }
        
        return "jobs/view";
    }
    
     @GetMapping("/employer/jobs/create")
public String createJobForm(Model model, RedirectAttributes redirectAttributes) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User employer = userService.findUserByEmail(auth.getName());
    
   
    List<Job> jobs = jobService.findJobsByEmployer(employer);
    if (!employer.getIsPremium() && jobs.size() >= 3) {
        redirectAttributes.addFlashAttribute("error", "Job posting limit reached! Upgrade to Premium for unlimited job postings.");
        return "redirect:/dashboard";
    }
    
    model.addAttribute("job", new Job());
    return "jobs/create";
}

  @PostMapping("/employer/jobs/create")
public String createJob(@ModelAttribute("job") Job job, Model model, RedirectAttributes redirectAttributes) {
    try {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User employer = userService.findUserByEmail(auth.getName());
        
        
        List<Job> jobs = jobService.findJobsByEmployer(employer);
        if (!employer.getIsPremium() && jobs.size() >= 3) {
            redirectAttributes.addFlashAttribute("error", "Job posting limit reached! Upgrade to Premium for unlimited job postings.");
            return "redirect:/dashboard";
        }
        
       
        job.setEmployer(employer);
        job.setStatus(Job.JobStatus.ACTIVE);
        
        
        if (job.getTitle() == null || job.getTitle().trim().isEmpty()) {
            model.addAttribute("error", "Job title is required");
            return "jobs/create";
        }
        if (job.getDescription() == null || job.getDescription().trim().isEmpty()) {
            model.addAttribute("error", "Job description is required");
            return "jobs/create";
        }
        if (job.getLocation() == null || job.getLocation().trim().isEmpty()) {
            model.addAttribute("error", "Job location is required");
            return "jobs/create";
        }
        if (job.getJobType() == null) {
            model.addAttribute("error", "Job type is required");
            return "jobs/create";
        }
        
        Job savedJob = jobService.createJob(job);
        redirectAttributes.addFlashAttribute("success", "Job created successfully!");
        return "redirect:/dashboard";
        
    } catch (Exception e) {
        model.addAttribute("error", "Error creating job: " + e.getMessage());
        return "jobs/create";
    }
}

   


    @GetMapping("/employer/jobs")
    public String listEmployerJobs(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User employer = userService.findUserByEmail(auth.getName());
        
        List<Job> jobs = jobService.findJobsByEmployer(employer);
        model.addAttribute("jobs", jobs);
        
        return "jobs/list";
    }
    

    
    @PostMapping("/employer/jobs/{id}/close")
    public String closeJob(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Job job = jobService.findJobById(id);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findUserByEmail(auth.getName());
        
        if (!job.getEmployer().getId().equals(currentUser.getId()) && currentUser.getUserType() != User.UserType.ADMIN) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access");
            return "redirect:/dashboard";
        }
        
        jobService.closeJob(id);
        redirectAttributes.addFlashAttribute("success", "Job closed successfully!");
        return "redirect:/dashboard";
    }
    
    @PostMapping("/employer/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id) {
        Job job = jobService.findJobById(id);
        
       
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findUserByEmail(auth.getName());
        
        if (!job.getEmployer().getId().equals(currentUser.getId()) && currentUser.getUserType() != User.UserType.ADMIN) {
            return "redirect:/employer/jobs?error=unauthorized";
        }
        
        jobService.deleteJob(id);
        return "redirect:/dashboard";
    }
    
    @GetMapping("/employer/resume/{applicationId}")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long applicationId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User employer = userService.findUserByEmail(auth.getName());
            
            JobApplication application = applicationService.findApplicationById(applicationId);
            
            if (!application.getJob().getEmployer().getId().equals(employer.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
          
            if (application.getResumePath() == null || application.getResumePath().isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            
            Path filePath = Paths.get(application.getResumePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
               
                String filename = filePath.getFileName().toString();
                
                
                if (filename.contains("_")) {
                    filename = filename.substring(filename.indexOf("_") + 1);
                }
                
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    

    

    

    

}
