package com.yourcompany.streamapp.streamservice.dto;

import com.yourcompany.streamapp.streamservice.model.Stream;
import java.time.Instant;

public class StreamResponse {
    private Long id;
    private String title;
    private String hostUsername; // This should be a String
    private String status;
    private Instant createdAt;

    // This constructor ensures we map the entity to the DTO correctly.
    public StreamResponse(Stream stream) {
        this.id = stream.getId();
        this.title = stream.getTitle();
        this.hostUsername = stream.getHostUsername(); // Reading the String field
        this.status = stream.getStatus().name();
        this.createdAt = stream.getCreatedAt();
    }

    // --- Getters ---
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getHostUsername() { return hostUsername; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}