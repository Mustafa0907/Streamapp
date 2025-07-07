package com.yourcompany.streamapp.user_service.service;

import com.yourcompany.streamapp.user_service.dto.RegistrationRequest;
import com.yourcompany.streamapp.user_service.entity.User;
import com.yourcompany.streamapp.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yourcompany.streamapp.user_service.dto.UpdateProfileRequestDto;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerNewUser(RegistrationRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new IllegalStateException("Error: Username is already taken!");
        }
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalStateException("Error: Email is already in use!");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setIsActive(true);
        user.setRoles(Set.of("ROLE_USER")); // Default role
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
    }

    @Transactional(readOnly = true)
    public List<User> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUserProfile(String username, UpdateProfileRequestDto req) {
        User user = findByUsername(username);
        if(req.getEmail() != null && !req.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(req.getEmail()).isPresent()) {
                throw new IllegalStateException("Error: Email is already in use!");
            }
            user.setEmail(req.getEmail());
        }
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserStatus(String username, boolean isActive) {
        User user = findByUsername(username);
        user.setIsActive(isActive);
        return userRepository.save(user);
    }
    @Transactional
    public User addRoleToUser(String username, String roleName) {
        // Ensure the role starts with "ROLE_" for Spring Security compatibility
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName.toUpperCase();
        }

        User user = findByUsername(username);
        user.getRoles().add(roleName); // Add the new role to the set of roles
        return userRepository.save(user);
    }

    @Transactional
    public User removeRoleFromUser(String username, String roleName) {
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName.toUpperCase();
        }

        User user = findByUsername(username);
        user.getRoles().remove(roleName); // Remove the role
        return userRepository.save(user);
    }
}