package com.jobportal.services;

import com.jobportal.models.Job;
import com.jobportal.models.JobApplication;
import com.jobportal.models.User;
import com.jobportal.repositories.JobApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
public class JobApplicationService {
    
    private final JobApplicationRepository applicationRepository;
    
    public JobApplicationService(JobApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }
    
    public JobApplication saveApplication(JobApplication application) {
        return applicationRepository.save(application);
    }
    
    public JobApplication findApplicationById(Long id) {
        return applicationRepository.findByIdWithJobAndApplicant(id)
                .orElseThrow(() -> new EntityNotFoundException("Application with id " + id + " not found"));
    }
    
    public List<JobApplication> findApplicationsByApplicant(User applicant) {
        return applicationRepository.findByApplicant(applicant);
    }
    
    public List<JobApplication> findApplicationsByJob(Job job) {
        return applicationRepository.findByJob(job);
    }
    
    public boolean hasApplied(Job job, User applicant) {
        return applicationRepository.existsByJobAndApplicant(job, applicant);
    }
    
    public void updateApplicationStatus(JobApplication application, String status) {
        try {
            JobApplication.ApplicationStatus newStatus = JobApplication.ApplicationStatus.valueOf(status);
            application.setStatus(newStatus);
            applicationRepository.save(application);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }
}
