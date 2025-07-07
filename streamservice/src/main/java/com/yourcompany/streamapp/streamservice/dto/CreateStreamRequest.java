package com.yourcompany.streamapp.streamservice.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateStreamRequest {
    @NotBlank
    private String title;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}