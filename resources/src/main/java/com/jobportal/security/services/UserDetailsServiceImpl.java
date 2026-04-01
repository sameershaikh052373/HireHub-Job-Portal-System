package com.jobportal.security.services;
import java.util.ArrayList;

import com.jobportal.models.Role;
import com.jobportal.models.User;
import com.jobportal.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                getAuthorities(user)
        );
    }
    
     private Collection<? extends GrantedAuthority> getAuthorities(User user) {
    // Temporarily bypass roles and use UserType
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    
    switch (user.getUserType()) {
        case JOB_SEEKER:
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            break;
        case EMPLOYER:
            authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYER"));
            break;
        case ADMIN:
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            break;
    }
    
    return authorities;
}

}
