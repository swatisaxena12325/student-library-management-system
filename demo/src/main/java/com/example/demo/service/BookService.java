package com.example.demo.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Book;
import com.example.demo.repository.BookRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BookService: Business logic for book management
 * - Browse books
 * - Search books
 * - Update book quantity
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;

    /**
     * Get all books
     */
    public List<Book> getAllBooks() {
        log.debug("Fetching all books");
        return bookRepository.findAll();
    }

    /**
     * Get book by ID
     */
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(Objects.requireNonNull(id));
    }

    /**
     * Get all available books (quantity > 0)
     */
    public List<Book> getAvailableBooks() {
        log.debug("Fetching available books");
        return bookRepository.findByQuantityGreaterThan(0);
    }

    /**
     * Search books by title
     */
    public List<Book> searchByTitle(String title) {
        log.debug("Searching books by title: {}", title);
        return bookRepository.findByTitleIgnoreCaseContaining(title);
    }

    /**
     * Search books by author
     */
    public List<Book> searchByAuthor(String author) {
        log.debug("Searching books by author: {}", author);
        return bookRepository.findByAuthorIgnoreCaseContaining(author);
    }

    /**
     * Get books by category
     */
    public List<Book> getBooksByCategory(String category) {
        log.debug("Fetching books by category: {}", category);
        return bookRepository.findByCategory(category);
    }

    /**
     * Create a new book (admin only)
     */
    @Transactional
    public Book createBook(Book book) {
        log.info("Creating new book: {}", book.getTitle());

        if (bookRepository.existsByIsbn(book.getIsbn())) {
            log.warn("Book creation failed: ISBN already exists: {}", book.getIsbn());
            throw new IllegalArgumentException("ISBN already exists");
        }

        return bookRepository.save(book);
    }

    /**
     * Update book details (admin only)
     */
    @Transactional
    public Book updateBook(Long id, Book bookDetails) {
        log.info("Updating book: {}", id);

        Optional<Book> book = bookRepository.findById(Objects.requireNonNull(id));
        if (book.isPresent()) {
            Book existingBook = book.get();
            existingBook.setTitle(bookDetails.getTitle());
            existingBook.setAuthor(bookDetails.getAuthor());
            existingBook.setQuantity(bookDetails.getQuantity());
            existingBook.setCategory(bookDetails.getCategory());
            existingBook.setDescription(bookDetails.getDescription());
            return bookRepository.save(existingBook);
        }

        throw new IllegalArgumentException("Book not found");
    }

    /**
     * Decrease book quantity by 1 when issued
     */
    @Transactional
    public Book decreaseQuantity(Long bookId) {
        log.debug("Decreasing quantity for book: {}", bookId);

        Optional<Book> book = bookRepository.findById(Objects.requireNonNull(bookId));
        if (book.isPresent()) {
            Book existingBook = book.get();
            if (existingBook.getQuantity() > 0) {
                existingBook.setQuantity(existingBook.getQuantity() - 1);
                return bookRepository.save(existingBook);
            } else {
                throw new IllegalArgumentException("Book out of stock");
            }
        }

        throw new IllegalArgumentException("Book not found");
    }

    /**
     * Get all categories
     */
    public List<String> getAllCategories() {
        return bookRepository.findAll().stream()
                .map(Book::getCategory)
                .distinct()
                .toList();
    }
}
