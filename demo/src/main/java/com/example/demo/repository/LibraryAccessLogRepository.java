package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.LibraryAccessLog;
import com.example.demo.entity.User;

/**
 * LibraryAccessLogRepository: Data access layer for LibraryAccessLog entity
 * Tracks user entry/exit from the library
 */
@Repository
public interface LibraryAccessLogRepository extends JpaRepository<LibraryAccessLog, Long> {

    /**
     * Find all access logs for a specific user
     */
    List<LibraryAccessLog> findByUser(User user);

    /**
     * Find the current active session for a user (exit time is null)
     */
    Optional<LibraryAccessLog> findByUserAndExitTimeIsNull(User user);

    /**
     * Find all currently active sessions (users in library)
     */
    List<LibraryAccessLog> findByExitTimeIsNull();

    /**
     * Find access logs within a date range
     */
    List<LibraryAccessLog> findByEntryTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find by session token
     */
    Optional<LibraryAccessLog> findBySessionToken(String sessionToken);

    /**
     * Find access logs by user and within a date range
     */
    List<LibraryAccessLog> findByUserAndEntryTimeBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
}
