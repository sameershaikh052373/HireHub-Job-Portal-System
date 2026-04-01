package com.jobportal.services;

import com.jobportal.models.Job;
import com.jobportal.models.User;
import com.jobportal.repositories.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobService {
    
    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }
    
    public Job createJob(Job job) {
        if (job.getStatus() == null) {
            job.setStatus(Job.JobStatus.ACTIVE);
        }
        if (job.getCreatedAt() == null) {
            job.setCreatedAt(LocalDateTime.now());
        }
        return jobRepository.save(job);
    }

    public Job updateJob(Job job) {
        return jobRepository.save(job);
    }
    
    public Job findJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Job with id " + id + " not found"));
    }
    
    public List<Job> findAllJobs() {
        return jobRepository.findAll();
    }
    
    public Page<Job> findAllJobs(Pageable pageable) {
        return jobRepository.findAll(pageable);
    }
    
    public List<Job> findJobsByEmployer(User employer) {
        return jobRepository.findByEmployer(employer);
    }
    
    public List<Job> findActiveJobs() {
        return jobRepository.findByStatus(Job.JobStatus.ACTIVE);
    }
    
    public List<Job> searchJobs(String keyword) {
        return jobRepository.searchJobs(keyword);
    }
    
    public List<Job> findJobsByLocation(String location) {
        return jobRepository.findByLocation(location);
    }
    
    public List<Job> searchJobsByKeywordAndLocation(String keyword, String location) {
        return jobRepository.searchJobsByKeywordAndLocation(keyword, location);
    }
    
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
    
    public void closeJob(Long id) {
        Job job = findJobById(id);
        job.setStatus(Job.JobStatus.CLOSED);
        jobRepository.save(job);
    }

    public Page<Job> findActiveJobs(Pageable pageable) {
        return jobRepository.findByStatus(Job.JobStatus.ACTIVE, pageable);
    }
    
    public Page<Job> findJobsByCategory(String category, Pageable pageable) {
        return jobRepository.findByTitleContainingIgnoreCase(category, pageable);
    }

}