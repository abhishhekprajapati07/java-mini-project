# Java Mini Project: LifeSaver Blood Donation & Donor Management System

Welcome to the **Java Mini Project** repository. This repository contains the complete full-stack implementation of **LifeSaver** — a modern, responsive, and secure Blood Donation and Emergency Donor Management System.

---

## 📁 Repository Structure

```
java-mini-project/
├── LifeSaver/                       # Main Project Directory
│   ├── backend/                     # Spring Boot 3.2 (Java 17+), REST API, JDBC / MySQL
│   ├── frontend/                    # React 18, Vite, Custom Design System
│   ├── database/                    # SQL Schema & Seed Data (MySQL / MariaDB / H2)
│   ├── postman/                     # Postman API Collection & Environment
│   ├── .env.example                 # Example Environment Configuration
│   └── README.md                    # In-depth Architecture & Documentation
├── .gitignore                       # Git Ignore Configuration
└── README.md                        # Root Project Overview
```

---

## 🚀 Quick Start Guide

### 1. Database Setup
Execute the database scripts in `LifeSaver/database/`:
- [`LifeSaver/database/schema.sql`](LifeSaver/database/schema.sql)
- [`LifeSaver/database/sample_data.sql`](LifeSaver/database/sample_data.sql)

*(Windows users without MySQL installed can run the portable zero-install script [`LifeSaver/database/start-portable-db.ps1`](LifeSaver/database/start-portable-db.ps1))*

### 2. Run Backend (Spring Boot)
```bash
cd LifeSaver/backend
./mvnw clean spring-boot:run
```
Backend API runs on: `http://localhost:8080`  
Health check endpoint: `http://localhost:8080/api/health`

### 3. Run Frontend (React + Vite)
```bash
cd LifeSaver/frontend
npm install
npm run dev
```
Web Application runs on: `http://localhost:5173`

---

## ✨ Features
- **Donor Directory & Search**: Real-time filtering by blood group and city with privacy masking.
- **Donor Registration & Self-Service**: Registration with duplicate prevention and eligibility calculation.
- **Emergency Blood Requests**: Priority queue (LOW / MEDIUM / HIGH / CRITICAL) with live status updates.
- **Admin Dashboard**: Secure credential hashing, session management, donor status toggles, and request fulfillment.
- **Comprehensive Test Suite**: Automated unit and full-stack integration tests.

For full project documentation, API contracts, and architecture diagrams, check out the [LifeSaver Documentation](LifeSaver/README.md).