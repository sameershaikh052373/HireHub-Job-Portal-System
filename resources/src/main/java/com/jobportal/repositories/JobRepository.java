package com.jobportal.repositories;

import com.jobportal.models.Job;
import com.jobportal.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;  // Add this import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByEmployer(User employer);
    List<Job> findByStatus(Job.JobStatus status);
    List<Job> findByJobType(Job.JobType jobType);
    Page<Job> findByStatus(Job.JobStatus status, Pageable pageable);
    
    @Query("SELECT j FROM Job j WHERE j.status = 'ACTIVE' AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Job> searchJobs(String keyword);
    
    @Query("SELECT j FROM Job j WHERE j.status = 'ACTIVE' AND " +
           "LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Job> findByLocation(String location);
    
    @Query("SELECT j FROM Job j WHERE j.status = 'ACTIVE' AND " +
           "(LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Job> searchJobsByKeywordAndLocation(String keyword, String location);
    
    Page<Job> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
