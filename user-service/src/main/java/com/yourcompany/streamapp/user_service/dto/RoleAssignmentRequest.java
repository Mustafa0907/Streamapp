package com.yourcompany.streamapp.user_service.dto;

import jakarta.validation.constraints.NotBlank;

public class RoleAssignmentRequest {

    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Role cannot be blank")
    private String roleName;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
