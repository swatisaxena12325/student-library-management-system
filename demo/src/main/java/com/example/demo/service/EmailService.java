package com.example.demo.service;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.demo.entity.BookIssuance;
import com.example.demo.entity.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * EmailService: Handles email sending for various events
 * - Email verification during registration
 * - Book issuance confirmation
 * - Library notifications
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@studentlibrary.com}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Send email verification link to new user
     * @param user The newly registered user
     * @param token The email verification token
     */
    public void sendVerificationEmail(User user, String token) {
        log.info("Sending verification email to: {}", user.getEmail());

        try {
            String verificationLink = baseUrl + "/auth/verify?token=" + token;
            String subject = "Email Verification - Student Library System";
            String content = buildVerificationEmailContent(user.getName(), verificationLink);

            sendHtmlEmail(user.getEmail(), subject, content);
            log.info("Verification email sent successfully");
        } catch (MessagingException e) {
            log.error("Failed to send verification email to: {}", user.getEmail(), e);
        }
    }

    /**
     * Send book issuance confirmation email with list of issued books
     * @param user The student who issued the books
     * @param issuances List of book issuances
     */
    public void sendIssuanceConfirmationEmail(User user, List<BookIssuance> issuances) {
        log.info("Sending issuance confirmation email to: {}", user.getEmail());

        try {
            String subject = "Book Issuance Confirmation - Student Library System";
            String content = buildIssuanceConfirmationContent(user.getName(), issuances);

            sendHtmlEmail(user.getEmail(), subject, content);
            log.info("Issuance confirmation email sent successfully");
        } catch (MessagingException e) {
            log.error("Failed to send issuance confirmation email to: {}", user.getEmail(), e);
        }
    }

    

    /**
     * Send an HTML email
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        log.debug("Sending HTML email to: {}", to);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom(Objects.requireNonNull(fromEmail));
        helper.setTo(Objects.requireNonNull(to));
        helper.setSubject(Objects.requireNonNull(subject));
        helper.setText(Objects.requireNonNull(htmlContent), true);

        mailSender.send(mimeMessage);
    }

    /**
     * Build HTML content for verification email
     */
    private String buildVerificationEmailContent(String name, String verificationLink) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 5px 5px 0 0; text-align: center; }
                        .content { background-color: #f9f9f9; padding: 20px; border-radius: 0 0 5px 5px; }
                        .button { display: inline-block; background-color: #667eea; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                        .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #999; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Welcome to Student Library System</h1>
                        </div>
                        <div class="content">
                            <h2>Hello %s,</h2>
                            <p>Thank you for registering with our Student Library Access Management System.</p>
                            <p>To verify your email and complete your registration, please click the button below:</p>
                            <a href= "%s" class="button">Verify Email</a>
                            <p><strong>Or copy this link in your browser:</strong></p>
                            <p><a href= "%s" style="color: #667eea;">"%s"</a></p>
                            <p>This link will expire in 24 hours.</p>
                            <p>If you did not create this account, please ignore this email.</p>
                        </div>
                        <div class="footer">
                            <p> &copy; 2026 Student Library System. All rights reserved. </p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(name, verificationLink, verificationLink, verificationLink);
    }

    /**
     * Build HTML content for book issuance confirmation email
     */
    private String buildIssuanceConfirmationContent(String name, List<BookIssuance> issuances) {
        StringBuilder bookList = new StringBuilder();
        for (BookIssuance issuance : issuances) {
            bookList.append("<tr>")
                    .append("<td style=\"padding: 10px; border-bottom: 1px solid #ddd;\">")
                    .append(issuance.getBook().getTitle()).append("</td>")
                    .append("<td style=\"padding: 10px; border-bottom: 1px solid #ddd;\">")
                    .append(issuance.getBook().getAuthor()).append("</td>")
                    .append("<td style=\"padding: 10px; border-bottom: 1px solid #ddd;\">")
                    .append(issuance.getIssuanceDate()).append("</td>")
                    .append("</tr>");
        }

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 5px 5px 0 0; text-align: center; }
                        .content { background-color: #f9f9f9; padding: 20px; border-radius: 0 0 5px 5px; }
                        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
                        .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #999; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Book Issuance Confirmation</h1>
                        </div>
                        <div class="content">
                            <h2>Hello %s,</h2>
                            <p>Your book issuance has been completed successfully.</p>
                            <h3>Issued Books:</h3>
                            <table>
                                <thead>
                                    <tr style="background-color: #667eea; color: white;">
                                        <th style="padding: 10px; text-align: left;">Title</th>
                                        <th style="padding: 10px; text-align: left;">Author</th>
                                        <th style="padding: 10px; text-align: left;">Date Issued</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    """ + bookList.toString() + """
                                </tbody>
                            </table>
                            <p>Thank you for using our library system!</p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2026 Student Library System. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(name);
    }
}
