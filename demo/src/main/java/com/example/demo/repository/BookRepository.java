package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Book;

/**
 * BookRepository: Data access layer for Book entity
 * Provides CRUD operations and custom queries for books
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Find book by ISBN
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Check if ISBN exists
     */
    boolean existsByIsbn(String isbn);

    /**
     * Find all books by category
     */
    List<Book> findByCategory(String category);

    /**
     * Search books by title (case-insensitive)
     */
    List<Book> findByTitleIgnoreCaseContaining(String title);

    /**
     * Search books by author (case-insensitive)
     */
    List<Book> findByAuthorIgnoreCaseContaining(String author);

    /**
     * Find books with quantity > 0 (available books)
     */
    List<Book> findByQuantityGreaterThan(Integer quantity);
}
