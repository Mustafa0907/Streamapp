package com.yourcompany.streamapp.user_service.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequestDto {
    @Email(message = "Email should be valid")
    private String email;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
