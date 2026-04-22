package com.example.demo.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * UserService: Business logic for user management
 * - Registration
 * - Login/Logout
 * - User data retrieval
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user (student)
     * @param name User's full name
     * @param email User's email
     * @param password User's password (will be encrypted)
     * @return Created user
     */
    @Transactional
    public User registerUser(String name, String email, String password) {
        log.info("Registering new user: {}", email);

        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            log.warn("Registration failed: Email already exists: {}", email);
            throw new IllegalArgumentException("Email already registered");
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(UserRole.ROLE_USER)
                .emailVerified(false)
                .loggedIn(false)
                .build();

        User savedUser = userRepository.save(Objects.requireNonNull(user));
        log.info("User registered successfully: {}", email);
        return savedUser;
    }

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Find user by ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(Objects.requireNonNull(id));
    }

    /**
     * User login: Update login status and create session token
     */
    @Transactional
    public User login(User user) {
        log.info("User login: {}", user.getEmail());
        user.setLoggedIn(true);
        user.setLastLoginDate(java.time.LocalDateTime.now());
        user.setSessionToken(UUID.randomUUID().toString());
        return userRepository.save(user);
    }

    /**
     * User logout: Reset login status and session token
     */
    @Transactional
    public User logout(User user) {
        log.info("User logout: {}", user.getEmail());
        user.setLoggedIn(false);
        user.setSessionToken(null);
        return userRepository.save(user);
    }

    /**
     * Get all users with ROLE_USER
     */
    public List<User> getAllStudents() {
        return userRepository.findByRole(UserRole.ROLE_USER);
    }

    /**
     * Get all currently logged in users
     */
    public List<User> getLoggedInUsers() {
        return userRepository.findByLoggedInTrue();
    }

    /**
     * Verify user's email
     */
    @Transactional
    public User verifyEmail(User user) {
        log.info("Email verified for user: {}", user.getEmail());
        user.setEmailVerified(true);
        return userRepository.save(user);
    }

    /**
     * Find user by session token
     */
    public Optional<User> findBySessionToken(String sessionToken) {
        return userRepository.findBySessionToken(sessionToken);
    }

    /**
     * Check if a password matches the user's encrypted password
     */
    public boolean passwordMatches(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
