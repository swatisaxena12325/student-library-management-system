package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.User;
import com.example.demo.service.EmailVerificationService;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AuthController: Handles authentication-related requests
 * - Registration
 * - Email verification
 * - Login page rendering
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    /**
     * Display login page
     */
    @GetMapping("/login")
    public String loginPage(Model model, @RequestParam(required = false) String error, @RequestParam(required = false) String logout) {
        log.debug("Login page requested");

        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }

        return "auth/login";
    }

    /**
     * Display registration page
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        log.debug("Registration page requested");
        model.addAttribute("user", new User());
        return "auth/register";
    }

    /**
     * Handle user registration
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        log.info("Registration request for email: {}", user.getEmail());

        if (result.hasErrors()) {
            log.debug("Validation errors in registration: {}", result.getAllErrors());
            return "auth/register";
        }

        try {
            // Register the user
            User registeredUser = userService.registerUser(user.getName(), user.getEmail(), user.getPassword());

            // Create and send verification email
            emailVerificationService.createVerificationToken(registeredUser);

            log.info("User registered successfully: {}", user.getEmail());
            model.addAttribute("message", "Registration successful! A verification email has been sent to your email address.");
            return "auth/verification-pending";
        } catch (IllegalArgumentException e) {
            log.warn("Registration failed: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        } catch (Exception e) {
            log.error("Unexpected error during registration", e);
            model.addAttribute("error", "An error occurred during registration. Please try again.");
            return "auth/register";
        }
    }

    /**
     * Verify email using token
     */
    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token, Model model) {
        log.info("Email verification requested with token: {}", token);

        try {
            // Verify the email
            User user = emailVerificationService.verifyEmail(token);

            log.info("Email verified successfully for user: {}", user.getEmail());
            model.addAttribute("message", "Email verified successfully! You can now log in.");
            model.addAttribute("email", user.getEmail());
            return "auth/verification-success";
        } catch (IllegalArgumentException e) {
            log.warn("Email verification failed: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "auth/verification-error";
        } catch (Exception e) {
            log.error("Unexpected error during email verification", e);
            model.addAttribute("error", "An error occurred during email verification. Please try again.");
            return "auth/verification-error";
        }
    }

    /**
     * Verification pending page
     */
    @GetMapping("/verification-pending")
    public String verificationPending() {
        log.debug("Verification pending page requested");
        return "auth/verification-pending";
    }

    /**
     * Check if user is authenticated and redirect accordingly
     */
    @GetMapping("/")
    public String redirectHome(Authentication authentication) {
        log.debug("Home redirect requested");

        if (authentication != null && authentication.isAuthenticated()) {
            // User is logged in
            return "redirect:/dashboard";
        }

        return "redirect:/auth/login";
    }

    @GetMapping("/auth/access-denied")
public String accessDenied() {
    return "access-denied";
}
}
