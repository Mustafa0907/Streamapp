package com.yourcompany.streamapp.user_service.controller;


import com.yourcompany.streamapp.user_service.config.JwtUtils;
import com.yourcompany.streamapp.user_service.dto.JwtResponse;
import com.yourcompany.streamapp.user_service.dto.LoginRequest;
import com.yourcompany.streamapp.user_service.dto.RegistrationRequest;
import com.yourcompany.streamapp.user_service.entity.User;
import com.yourcompany.streamapp.user_service.service.UserDetailsServiceImpl;
import com.yourcompany.streamapp.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


//import com.yourcompany.streamapp.userservice.config.jwt.JwtUtils;
//import com.yourcompany.streamapp.userservice.dto.JwtResponse;
//import com.yourcompany.streamapp.userservice.dto.LoginRequest;
//import com.yourcompany.streamapp.userservice.dto.RegistrationRequest;
//import com.yourcompany.streamapp.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth") // Changed base path to /api/auth
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest req) {
        try {
            userService.registerNewUser(req);
            return ResponseEntity.ok("User registered successfully!");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // --- THE FIX IS HERE ---
        // Get the UserDetails object from the authentication's "principal"
        org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        // Pass this UserDetails object to the token generator
        String jwt = jwtUtils.generateToken(userDetails);
        // --- END FIX ---
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        User appUser = userService.findByUsername(userDetails.getUsername());

        return ResponseEntity.ok(new JwtResponse(jwt,
                appUser.getId(),
                appUser.getUsername(),
                appUser.getEmail(),
                roles));
    }
}