package com.yourcompany.streamapp.streamservice.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "streams")
public class Stream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    // CORRECTED: This field must be a String to match our logic.
    @Column(nullable = false)
    private String hostUsername;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StreamStatus status;

    private Instant createdAt;
    private Instant endedAt;

    public enum StreamStatus {
        PENDING,
        LIVE,
        ENDED
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getHostUsername() { return hostUsername; }
    public void setHostUsername(String hostUsername) { this.hostUsername = hostUsername; }
    public StreamStatus getStatus() { return status; }
    public void setStatus(StreamStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getEndedAt() { return endedAt; }
    public void setEndedAt(Instant endedAt) { this.endedAt = endedAt; }
}