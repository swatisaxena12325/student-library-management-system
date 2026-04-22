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
 * BookIssuance Entity: Tracks when a book is issued by a user.
 * 
 * This is a join table with metadata:
 * - Which user issued the book
 * - Which book was issued
 * - When it was issued (timestamp)
 * - This enables the admin dashboard to show all issuances
 * 
 * Relationships:
 * - Many-to-one: Many BookIssuances belong to one User
 * - Many-to-one: Many BookIssuances belong to one Book
 */
@Entity
@Table(name = "book_issuances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookIssuance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many-to-one: The student who issued the book
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User issuedBy;

    /**
     * Many-to-one: The book that was issued
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /**
     * Timestamp when the book was issued
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime issuanceDate;

    /**
     * Email confirmation sent timestamp
     */
    private LocalDateTime emailSentAt;

    /**
     * Automatically set issuance date on creation
     */
    @PrePersist
    protected void onCreate() {
        issuanceDate = LocalDateTime.now();
    }

    /**
     * Get a formatted string of the book details for display
     */
    public String getBookDetails() {
        return book.getTitle() + " by " + book.getAuthor();
    }
}
