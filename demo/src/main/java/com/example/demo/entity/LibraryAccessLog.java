package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LibraryAccessLog Entity: Tracks user entry and exit from the library.
 * 
 * This is used by the admin dashboard to see:
 * - When a student entered (logged in)
 * - When a student exited (logged out)
 * - How long they spent in the library
 * 
 * Relationships:
 * - Many-to-one: Many access logs belong to one User
 */
@Entity
@Table(name = "library_access_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many-to-one: The user who logged in/out
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Entry timestamp (when user logged in)
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime entryTime;

    /**
     * Exit timestamp (when user logged out)
     * Can be null if user hasn't exited yet
     */
    private LocalDateTime exitTime;

    /**
     * Session token for tracking active sessions
     */
    @Column(nullable = false)
    private String sessionToken;

    /**
     * Automatically set entry time on creation
     */
    @PrePersist
    protected void onCreate() {
        entryTime = LocalDateTime.now();
    }

    /**
     * Calculate duration in library in minutes
     */
    public long getDurationInMinutes() {
        if (exitTime == null) {
            return -1; // User still in library
        }
        return java.time.temporal.ChronoUnit.MINUTES.between(entryTime, exitTime);
    }

    /**
     * Check if user is currently in the library
     */
    public boolean isCurrentlyInLibrary() {
        return exitTime == null;
    }
}
