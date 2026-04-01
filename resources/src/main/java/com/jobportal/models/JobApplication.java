package com.jobportal.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
public class JobApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;
    
    @Column(columnDefinition = "TEXT")
    private String coverLetter;
    
    private String resumePath;
    
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public enum ApplicationStatus {
        APPLIED,
        REVIEWING,
        SHORTLISTED,
        INTERVIEWED,
        REJECTED,
        OFFERED,
        HIRED
    }

    // Constructors and getters/setters remain the same...
    public JobApplication() {}

    public JobApplication(Long id, Job job, User applicant, String coverLetter, String resumePath, ApplicationStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.job = job;
        this.applicant = applicant;
        this.coverLetter = coverLetter;
        this.resumePath = resumePath;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // All getters and setters remain the same...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    public User getApplicant() { return applicant; }
    public void setApplicant(User applicant) { this.applicant = applicant; }
    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    public String getResumePath() { return resumePath; }
    public void setResumePath(String resumePath) { this.resumePath = resumePath; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
