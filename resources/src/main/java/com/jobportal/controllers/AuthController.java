package com.jobportal.controllers;

import com.jobportal.models.Role;
import com.jobportal.models.User;
import com.jobportal.repositories.RoleRepository;
import com.jobportal.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }
    

    @GetMapping("/login")
    public String login() {
        return "login";
    }


  
    
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    


    @GetMapping("/register/jobseeker")
    public String registerJobSeekerForm(Model model) {
        User user = new User();
        user.setUserType(User.UserType.JOB_SEEKER);
        model.addAttribute("user", user);
        return "register-jobseeker";
    }
    
    @GetMapping("/register/employer")
    public String registerEmployerForm(Model model) {
        User user = new User();
        user.setUserType(User.UserType.EMPLOYER);
        model.addAttribute("user", user);
        return "register-employer";
    }
     
    

    @PostMapping("/register/jobseeker")
    public String registerJobSeeker(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register-jobseeker";
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", "Email is already in use!");
            return "register-jobseeker";
        }

        // Set password encoding
        user.setPassword(encoder.encode(user.getPassword()));
        
       
        user.setUserType(User.UserType.JOB_SEEKER);

       
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);
        
        userRepository.save(user);

        return "redirect:/login?registered";
    }
    
    @PostMapping("/register/employer")
    public String registerEmployer(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        // Additional validation for employer fields
        if (user.getCompanyName() == null || user.getCompanyName().trim().isEmpty()) {
            result.rejectValue("companyName", "error.user", "Company name is required");
        }
        
        if (result.hasErrors()) {
            return "register-employer";
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("emailError", "Email is already in use!");
            return "register-employer";
        }

        // Set password encoding
        user.setPassword(encoder.encode(user.getPassword()));
        
        // Set user type
        user.setUserType(User.UserType.EMPLOYER);

        // Set roles
        Set<Role> roles = new HashSet<>();
        Role employerRole = roleRepository.findByName(Role.ERole.ROLE_EMPLOYER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(employerRole);
        user.setRoles(roles);
        
        userRepository.save(user);

        return "redirect:/login?registered";
    }
}