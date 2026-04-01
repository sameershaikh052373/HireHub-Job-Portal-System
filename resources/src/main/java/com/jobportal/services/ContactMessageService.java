package com.jobportal.services;

import com.jobportal.models.ContactMessage;
import com.jobportal.repositories.ContactMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactMessageService {
    
    private final ContactMessageRepository contactMessageRepository;
    
    public ContactMessageService(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }
    
    public ContactMessage saveMessage(ContactMessage message) {
        return contactMessageRepository.save(message);
    }
    
    public List<ContactMessage> findAllMessages() {
        return contactMessageRepository.findByOrderByCreatedAtDesc();
    }
    
    public List<ContactMessage> findUnreadMessages() {
        return contactMessageRepository.findByIsReadOrderByCreatedAtDesc(false);
    }
    
    public ContactMessage findMessageById(Long id) {
        return contactMessageRepository.findById(id).orElse(null);
    }
    
    public ContactMessage markAsRead(Long id) {
        ContactMessage message = findMessageById(id);
        if (message != null) {
            message.setIsRead(true);
            return contactMessageRepository.save(message);
        }
        return null;
    }
    
    public long getUnreadCount() {
        return contactMessageRepository.countByIsRead(false);
    }
    
    public void deleteMessage(Long id) {
        contactMessageRepository.deleteById(id);
    }
}