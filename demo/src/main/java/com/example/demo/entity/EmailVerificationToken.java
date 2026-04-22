package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EmailVerificationToken Entity: Manages email verification for new registrations.
 * 
 * When a user registers:
 * 1. A verification token is created and stored in this table
 * 2. An email is sent with a link containing the token
 * 3. User clicks the link, which sends the token back to the server
 * 4. If token is valid (not expired, exists), user's email is marked as verified
 * 
 * Relationships:
 * - One-to-one: Each token belongs to one User
 */
@Entity
@Table(name = "email_verification_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The verification token (unique random string)
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * One-to-one: Associated user
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Token creation timestamp
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Token expiration timestamp (24 hours from creation)
     */
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    /**
     * Whether the token has been used to verify email
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    @Builder.Default
    private Boolean isUsed = false;

    /**
     * Generate token on creation
     */
    @PrePersist
    protected void onCreate() {
        token = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
        expiryDate = createdAt.plusHours(24); // Token valid for 24 hours
    }

    /**
     * Check if token is valid (not expired and not used)
     */
    public boolean isValid() {
        return !isUsed && LocalDateTime.now().isBefore(expiryDate);
    }

    /**
     * Check if token has expired
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
