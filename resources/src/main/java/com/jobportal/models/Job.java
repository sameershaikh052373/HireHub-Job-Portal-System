package com.jobportal.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "jobs")
public class Job {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    private String title;
    
    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotBlank
    private String location;
    
    private String salary;
    
    @Enumerated(EnumType.STRING)
    private JobType jobType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id")
    private User employer;
    
    @Enumerated(EnumType.STRING)
    private JobStatus status;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    private LocalDateTime expiresAt;
    
    private String requirements;
    
    private String benefits;
    
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<JobApplication> applications = new HashSet<>();
    
    public enum JobType {
        FULL_TIME,
        PART_TIME,
        CONTRACT,
        INTERNSHIP,
        REMOTE
    }
    
    public enum JobStatus {
        ACTIVE,
        CLOSED,
        DRAFT
    }

    public Job() {}

    public Job(Long id, String title, String description, String location, String salary, JobType jobType, User employer, JobStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime expiresAt, String requirements, String benefits, Set<JobApplication> applications) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.salary = salary;
        this.jobType = jobType;
        this.employer = employer;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.expiresAt = expiresAt;
        this.requirements = requirements;
        this.benefits = benefits;
        this.applications = applications;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public User getEmployer() {
        return employer;
    }

    public void setEmployer(User employer) {
        this.employer = employer;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public Set<JobApplication> getApplications() {
        return applications;
    }

    public void setApplications(Set<JobApplication> applications) {
        this.applications = applications;
    }
}
