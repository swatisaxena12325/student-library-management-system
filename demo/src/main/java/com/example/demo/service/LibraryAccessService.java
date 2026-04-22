package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.LibraryAccessLog;
import com.example.demo.entity.User;
import com.example.demo.repository.LibraryAccessLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * LibraryAccessService: Business logic for tracking library entry/exit
 * - Log user entry (login)
 * - Log user exit (logout)
 * - Get access history
 * - Get currently logged in users
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LibraryAccessService {

    private final LibraryAccessLogRepository accessLogRepository;

    /**
     * Log user entry to the library (user login)
     * @param user The user entering the library
     * @param sessionToken The session token
     * @return Created LibraryAccessLog
     */
    @Transactional
    public LibraryAccessLog logEntry(User user, String sessionToken) {
        log.info("User entering library: {} with session token: {}", user.getEmail(), sessionToken);

        // Check if user already has an active session
        Optional<LibraryAccessLog> activeSession = accessLogRepository.findByUserAndExitTimeIsNull(user);
        if (activeSession.isPresent()) {
            log.warn("User already has an active session");
            // Close the previous session if any
            LibraryAccessLog previousSession = activeSession.get();
            previousSession.setExitTime(LocalDateTime.now());
            accessLogRepository.save(previousSession);
        }

        LibraryAccessLog accessLog = LibraryAccessLog.builder()
                .user(user)
                .sessionToken(sessionToken)
                .build();

        LibraryAccessLog savedLog = accessLogRepository.save(Objects.requireNonNull(accessLog));
        log.info("User entry logged successfully");
        return savedLog;
    }

    /**
     * Log user exit from the library (user logout)
     * @param user The user exiting the library
     * @return Updated LibraryAccessLog
     */
    @Transactional
    public LibraryAccessLog logExit(User user) {
        log.info("User exiting library: {}", user.getEmail());

        Optional<LibraryAccessLog> activeSession = accessLogRepository.findByUserAndExitTimeIsNull(user);
        if (activeSession.isEmpty()) {
            log.warn("No active session found for user: {}", user.getEmail());
            throw new IllegalArgumentException("No active session found");
        }

        LibraryAccessLog accessLog = activeSession.get();
        accessLog.setExitTime(LocalDateTime.now());
        LibraryAccessLog updatedLog = accessLogRepository.save(accessLog);
        log.info("User exit logged successfully");
        return updatedLog;
    }

    /**
     * Get access history for a user
     */
    public List<LibraryAccessLog> getUserAccessHistory(User user) {
        log.debug("Fetching access history for user: {}", user.getEmail());
        return accessLogRepository.findByUser(user);
    }

    /**
     * Get all users currently in the library
     */
    public List<LibraryAccessLog> getCurrentlyInLibrary() {
        log.debug("Fetching all users currently in library");
        return accessLogRepository.findByExitTimeIsNull();
    }

    /**
     * Get all access logs within a date range
     */
    public List<LibraryAccessLog> getAccessLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching access logs between {} and {}", startDate, endDate);
        return accessLogRepository.findByEntryTimeBetween(startDate, endDate);
    }

    /**
     * Get access log by session token
     */
    public Optional<LibraryAccessLog> getBySessionToken(String sessionToken) {
        return accessLogRepository.findBySessionToken(sessionToken);
    }

    /**
     * Get user's current active session
     */
    public Optional<LibraryAccessLog> getUserActiveSession(User user) {
        return accessLogRepository.findByUserAndExitTimeIsNull(user);
    }

    /**
     * Check if user is currently in the library
     */
    public boolean isUserInLibrary(User user) {
        return accessLogRepository.findByUserAndExitTimeIsNull(user).isPresent();
    }

    /**
     * Get total library visits by a user
     */
    public long getTotalVisits(User user) {
        return accessLogRepository.findByUser(user).stream()
                .filter(accessLog -> accessLog.getExitTime() != null)
                .count();
    }

    /**
     * Get average time spent in library (in minutes)
     */
    public double getAverageTimeSpent(User user) {
        List<LibraryAccessLog> logs = accessLogRepository.findByUser(user);
        if (logs.isEmpty()) {
            return 0;
        }

        long totalMinutes = logs.stream()
                .filter(accessLog -> accessLog.getExitTime() != null)
                .mapToLong(LibraryAccessLog::getDurationInMinutes)
                .sum();

        return (double) totalMinutes / logs.size();
    }
}
