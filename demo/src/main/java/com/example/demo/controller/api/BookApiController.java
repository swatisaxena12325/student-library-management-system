package com.example.demo.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Book;
import com.example.demo.service.BookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BookApiController: REST API for book operations
 * - List all books
 * - Search books
 * - Get book details
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookApiController {

    private final BookService bookService;

    /**
     * Get all books
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Book>> getAllBooks() {
        log.debug("API: Getting all books");
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /**
     * Get available books only
     */
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Book>> getAvailableBooks() {
        log.debug("API: Getting available books");
        return ResponseEntity.ok(bookService.getAvailableBooks());
    }

    /**
     * Get book by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        log.debug("API: Getting book by ID: {}", id);
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search books by title
     */
    @GetMapping("/search/title")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Book>> searchByTitle(@RequestParam String q) {
        log.debug("API: Searching books by title: {}", q);
        return ResponseEntity.ok(bookService.searchByTitle(q));
    }

    /**
     * Search books by author
     */
    @GetMapping("/search/author")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Book>> searchByAuthor(@RequestParam String q) {
        log.debug("API: Searching books by author: {}", q);
        return ResponseEntity.ok(bookService.searchByAuthor(q));
    }

    /**
     * Get books by category
     */
    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Book>> getByCategory(@PathVariable String category) {
        log.debug("API: Getting books by category: {}", category);
        return ResponseEntity.ok(bookService.getBooksByCategory(category));
    }

    /**
     * Get all categories
     */
    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<String>> getCategories() {
        log.debug("API: Getting all categories");
        return ResponseEntity.ok(bookService.getAllCategories());
    }
}
