package com.example.demo.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * User Entity: Represents both Admin and Student users in the system.
 * 
 * Role:
 * - ROLE_ADMIN: Has access to admin dashboard, can view all library logs
 * - ROLE_USER: Regular student, can browse and issue books
 * 
 * Relationships:
 * - One User can issue multiple books (one-to-many with BookIssuance)
 * - One User can have multiple library access logs (one-to-many with LibraryAccessLog)
 * - One User can have verification token for email confirmation
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor(force=true)
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's name - displayed in logs and UI
     */
    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    /**
     * User's email - must be unique
     */
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Encrypted password
     */
    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    /**
     * User's role: ROLE_ADMIN or ROLE_USER
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    /**
     * Whether the email has been verified
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    @Builder.Default
    private Boolean emailVerified = false;

    /**
     * Registration timestamp
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    /**
     * Last login timestamp
     */
    private LocalDateTime lastLoginDate;

    /**
     * Whether the user is currently logged in (in the library)
     * Note: Field intentionally named 'loggedIn' (not 'isLoggedIn') to avoid
     * Lombok generating broken getter/setter names (isIsLoggedIn / setIsLoggedIn).
     * Lombok with Boolean wrapper + 'is' prefix strips the prefix, causing
     * getter → isLoggedIn(), setter → setLoggedIn() — mismatching call sites.
     */
    @Column(name = "is_logged_in", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    @Builder.Default
    private Boolean loggedIn = false;

    /**
     * User's current session token (for JWT or session tracking)
     */
    private String sessionToken;

    /**
     * One-to-many: User can issue multiple books
     */
    @ToString.Exclude
    @OneToMany(mappedBy = "issuedBy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<BookIssuance> issuedBooks = new HashSet<>();

    /**
     * One-to-many: User can have multiple library access logs
     */
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<LibraryAccessLog> libraryAccessLogs = new HashSet<>();

    /**
     * One-to-one: User can have one email verification token
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EmailVerificationToken verificationToken;

    /**
     * Automatically set registration date on entity creation
     */
    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
        loggedIn = false;
        
    }

    /**
     * Spring Security: Returns authorities based on user role
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.name()));
        return authorities;
    }

    /**
     * Spring Security: Returns username (we use email as username)
     */
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Spring Security: Account is always enabled
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Spring Security: Account is always non-locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Spring Security: Credentials always non-expired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Spring Security: Enabled only if email is verified
     */
    @Override
    public boolean isEnabled() {
        return emailVerified;
    }

    /**
     * Check if user is an admin
     */
    public boolean isAdmin() {
        return role == UserRole.ROLE_ADMIN;
    }
}