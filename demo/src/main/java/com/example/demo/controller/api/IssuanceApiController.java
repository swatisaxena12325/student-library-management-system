package com.example.demo.controller.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Book;
import com.example.demo.entity.BookIssuance;
import com.example.demo.entity.LibraryAccessLog;
import com.example.demo.entity.User;
import com.example.demo.service.BookIssuanceService;
import com.example.demo.service.BookService;
import com.example.demo.service.EmailService;
import com.example.demo.service.LibraryAccessService;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * IssuanceApiController: REST API for book issuance operations
 * - Issue books
 * - Get issuance history
 * - Handle library entry/exit
 */
@RestController
@RequestMapping("/api/issuances")
@RequiredArgsConstructor
@Slf4j
public class IssuanceApiController {

    private final BookIssuanceService bookIssuanceService;
    private final BookService bookService;
    private final UserService userService;
    private final LibraryAccessService libraryAccessService;
    private final EmailService emailService;

    /**
     * Issue a single book
     */
    @PostMapping("/issue")
    public ResponseEntity<?> issueBook(@RequestParam Long bookId, Authentication authentication) {
        log.info("API: Book issuance requested for book: {}", bookId);

        try {
            String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
            Optional<User> userOpt = userService.findByEmail(userEmail);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            Optional<Book> bookOpt = bookService.getBookById(bookId);
            if (bookOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
            }

            User user = userOpt.get();
            Book book = bookOpt.get();

            // Issue the book
            BookIssuance issuance = bookIssuanceService.issueBook(user, book);
            bookIssuanceService.markEmailSent(issuance);

            log.info("Book issued successfully: {}", bookId);
            return ResponseEntity.ok(issuance);
        } catch (IllegalArgumentException e) {
            log.warn("Book issuance failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during book issuance", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    /**
     * Issue multiple books at once
     */
    @PostMapping("/issue-multiple")
    public ResponseEntity<?> issueMultipleBooks(@RequestParam List<Long> bookIds, Authentication authentication) {
        log.info("API: Multiple book issuance requested for books: {}", bookIds);

        try {
            String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
            Optional<User> userOpt = userService.findByEmail(userEmail);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            User user = userOpt.get();
            List<BookIssuance> issuances = new ArrayList<>();

            for (Long bookId : bookIds) {
                Optional<Book> bookOpt = bookService.getBookById(bookId);
                if (bookOpt.isPresent()) {
                    try {
                        BookIssuance issuance = bookIssuanceService.issueBook(user, bookOpt.get());
                        issuances.add(issuance);
                    } catch (IllegalArgumentException e) {
                        log.warn("Failed to issue book {}: {}", bookId, e.getMessage());
                    }
                }
            }

            // Send confirmation email
            if (!issuances.isEmpty()) {
                emailService.sendIssuanceConfirmationEmail(user, issuances);
            }

            log.info("Multiple books issued successfully: {} books", issuances.size());
            return ResponseEntity.ok(issuances);
        } catch (Exception e) {
            log.error("Unexpected error during multiple book issuance", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    /**
     * Get user's issuance history
     */
    @GetMapping("/history")
    public ResponseEntity<List<BookIssuance>> getIssuanceHistory(Authentication authentication) {
        log.debug("API: Getting issuance history");

        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> userOpt = userService.findByEmail(userEmail);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userOpt.get();
        return ResponseEntity.ok(bookIssuanceService.getUserIssuances(user));
    }

    /**
     * Enter library (login)
     */
    @PostMapping("/enter")
    public ResponseEntity<?> enterLibrary(Authentication authentication) {
        log.info("API: User entering library");

        try {
            String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
            Optional<User> userOpt = userService.findByEmail(userEmail);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            User user = userOpt.get();

            // Generate session token
            String sessionToken = UUID.randomUUID().toString();

            // Log entry
            LibraryAccessLog accessLog = libraryAccessService.logEntry(user, sessionToken);

            // Update user login status
            user = userService.login(user);

            log.info("User entered library successfully");
            return ResponseEntity.ok(Map.of(
                    "message", "Library entry logged",
                    "sessionToken", sessionToken,
                    "accessLogId", accessLog.getId()
            ));
        } catch (Exception e) {
            log.error("Error during library entry", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    /**
     * Exit library (logout)
     */
    @PostMapping("/exit")
    public ResponseEntity<?> exitLibrary(Authentication authentication) {
        log.info("API: User exiting library");

        try {
            String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
            Optional<User> userOpt = userService.findByEmail(userEmail);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            User user = userOpt.get();

            // Log exit
            LibraryAccessLog accessLog = libraryAccessService.logExit(user);

            // Update user login status
            user = userService.logout(user);

            log.info("User exited library successfully");
            return ResponseEntity.ok(Map.of(
                    "message", "Library exit logged",
                    "durationMinutes", accessLog.getDurationInMinutes()
            ));
        } catch (Exception e) {
            log.warn("Error during library exit: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Check if user is in library
     */
    @GetMapping("/in-library")
    public ResponseEntity<Boolean> isInLibrary(Authentication authentication) {
        log.debug("API: Checking if user is in library");

        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> userOpt = userService.findByEmail(userEmail);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userOpt.get();
        boolean inLibrary = libraryAccessService.isUserInLibrary(user);
        return ResponseEntity.ok(inLibrary);
    }
}
