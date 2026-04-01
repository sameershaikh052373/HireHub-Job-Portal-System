package com.jobportal.repositories;

import com.jobportal.models.Job;
import com.jobportal.models.JobApplication;
import com.jobportal.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByApplicant(User applicant);
    List<JobApplication> findByJob(Job job);
    Optional<JobApplication> findByJobAndApplicant(Job job, User applicant);
    List<JobApplication> findByStatus(JobApplication.ApplicationStatus status);
    List<JobApplication> findByJobAndStatus(Job job, JobApplication.ApplicationStatus status);
    boolean existsByJobAndApplicant(Job job, User applicant);
    
    @Query("SELECT ja FROM JobApplication ja JOIN FETCH ja.job j JOIN FETCH ja.applicant a WHERE ja.id = :id")
    Optional<JobApplication> findByIdWithJobAndApplicant(@Param("id") Long id);
}
