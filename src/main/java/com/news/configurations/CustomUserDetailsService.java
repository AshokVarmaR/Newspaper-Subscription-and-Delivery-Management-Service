package com.news.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.news.enums.UserRole;
import com.news.models.User;
import com.news.repositories.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private HttpServletRequest request;

    @Autowired
    private UserRepository userRepo;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        
        String loginPath = request.getRequestURI();

        // Reject if user is logging in from the wrong path
        if (loginPath.startsWith("/admin") && user.getRole() != UserRole.ADMIN) {
            throw new BadCredentialsException("Access denied: not an admin");
        } else if (loginPath.startsWith("/customer") && user.getRole() != UserRole.CUSTOMER) {
            throw new BadCredentialsException("Access denied: not a citizen");
        } else if (loginPath.startsWith("/employee") && user.getRole() != UserRole.EMPLOYEE) {
            throw new BadCredentialsException("Access denied: not an officer");
        }
        
        return new CustomUserDetails(user);
    }
}
