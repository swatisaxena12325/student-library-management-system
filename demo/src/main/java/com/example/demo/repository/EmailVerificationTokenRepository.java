package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.EmailVerificationToken;
import com.example.demo.entity.User;

/**
 * EmailVerificationTokenRepository: Data access layer for EmailVerificationToken entity
 * Manages email verification tokens for new user registrations
 */
@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    /**
     * Find token by its string value
     */
    Optional<EmailVerificationToken> findByToken(String token);

    /**
     * Find token by user
     */
    Optional<EmailVerificationToken> findByUser(User user);

    /**
     * Check if token exists
     */
    boolean existsByToken(String token);
}
