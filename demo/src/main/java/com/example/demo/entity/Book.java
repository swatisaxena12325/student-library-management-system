package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
/**
 * Book Entity: Represents a book in the library.
 * 
 * Features:
 * - Title, Author, ISBN, and other metadata
 * - Quantity tracking (total copies available)
 * - Timestamps for creation and last modification
 * 
 * Relationships:
 * - One Book can be issued multiple times (one-to-many with BookIssuance)
 */
@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Book title
     */
    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    /**
     * Author name
     */
    @NotBlank(message = "Author is required")
    @Column(nullable = false)
    private String author;

    /**
     * International Standard Book Number (unique identifier)
     */
    @Column(nullable = false, unique = true)
    private String isbn;

    /**
     * Book description/summary
     */
    @Column(length = 1000)
    private String description;

    /**
     * Total copies available in the library
     */
    @Positive(message = "Quantity must be greater than 0")
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Category (e.g., Science, Fiction, History, Technology)
     */
    @Column(nullable = false)
    private String category;

    /**
     * Publication year
     */
    private Integer publicationYear;

    /**
     * Publisher name
     */
    private String publisher;

    /**
     * Book creation timestamp
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Last updated timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * One-to-many: One book can be issued multiple times
     */
    @ToString.Exclude
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<BookIssuance> issuances = new HashSet<>();

    /**
     * Automatically set creation and update timestamps
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}