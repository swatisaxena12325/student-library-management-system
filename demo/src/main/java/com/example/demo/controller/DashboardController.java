package com.example.demo.controller;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.entity.User;
import com.example.demo.service.BookIssuanceService;
import com.example.demo.service.BookService;
import com.example.demo.service.LibraryAccessService;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DashboardController: Handles dashboard requests for students and admins
 * - User dashboard (browse books, view issuances)
 * - Admin dashboard (view library logs, user activity)
 */
@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final UserService userService;
    private final BookService bookService;
    private final BookIssuanceService bookIssuanceService;
    private final LibraryAccessService libraryAccessService;

    /**
     * Main dashboard page - redirects to admin or user dashboard based on role
     */
    @GetMapping
    public String dashboard(Authentication authentication, Model model) {
        log.debug("Dashboard requested");

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/auth/login";
        }

        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> userOpt = userService.findByEmail(userEmail);

        if (userOpt.isEmpty()) {
            return "redirect:/auth/login";
        }

        User user = userOpt.get();

        if (user.isAdmin()) {
            return "redirect:/dashboard/admin";
        } else {
            return "redirect:/dashboard/student";
        }
    }

    /**
     * Student dashboard
     */
    @GetMapping("/student")
    public String studentDashboard(Authentication authentication, Model model) {
        log.debug("Student dashboard requested");

        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> userOpt = userService.findByEmail(userEmail);

        if (userOpt.isEmpty()) {
            return "redirect:/auth/login";
        }

        User user = userOpt.get();

        // Add user info
        model.addAttribute("user", user);

        // Add available books
        model.addAttribute("availableBooks", bookService.getAvailableBooks());

        // Add categories
        model.addAttribute("categories", bookService.getAllCategories());

        // Add user's issued books
        model.addAttribute("userIssuances", bookIssuanceService.getUserIssuances(user));

        // Add library access info
        model.addAttribute("inLibrary", libraryAccessService.isUserInLibrary(user));

        return "dashboard/student";
    }

    /**
     * Admin dashboard
     */
    @GetMapping("/admin")
    public String adminDashboard(Authentication authentication, Model model) {
        log.debug("Admin dashboard requested");

        String userEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> userOpt = userService.findByEmail(userEmail);

        if (userOpt.isEmpty()) {
            return "redirect:/auth/login";
        }

        User adminUser = userOpt.get();

        if (!adminUser.isAdmin()) {
            return "redirect:/error/access-denied";
        }

        // Dashboard statistics
        model.addAttribute("totalStudents", userService.getAllStudents().size());
        model.addAttribute("totalBooks", bookService.getAllBooks().size());
        model.addAttribute("totalIssuances", bookIssuanceService.getAllIssuances().size());
        model.addAttribute("currentlyInLibrary", libraryAccessService.getCurrentlyInLibrary());

        // All students
        model.addAttribute("students", userService.getAllStudents());

        // All issuances
        model.addAttribute("issuances", bookIssuanceService.getAllIssuances());

        // Access logs
        model.addAttribute("accessLogs", libraryAccessService.getCurrentlyInLibrary());

        return "dashboard/admin";
    }
}
