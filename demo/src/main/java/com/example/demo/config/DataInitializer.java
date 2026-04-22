package com.example.demo.config;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.entity.Book;
import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DataInitializer: Pre-populates the database with sample data
 * 
 * Creates:
 * - Admin user (from configuration)
 * - Sample books with various genres
 * 
 * Runs automatically on application startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing database with sample data");

        // Create admin user if not exists
        if (!userRepository.existsByEmail(adminEmail)) {
            createAdminUser();
        }

        // Create sample books if database is empty
        if (bookRepository.count() == 0) {
            createSampleBooks();
        }

        log.info("Database initialization completed");
    }

    /**
     * Create admin user with credentials from application.properties
     */
    
    private void createAdminUser() {
        log.info("Creating admin user: {}", adminEmail);

        User admin = User.builder()
                .name("Admin User")
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(UserRole.ROLE_ADMIN)
                .emailVerified(true)
                .loggedIn(false)
                .registrationDate(LocalDateTime.now())
                .build();

        userRepository.save(Objects.requireNonNull(admin));
        log.info("Admin user created successfully");
    }

    /**
     * Create sample books for testing
     */
    private void createSampleBooks() {
        log.info("Creating sample books");

        // Science & Technology
        bookRepository.save(Objects.requireNonNull(Book.builder()
                .title("The Catcher in the Rye")
                .author("J.D. Salinger")
                .isbn("978-0-316-76948-0")
                .description("A classic novel about teenage angst and alienation")
                .quantity(5)
                .category("Fiction")
                .publicationYear(1951)
                .publisher("Little, Brown")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()));

        bookRepository.save(Objects.requireNonNull(Book.builder()
                .title("To Kill a Mockingbird")
                .author("Harper Lee")
                .isbn("978-0-06-112008-4")
                .description("A gripping tale of race and justice in the deep south")
                .quantity(4)
                .category("Fiction")
                .publicationYear(1960)
                .publisher("J.B. Lippincott")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()));

        bookRepository.save(Objects.requireNonNull(Book.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0-13-235088-4")
                .description("A guide to writing clean, maintainable code")
                .quantity(3)
                .category("Technology")
                .publicationYear(2008)
                .publisher("Prentice Hall")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()));

        bookRepository.save(Objects.requireNonNull(Book.builder()
                .title("The Design of Everyday Things")
                .author("Don Norman")
                .isbn("978-0-465-02723-2")
                .description("Understanding user experience and good design")
                .quantity(3)
                .category("Technology")
                .publicationYear(2013)
                .publisher("Basic Books")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()));

        bookRepository.save(Objects.requireNonNull(Book.builder()
                .title("Atomic Habits")
                .author("James Clear")
                .isbn("978-0-735-21159-7")
                .description("Tiny changes, remarkable results")
                .quantity(6)
                .category("Self-Help")
                .publicationYear(2018)
                .publisher("Avery")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()));

        bookRepository.save(Objects.requireNonNull(Book.builder()
                .title("Thinking, Fast and Slow")
                .author("Daniel Kahneman")
                .isbn("978-0-374-27563-1")
                .description("Explore how our minds think and make decisions")
                .quantity(4)
                .category("Psychology")
                .publicationYear(2011)
                .publisher("Farrar, Straus and Giroux")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()));

        bookRepository.save(Objects.requireNonNull(Book.builder()
                .title("A Brief History of Time")
                .author("Stephen Hawking")
                .isbn("978-0-553-38016-3")
                .description("From the Big Bang to Black Holes")
                .quantity(3)
                .category("Science")
                .publicationYear(1988)
                .publisher("Bantam")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()));

        bookRepository.save(Objects.requireNonNull(Book.builder()
                .title("The Lean Startup")
                .author("Eric Ries")
                .isbn("978-0-307-88789-4")
                .description("How today's entrepreneurs build successful businesses")
                .quantity(4)
                .category("Business")
                .publicationYear(2011)
                .publisher("Crown Business")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()));

        bookRepository.save(Objects.requireNonNull(Book.builder()
                .title("The Great Gatsby")
                .author("F. Scott Fitzgerald")
                .isbn("978-0-7432-7356-5")
                .description("A timeless tale of wealth, love and the American Dream")
                .quantity(5)
                .category("Fiction")
                .publicationYear(1925)
                .publisher("Scribner")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()));

        bookRepository.save(Objects.requireNonNull(Book.builder()
                .title("Sapiens")
                .author("Yuval Noah Harari")
                .isbn("978-0-062-31657-1")
                .description("A brief history of humankind")
                .quantity(4)
                .category("History")
                .publicationYear(2014)
                .publisher("Harper")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()));

        log.info("Sample books created successfully");
    }
}
