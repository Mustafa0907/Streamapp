package com.yourcompany.streamapp.user_service.dto;


import jakarta.validation.constraints.NotNull;

public class UpdateUserStatusRequestDto {
    @NotNull
    private Boolean isActive;

    // Getters and Setters
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
}
