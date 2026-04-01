package com.jobportal.controllers;

import com.jobportal.services.JobService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    private final JobService jobService;

    public HomeController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/")
    public String home(Model model) {
    
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        model.addAttribute("latestJobs", jobService.findAllJobs(pageable));
        return "home";
    }
    
    @GetMapping("/about")
    public String about() {
        return "about";
    }
}