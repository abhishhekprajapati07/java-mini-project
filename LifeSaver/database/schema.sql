-- ============================================================================
-- LifeSaver - Blood Donation and Donor Management System
-- Database Schema (MySQL 8.x / MariaDB 10.x compatible)
-- ============================================================================
-- College Mini Project: Full Stack Java (Spring Boot + React + JDBC + MySQL)
-- ============================================================================

CREATE DATABASE IF NOT EXISTS lifesaver_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE lifesaver_db;

-- ----------------------------------------------------------------------------
-- Table: donors
-- Stores registered blood donors with contact information and availability
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS donors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    contact_number VARCHAR(20) NOT NULL,
    blood_group VARCHAR(5) NOT NULL,
    city VARCHAR(60) NOT NULL,
    area VARCHAR(80) NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_donors_blood_group (blood_group),
    INDEX idx_donors_city (city),
    INDEX idx_donors_available (is_available),
    INDEX idx_donors_search (blood_group, city, is_available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- Table: blood_requests
-- Stores patient/hospital blood requests submitted by public or medical staff
-- Status values: PENDING (unverified), VERIFIED, FULFILLED, REJECTED
-- Urgency values: NORMAL, URGENT, CRITICAL
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS blood_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    requester_name VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    patient_blood_group VARCHAR(5) NOT NULL,
    hospital_name VARCHAR(150) NOT NULL,
    city VARCHAR(60) NOT NULL,
    units_requested INT NOT NULL DEFAULT 1,
    urgency VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes TEXT NULL,
    request_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_requests_blood_group (patient_blood_group),
    INDEX idx_requests_city (city),
    INDEX idx_requests_status (status),
    INDEX idx_requests_date (request_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- Table: admin_users
-- Stores administrative users with PBKDF2 hashed passwords and unique salts
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS admin_users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    password_salt VARCHAR(64) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_admin_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
