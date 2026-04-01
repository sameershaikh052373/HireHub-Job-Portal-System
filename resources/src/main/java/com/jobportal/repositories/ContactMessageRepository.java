package com.jobportal.repositories;

import com.jobportal.models.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    List<ContactMessage> findByOrderByCreatedAtDesc();
    List<ContactMessage> findByIsReadOrderByCreatedAtDesc(Boolean isRead);
    long countByIsRead(Boolean isRead);
}