package com.yourcompany.streamapp.user_service.config;

import com.yourcompany.streamapp.user_service.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * This bean provides the password hashing algorithm.
     * It's needed by both the AuthenticationManager (for login)
     * and the UserService (for registration).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * This bean provides the core authentication mechanism.
     * It's used by the AuthController's /login endpoint to validate credentials.
     * It correctly uses the UserDetailsServiceImpl and PasswordEncoder to do this.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * This is the HTTP security configuration.
     * It is simplified to permit all requests because we trust the API Gateway
     * to have already performed JWT validation for protected routes.
     * The AuthController's login endpoint is handled by the AuthenticationManager directly.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        // Trust all incoming requests. The gateway is our security guard.
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}