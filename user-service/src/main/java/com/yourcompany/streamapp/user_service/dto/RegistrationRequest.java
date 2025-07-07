package com.yourcompany.streamapp.user_service.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// This is a plain old Java object (POJO) for transferring data.
public class RegistrationRequest {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 40, message = "Password must be at least 6 characters")
    private String password;

    // --- Getters and Setters ---
    // (Generate them automatically in your IDE)
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}