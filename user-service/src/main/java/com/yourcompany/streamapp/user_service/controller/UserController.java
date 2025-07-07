package com.yourcompany.streamapp.user_service.controller;


import com.yourcompany.streamapp.user_service.dto.RoleAssignmentRequest;
import com.yourcompany.streamapp.user_service.dto.UpdateProfileRequestDto;
import com.yourcompany.streamapp.user_service.dto.UpdateUserStatusRequestDto;
import com.yourcompany.streamapp.user_service.dto.UserProfileDto;
import com.yourcompany.streamapp.user_service.entity.User;
import com.yourcompany.streamapp.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private UserProfileDto convertToDto(User user) {
        return new UserProfileDto(user.getId(), user.getUsername(), user.getEmail(), user.getIsActive());
    }

    // --- Category 1: Essential User Profile Management ---
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUser(@RequestHeader("X-Authenticated-Username") String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(convertToDto(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateCurrentUser(@RequestHeader("X-Authenticated-Username") String username,
                                                            @RequestBody UpdateProfileRequestDto req) {
        User updatedUser = userService.updateUserProfile(username, req);
        return ResponseEntity.ok(convertToDto(updatedUser));
    }

    // --- Category 2: User Interaction and Discovery ---
    @GetMapping("/{username}")
    public ResponseEntity<UserProfileDto> getPublicProfile(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(convertToDto(user));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserProfileDto>> searchUsers(@RequestParam("q") String query) {
        List<UserProfileDto> users = userService.searchUsers(query).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // --- Category 3: Administrative Functions ---
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileDto>> getAllUsers() {
        List<UserProfileDto> users = userService.findAllUsers().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{username}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileDto> updateUserStatus(@PathVariable String username, @RequestBody UpdateUserStatusRequestDto req) {
        User updatedUser = userService.updateUserStatus(username, req.getIsActive());
        return ResponseEntity.ok(convertToDto(updatedUser));
    }
    @PostMapping("/roles/add")
    @PreAuthorize("hasRole('ADMIN')") // Only users with ROLE_ADMIN can access this
    public ResponseEntity<?> addRole(@Valid @RequestBody RoleAssignmentRequest request) {
        try {
            userService.addRoleToUser(request.getUsername(), request.getRoleName());
            return ResponseEntity.ok("Role " + request.getRoleName() + " added to user " + request.getUsername());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/roles/remove")
    @PreAuthorize("hasRole('ADMIN')") // Only users with ROLE_ADMIN can access this
    public ResponseEntity<?> removeRole(@Valid @RequestBody RoleAssignmentRequest request) {
        try {
            userService.removeRoleFromUser(request.getUsername(), request.getRoleName());
            return ResponseEntity.ok("Role " + request.getRoleName() + " removed from user " + request.getUsername());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
