package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;

/**
 * UserRepository: Data access layer for User entity
 * Provides CRUD operations and custom queries for users
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find all users by role
     */
    List<User> findByRole(UserRole role);

    /**
     * Find all users currently logged in
     */
    List<User> findByLoggedInTrue();

    /**
     * Find user by session token
     */
    Optional<User> findBySessionToken(String sessionToken);
}
