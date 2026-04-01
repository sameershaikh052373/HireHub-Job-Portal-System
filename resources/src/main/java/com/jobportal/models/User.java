package com.jobportal.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Email(message = "Please enter a valid email address")
    @Column(unique = true)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 120, message = "Password must be between 6 and 120 characters")
    @Column(name = "password_a")
    private String password;
    
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;
    
    @Enumerated(EnumType.STRING)
    private UserType userType;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // For job seekers
    private String resume;
    private String skills;
    private String experience;
    private String education;
    private String profileImage;
    
    // For employers
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    private String companyName;
    
    @Size(max = 500, message = "Company description must not exceed 500 characters")
    private String companyDescription;
    
    @Size(max = 100, message = "Company website must not exceed 100 characters")
    private String companyWebsite;
    
    private String companyLogo;
    
    @Size(max = 50, message = "Industry must not exceed 50 characters")
    private String industry;
    
    @Column(name = "is_premium", nullable = false)
    private Boolean isPremium = false;

    @Column(name = "premium_expires_at")
    private LocalDateTime premiumExpiresAt;

    @Column(name = "jobs_posted_count", nullable = false)
    private Integer jobsPostedCount = 0;

    @Column(name = "jobs_applied_count", nullable = false)
    private Integer jobsAppliedCount = 0;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
    
    public enum UserType {
        JOB_SEEKER,
        EMPLOYER,
        ADMIN
    }
    
    // Constructors
    public User() {
        this.jobsPostedCount = 0;
        this.jobsAppliedCount = 0;
        this.isPremium = false;
        this.enabled = true;
    }
    
    public User(String firstName, String lastName, String email, String password, UserType userType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.jobsPostedCount = 0;
        this.jobsAppliedCount = 0;
        this.isPremium = false;
        this.enabled = true;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    // Add this method for Spring Security
    public boolean isEnabled() {
        return enabled != null ? enabled : true;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
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

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Boolean getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(Boolean isPremium) {
        this.isPremium = isPremium;
    }

    public LocalDateTime getPremiumExpiresAt() {
        return premiumExpiresAt;
    }

    public void setPremiumExpiresAt(LocalDateTime premiumExpiresAt) {
        this.premiumExpiresAt = premiumExpiresAt;
    }

    public Integer getJobsPostedCount() {
        return jobsPostedCount;
    }

    public void setJobsPostedCount(Integer jobsPostedCount) {
        this.jobsPostedCount = jobsPostedCount;
    }

    public Integer getJobsAppliedCount() {
        return jobsAppliedCount;
    }

    public void setJobsAppliedCount(Integer jobsAppliedCount) {
        this.jobsAppliedCount = jobsAppliedCount;
    }
}
