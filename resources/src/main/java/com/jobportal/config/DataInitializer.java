package com.jobportal.config;

import com.jobportal.models.Role;
import com.jobportal.models.User;
import com.jobportal.repositories.RoleRepository;
import com.jobportal.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        if (roleRepository.count() == 0) {
            Role userRole = new Role(Role.ERole.ROLE_USER);
            Role employerRole = new Role(Role.ERole.ROLE_EMPLOYER);
            Role adminRole = new Role(Role.ERole.ROLE_ADMIN);
            
            roleRepository.save(userRole);
            roleRepository.save(employerRole);
            roleRepository.save(adminRole);
        }
        
        // Create admin user if it doesn't exist
        if (!userRepository.existsByEmail("admin@jobportal.com")) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@jobportal.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setUserType(User.UserType.ADMIN);
            
            Set<Role> roles = new HashSet<>();
            Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
            admin.setRoles(roles);
            
            userRepository.save(admin);
        }
    }
}