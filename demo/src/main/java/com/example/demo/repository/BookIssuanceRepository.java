package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Book;
import com.example.demo.entity.BookIssuance;
import com.example.demo.entity.User;

/**
 * BookIssuanceRepository: Data access layer for BookIssuance entity
 * Tracks book issuances (checkouts) by users
 */
@Repository
public interface BookIssuanceRepository extends JpaRepository<BookIssuance, Long> {

    /**
     * Find all issuances by a specific user
     */
    List<BookIssuance> findByIssuedBy(User user);

    /**
     * Find all issuances of a specific book
     */
    List<BookIssuance> findByBook(Book book);

    /**
     * Find issuances within a date range
     */
    List<BookIssuance> findByIssuanceDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find all issuances (for admin dashboard)
     */
    

    /**
     * Count total issuances by a user
     */
    long countByIssuedBy(User user);

    /**
     * Count total issuances of a book
     */
    long countByBook(Book book);
}
