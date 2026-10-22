package com.lifesaver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * LifeSaver - Blood Donation and Donor Management System
 * College Mini Project: Full Stack Java (Spring Boot + React + JDBC + MySQL)
 * 
 * Strict Technology Guidelines:
 * - Backend: Java Spring Boot monolithic REST API
 * - Data Access: Pure JDBC with PreparedStatement (No JPA/Hibernate)
 * - Security: Built-in PBKDF2WithHmacSHA256 & HttpSession (No Spring Security)
 * - Architecture: MVC (Controller -> Service -> DAO)
 */
@SpringBootApplication
public class LifeSaverApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifeSaverApplication.class, args);
    }
}
