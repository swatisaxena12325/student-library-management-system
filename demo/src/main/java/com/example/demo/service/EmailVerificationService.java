package com.example.demo.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.EmailVerificationToken;
import com.example.demo.entity.User;
import com.example.demo.repository.EmailVerificationTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EmailVerificationService: Business logic for email verification tokens
 * - Create verification tokens
 * - Verify tokens
 * - Manage token lifecycle
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserService userService;

    /**
     * Create verification token for user and send email
     */
    @Transactional
    public EmailVerificationToken createVerificationToken(User user) {
        log.info("Creating verification token for user: {}", user.getEmail());

        // Delete existing token if any
        Optional<EmailVerificationToken> existingToken = tokenRepository.findByUser(user);
        existingToken.ifPresent(tokenRepository::delete);

        // Create new token
        EmailVerificationToken token = EmailVerificationToken.builder()
                .user(user)
                .build();

        EmailVerificationToken savedToken = tokenRepository.save(Objects.requireNonNull(token));
        log.debug("Verification token created");

        // Send verification email
        emailService.sendVerificationEmail(user, savedToken.getToken());

        return savedToken;
    }

    /**
     * Verify email using token
     */
    @Transactional
    public User verifyEmail(String token) {
        log.info("Verifying email with token: {}", token);

        Optional<EmailVerificationToken> verificationToken = tokenRepository.findByToken(token);

        if (verificationToken.isEmpty()) {
            log.warn("Verification token not found: {}", token);
            throw new IllegalArgumentException("Invalid verification token");
        }

        EmailVerificationToken emailToken = verificationToken.get();

        // Check if token is expired
        if (emailToken.isExpired()) {
            log.warn("Verification token has expired: {}", token);
            throw new IllegalArgumentException("Verification token has expired");
        }

        // Check if token has already been used
        if (emailToken.getIsUsed()) {
            log.warn("Verification token has already been used: {}", token);
            throw new IllegalArgumentException("Verification token has already been used");
        }

        // Mark token as used
        emailToken.setIsUsed(true);
        tokenRepository.save(emailToken);

        // Mark user email as verified
        User user = emailToken.getUser();
        user = userService.verifyEmail(user);

        log.info("Email verified successfully for user: {}", user.getEmail());
        return user;
    }

    /**
     * Get verification token by token string
     */
    public Optional<EmailVerificationToken> getByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    /**
     * Get verification token by user
     */
    public Optional<EmailVerificationToken> getByUser(User user) {
        return tokenRepository.findByUser(user);
    }

    /**
     * Check if token is valid
     */
    public boolean isTokenValid(String token) {
        Optional<EmailVerificationToken> verificationToken = tokenRepository.findByToken(token);
        if (verificationToken.isEmpty()) {
            return false;
        }

        EmailVerificationToken emailToken = verificationToken.get();
        return emailToken.isValid();
    }

    /**
     * Delete verification token
     */
    @Transactional
    public void deleteToken(EmailVerificationToken token) {
        tokenRepository.delete(Objects.requireNonNull(token));
    }
}
