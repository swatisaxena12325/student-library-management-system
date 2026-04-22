package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Student Library Access Management System - Main Application Class
 * 
 * This Spring Boot application provides a comprehensive library management system for students and administrators.
 * 
 * Features:
 * - User registration with email verification
 * - Book browsing and issuance
 * - Library access tracking (entry/exit)
 * - Admin dashboard with real-time statistics
 * - Email notifications for confirmations
 * 
 * Run: mvn spring-boot:run
 * Or: java -jar target/demo-1.0.0.jar
 * 
 * Access: http://localhost:8080
 * 
 * Default Admin Credentials:
 * Email: admin@library.com
 * Password: Admin@123
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.demo"})
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		System.out.println("\n========================================");
		System.out.println("Student Library Management System");
		System.out.println("========================================");
		System.out.println("Application started successfully!");
		System.out.println("Access the application at: http://localhost:8080");
		System.out.println("========================================\n");
	}

}

