package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Book;
import com.example.demo.entity.BookIssuance;
import com.example.demo.entity.User;
import com.example.demo.repository.BookIssuanceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BookIssuanceService: Business logic for book issuances (checkouts)
 * - Issue books to students
 * - Track issuances
 * - Get issuance history
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookIssuanceService {

    private final BookIssuanceRepository bookIssuanceRepository;
    private final BookService bookService;

    /**
     * Issue a book to a user
     * @param user The student issuing the book
     * @param book The book being issued
     * @return Created BookIssuance record
     */
    @Transactional
    public BookIssuance issueBook(User user, Book book) {
        log.info("Issuing book {} to user {}", book.getId(), user.getEmail());

        // Check if book is available
        if (book.getQuantity() <= 0) {
            log.warn("Cannot issue book: Out of stock - {}", book.getTitle());
            throw new IllegalArgumentException("Book is out of stock");
        }

        // Create issuance record
        BookIssuance issuance = BookIssuance.builder()
                .issuedBy(user)
                .book(book)
                .emailSentAt(null)
                .build();

        BookIssuance savedIssuance = bookIssuanceRepository.save(Objects.requireNonNull(issuance));

        // Decrease book quantity
        bookService.decreaseQuantity(book.getId());

        log.info("Book issued successfully");
        return savedIssuance;
    }

    /**
     * Get all issuances by a user
     */
    public List<BookIssuance> getUserIssuances(User user) {
        log.debug("Fetching issuances for user: {}", user.getEmail());
        return bookIssuanceRepository.findByIssuedBy(user);
    }

    /**
     * Get all issuances of a specific book
     */
    public List<BookIssuance> getBookIssuances(Book book) {
        log.debug("Fetching issuances for book: {}", book.getTitle());
        return bookIssuanceRepository.findByBook(book);
    }

    /**
     * Get all issuances (admin dashboard)
     */
    public List<BookIssuance> getAllIssuances() {
        log.debug("Fetching all issuances");
        return bookIssuanceRepository.findAll();
    }

    /**
     * Get issuances within a date range
     */
    public List<BookIssuance> getIssuancesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching issuances between {} and {}", startDate, endDate);
        return bookIssuanceRepository.findByIssuanceDateBetween(startDate, endDate);
    }

    /**
     * Mark email as sent for an issuance
     */
    @Transactional
    public BookIssuance markEmailSent(BookIssuance issuance) {
        log.debug("Marking email as sent for issuance: {}", issuance.getId());
        issuance.setEmailSentAt(LocalDateTime.now());
        return bookIssuanceRepository.save(issuance);
    }

    /**
     * Get total issuances by a user
     */
    public long getTotalIssuancesByUser(User user) {
        return bookIssuanceRepository.countByIssuedBy(user);
    }

    /**
     * Get total issuances of a book
     */
    public long getTotalIssuancesOfBook(Book book) {
        return bookIssuanceRepository.countByBook(book);
    }

    /**
     * Get issuance history (for pagination and filtering)
     */
    public List<BookIssuance> getIssuanceHistory(User user, int limit) {
        List<BookIssuance> issuances = getUserIssuances(user);
        return issuances.stream().limit(limit).toList();
    }
}
