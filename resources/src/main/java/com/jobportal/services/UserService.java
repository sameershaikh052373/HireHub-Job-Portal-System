package com.jobportal.services;

import com.jobportal.models.User;
import com.jobportal.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with id " + id + " not found"));
    }
    
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
    }
    
    
    
    public Optional<User> findUserByEmailOptional(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> findAllJobSeekers() {
        return (List<User>) userRepository.findByUserType(User.UserType.JOB_SEEKER);
    }
    
    public List<User> findAllEmployers() {
        return (List<User>) userRepository.findByUserType(User.UserType.EMPLOYER);
    }
    
    public User updateUser(User user) {
    return userRepository.save(user);
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    
    public List<User> findByUserType(User.UserType userType) {
        return userRepository.findByUserType(userType);
    }

    


}