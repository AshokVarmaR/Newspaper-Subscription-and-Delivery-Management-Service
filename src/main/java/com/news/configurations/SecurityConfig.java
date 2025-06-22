package com.news.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.news.enums.UserRole;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // -------------------- CUSTOMER SECURITY --------------------
    @Bean
    @Order(1)
    public SecurityFilterChain customerSecurity(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/customer/**", "/customer/login", "/customer/register", "/customer/signup", "/customer/validate-login", "/customer/logout", "/customer/forgot-password")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/customer/signup", "/customer/register", "/customer/login","/customer/forgot-password", "/customer/otp/send", "/customer/otp/verify", "/customer/password/reset").permitAll()
                .requestMatchers("/customer/**").hasRole(UserRole.CUSTOMER.name())
                .anyRequest().authenticated())
            .formLogin(login -> login
                .loginProcessingUrl("/customer/validate-login")
                .loginPage("/customer/login")
                .defaultSuccessUrl("/customer/dashboard", true)
                .failureUrl("/customer/login?error=true"))
            .logout(logout -> logout
                .logoutUrl("/customer/logout")
                .logoutSuccessUrl("/customer/login?logout=true"))
            .csrf(csrf -> csrf.disable()); // Optional: disable CSRF if you're not using POST forms
        return http.build();
    }

    // -------------------- EMPLOYEE SECURITY --------------------
    @Bean
    @Order(2)
    public SecurityFilterChain employeeSecurity(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/employee/**", "/employee/login", "/employee/register", "/employee/signup", "/employee/validate-login", "/employee/logout")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/employee/signup", "/employee/register", "/employee/login").permitAll()
                .requestMatchers("/employee/**").hasRole(UserRole.EMPLOYEE.name())
                .anyRequest().authenticated())
            .formLogin(login -> login
                .loginProcessingUrl("/employee/validate-login")
                .loginPage("/employee/login")
                .defaultSuccessUrl("/employee/dashboard", true)
                .failureUrl("/employee/login?error=true"))
            .logout(logout -> logout
                .logoutUrl("/employee/logout")
                .logoutSuccessUrl("/employee/login?logout=true"))
            .csrf(csrf -> csrf.disable());
        return http.build();
    }

    // -------------------- ADMIN SECURITY --------------------
    @Bean
    @Order(3)
    public SecurityFilterChain adminSecurity(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/admin/**", "/admin/login", "/admin/register", "/admin/signup", "/admin/validate-login", "/admin/logout")
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers("/admin/**").permitAll())
            		
//                .requestMatchers("/admin/signup", "/admin/register", "/admin/login").permitAll()
//                .requestMatchers("/admin/**").hasRole(UserRole.ADMIN.name())
//                .anyRequest().authenticated())
//            .formLogin(login -> login
//                .loginProcessingUrl("/admin/validate-login")
//                .loginPage("/admin/login")
//                .defaultSuccessUrl("/admin/dashboard", true)
//                .failureUrl("/admin/login?error=true"))
//            .logout(logout -> logout
//                .logoutUrl("/admin/logout")
//                .logoutSuccessUrl("/admin/login?logout=true"))
            .csrf(csrf -> csrf.disable());
        return http.build();
    }

    // -------------------- COMMON AUTHENTICATION BEANS --------------------
//    @Bean
//    public AuthenticationManager authenticationManager(
//            AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new CustomUserDetailsService();
//    }

}
