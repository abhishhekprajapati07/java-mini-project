# LifeSaver — Blood Donation and Donor Management System
**College Mini Project: Full-Stack Java (Spring Boot + React + JDBC + MySQL)**

---

## 1. Project Overview & Educational Goal

**LifeSaver** is a responsive, monolithic web application designed to coordinate voluntary blood donations between donors, patients, and hospital care teams. It allows volunteers to register their blood group and location, visitors to search matching donors with privacy-protected contact information, patients and caregivers to submit emergency blood requirements, and administrators to review, verify, and manage records.

> [!IMPORTANT]
> **Medical & Educational Safety Disclaimer:**
> LifeSaver is an educational mini project built for academic evaluation, **not a certified clinical medical service**.
> A matching ABO/Rh blood group recorded in this database **never guarantees biological compatibility or donation eligibility**.
> Certified hospital blood bank staff must perform independent antibody screening, clinical health evaluations, and laboratory crossmatching prior to any donation or transfusion. All donor records and patient requests included here are strictly fictional test entries.

---

## 2. Technology Stack & Syllabus Alignment

This project strictly adheres to the **Full Stack Java** university syllabus. No outside libraries, third-party frameworks, or non-syllabus technologies are used:

| Layer | Technology | Details & Syllabus Concepts |
| :--- | :--- | :--- |
| **Backend** | **Java Spring Boot 3.3.4** | Monolithic REST API, MVC architecture, `@RestController`, `@Service`, `@Repository`, DTOs, `@RestControllerAdvice` Global Exception Handling. |
| **Java OOP** | **Core Java SE** | Encapsulation (private fields, getters/setters, no Lombok), Inheritance (custom `RuntimeException` hierarchy), Interfaces (`*Dao`, `*Service`), Enums (`BloodGroup`, `RequestStatus`, `UrgencyLevel`). |
| **Database** | **MySQL 8.x / MariaDB** | Relational schema with primary keys, unique constraints, timestamps, and indexes on `blood_group`, `city`, `is_available`, and `status`. |
| **Data Access** | **Pure JDBC (`PreparedStatement`)** | `java.sql.Connection`, `PreparedStatement` with parameterized queries (`?`), `ResultSet` mapping, and manual transactions. **Zero JPA/Hibernate**. |
| **Security** | **Built-in Java Cryptography** | `PBKDF2WithHmacSHA256` password hashing with 16-byte cryptographically secure random salt (`SecureRandom`) and 65,536 rounds. Server-side session management with `HttpSession` (`LIFESAVER_SESSION` cookie) and `AdminAuthInterceptor`. **Zero Spring Security**. |
| **Frontend** | **React 18 (Vite SPA)** | Functional components, Hooks (`useState`, `useEffect`, `useCallback`, `useContext`, `useNavigate`, `useSearchParams`), React Router DOM v6, controlled forms. |
| **Styling** | **Plain Vanilla CSS3** | Modern custom CSS design system, responsive flex/grid layouts, deep-crimson `#c8102e` & medical ruby theme, custom blood-drop artwork, cards, badges, modal dialogs. **Zero Tailwind / Bootstrap**. |
| **Networking** | **Native `fetch` API** | REST API calls with `credentials: 'include'` for secure session cookie exchange. Configurable `VITE_API_BASE_URL`. |

---

## 3. Project Directory Tree

```
LifeSaver/
├── backend/                               # Spring Boot Monolithic Backend
│   ├── mvnw                               # Maven Wrapper (Linux/macOS)
│   ├── mvnw.cmd                           # Maven Wrapper (Windows PowerShell)
│   ├── pom.xml                            # Maven Build Configuration
│   └── src/
│       ├── main/
│       │   ├── java/com/lifesaver/
│       │   │   ├── LifeSaverApplication.java
│       │   │   ├── config/
│       │   │   │   ├── DatabaseInitializer.java   # Auto-seeds schema & default admin
│       │   │   │   └── WebConfig.java             # Interceptors & CORS configuration
│       │   │   ├── controller/
│       │   │   │   ├── AdminAuthController.java   # /api/admin/login, logout, me
│       │   │   │   ├── BloodRequestController.java# /api/requests
│       │   │   │   ├── DonorController.java       # /api/donors
│       │   │   │   ├── HealthController.java      # /api/health (app + DB check)
│       │   │   │   └── StatsController.java       # /api/stats (live DB metrics)
│       │   │   ├── dao/                           # Pure JDBC with PreparedStatement
│       │   │   │   ├── AdminUserDao.java & AdminUserDaoImpl.java
│       │   │   │   ├── BloodRequestDao.java & BloodRequestDaoImpl.java
│       │   │   │   ├── DonorDao.java & DonorDaoImpl.java
│       │   │   │   └── StatsDao.java & StatsDaoImpl.java
│       │   │   ├── dto/                           # Data Transfer Objects
│       │   │   │   ├── AdminLoginDto.java
│       │   │   │   ├── AdminUserDto.java
│       │   │   │   ├── ApiResponse.java
│       │   │   │   ├── BloodRequestCreateDto.java
│       │   │   │   ├── DonorRegistrationDto.java
│       │   │   │   ├── DonorUpdateDto.java
│       │   │   │   ├── StatsDto.java
│       │   │   │   └── StatusUpdateDto.java
│       │   │   ├── exception/                     # Custom Exception Hierarchy
│       │   │   │   ├── DuplicateResourceException.java
│       │   │   │   ├── ErrorResponse.java
│       │   │   │   ├── GlobalExceptionHandler.java
│       │   │   │   ├── InvalidDataException.java
│       │   │   │   ├── ResourceNotFoundException.java
│       │   │   │   └── UnauthorizedException.java
│       │   │   ├── model/                         # Domain Entities & Enums
│       │   │   │   ├── AdminUser.java
│       │   │   │   ├── BloodGroup.java
│       │   │   │   ├── BloodRequest.java
│       │   │   │   ├── Donor.java
│       │   │   │   ├── RequestStatus.java
│       │   │   │   └── UrgencyLevel.java
│       │   │   ├── service/                       # Business Logic Layer
│       │   │   │   ├── AdminAuthService.java & AdminAuthServiceImpl.java
│       │   │   │   ├── BloodRequestService.java & BloodRequestServiceImpl.java
│       │   │   │   ├── DonorService.java & DonorServiceImpl.java
│       │   │   │   └── StatsService.java & StatsServiceImpl.java
│       │   │   └── util/                          # Cryptography & Interceptors
│       │   │       ├── AdminAuthInterceptor.java
│       │   │       ├── PasswordHasher.java        # PBKDF2WithHmacSHA256
│       │   │       └── ValidationUtil.java
│       │   └── resources/
│       │       ├── application.properties         # Database connection & server port
│       │       └── schema.sql                     # Classpath DDL table creation
│       └── test/java/com/lifesaver/               # 54 Automated Tests
│           ├── LifeSaverIntegrationTest.java      # End-to-end REST API & Session test
│           ├── service/                           # Unit tests for services
│           └── util/                              # Unit tests for Hasher & Validation
├── frontend/                              # React 18 Single-Page Application
│   ├── package.json
│   ├── vite.config.js                     # Proxy to backend on port 8080
│   ├── index.html                         # Head metadata, fonts, SVG favicon
│   ├── .env.example                       # VITE_API_BASE_URL template
│   └── src/
│       ├── main.jsx
│       ├── App.jsx                        # Routes & AuthProvider
│       ├── index.css                      # Plain Vanilla CSS design system
│       ├── api/
│       │   └── client.js                  # Native fetch API wrapper
│       ├── context/
│       │   └── AuthContext.jsx            # Admin login/logout context state
│       ├── components/
│       │   ├── Alert.jsx
│       │   ├── DonorCard.jsx              # Masked phone & contact modal
│       │   ├── Footer.jsx
│       │   ├── LoadingSpinner.jsx
│       │   ├── MedicalDisclaimer.jsx
│       │   ├── Modal.jsx
│       │   ├── Navbar.jsx
│       │   └── StatusBadge.jsx
│       └── pages/
│           ├── HomePage.jsx               # Hero, live stats, compatibility guide
│           ├── FindDonorsPage.jsx         # Search filters, pagination, donor cards
│           ├── RegisterDonorPage.jsx      # Controlled donor registration form
│           ├── RequestBloodPage.jsx       # Controlled blood request form
│           ├── AboutPage.jsx              # Eligibility, deferrals, architecture
│           ├── AdminLoginPage.jsx         # Secure PBKDF2 authentication
│           ├── AdminDashboardPage.jsx     # Request status updates & donor management
│           └── NotFoundPage.jsx           # 404 error page
├── database/
│   ├── schema.sql                         # MySQL DDL (donors, requests, admin_users)
│   ├── sample_data.sql                    # 10 fictional donors, 5 requests, 1 admin
│   └── start-portable-db.ps1              # Zero-install portable database runner
├── postman/
│   └── LifeSaver_API_Collection.json      # 20 pre-configured Postman requests
├── .env.example                           # Root environment variable template
└── README.md
```

---

## 4. Prerequisites & Windows Setup

### Installed Software Requirements
1. **Java Development Kit (JDK):**
   - **Java 21 LTS** (Recommended) or **Java 26** (Tested and verified).
   - *Compatibility Note:* Because Java 26 supports `--release 21`, Maven compiles compatible Java 21 bytecode that Spring Boot 3 executes seamlessly.
   - If your university evaluator requires an LTS release, you can download a portable JDK 21 and point `JAVA_HOME` in your current PowerShell window without altering your global Java 26:
     ```powershell
     $env:JAVA_HOME = "C:\path\to\jdk-21"
     $env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
     ```
2. **Node.js & npm:**
   - Node.js 18+ (Node v24 is installed and verified).
3. **Build Tool:**
   - Apache Maven 3.9+ or the included `backend/mvnw.cmd` wrapper.
4. **Database (MySQL):**
   - MySQL 8.x Server, MariaDB 10.x, or XAMPP MySQL.

---

## 5. Database Setup (MySQL)

### Option A: Using Local MySQL or XAMPP
1. Start your local MySQL service (e.g., click **Start** next to MySQL in XAMPP Control Panel, or start the Windows MySQL service).
2. Open MySQL CLI or phpMyAdmin and execute `database/schema.sql`:
   ```powershell
   mysql -u root -p < database\schema.sql
   ```
3. Load the fictional demo dataset:
   ```powershell
   mysql -u root -p lifesaver_db < database\sample_data.sql
   ```

### Option B: Automatic Startup Bootstrapping
When the Spring Boot backend starts, `DatabaseInitializer.java` automatically:
- Creates all tables (`donors`, `blood_requests`, `admin_users`) and indexes if they do not exist.
- Seeds the initial default administrator account (`admin` / `AdminPassword@123`).
- Seeds 10 realistic fictional donors across Delhi, Mumbai, Bengaluru, Pune, Hyderabad, Chennai, and Kolkata.
- Seeds 5 realistic blood requests across different hospitals and statuses.

---

## 6. How to Run the Project (Step-by-Step)

### Step 1: Start the Backend (Spring Boot)
Open Windows PowerShell and navigate to `backend`:
```powershell
cd c:\Users\Abhishek\OneDrive\Documents\LifeSaver\backend

# Run using the Windows Maven wrapper:
.\mvnw.cmd spring-boot:run

# OR run the pre-built standalone executable JAR:
java -jar target\lifesaver-backend-1.0.0.jar
```
*The backend starts at `http://localhost:8080`.*

### Step 2: Start the Frontend (React + Vite)
Open a **second** Windows PowerShell terminal and navigate to `frontend`:
```powershell
cd c:\Users\Abhishek\OneDrive\Documents\LifeSaver\frontend

# Install dependencies (already executed):
npm install

# Start Vite development server:
npm run dev
```
*The frontend opens at `http://localhost:5173`.*

---

## 7. Automated Testing & Verification

The project includes **54 automated unit and integration tests** verifying:
- Successful donor registration with input sanitization.
- Duplicate email conflict rejection (`HTTP 409 Conflict`).
- Validation failures for malformed emails, bad phone numbers, and invalid blood groups (`HTTP 400 Bad Request`).
- Public donor search with privacy masking.
- Blood request creation defaulting to unverified `PENDING` status.
- Unauthorized access rejection for anonymous users on admin endpoints (`HTTP 401 Unauthorized`).
- PBKDF2 password verification with constant-time equality checks.
- Session creation on login, session verification via `/api/admin/me`, and session invalidation on logout.
- Blood request lifecycle updates (`PENDING` -> `VERIFIED` -> `FULFILLED` -> `REJECTED`).
- System and database health check separation.

To run the complete test suite:
```powershell
cd c:\Users\Abhishek\OneDrive\Documents\LifeSaver\backend
.\mvnw.cmd test
```
**Expected Result:**
```
[INFO] Results:
[INFO] Tests run: 54, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 8. Complete REST API Documentation

All responses are returned as structured JSON. All admin endpoints enforce server-side authentication through `AdminAuthInterceptor` checking the `LIFESAVER_SESSION` cookie.

### 1. Health Check
- **Endpoint:** `GET /api/health`
- **Access:** Public
- **Description:** Returns application status and database connectivity independently. Does not fail application status if the database is offline.
- **Sample Response (HTTP 200 OK):**
  ```json
  {
    "status": "UP",
    "application": "LifeSaver - Blood Donation and Donor Management System",
    "version": "1.0.0",
    "timestamp": "2026-10-21T21:32:29",
    "database": {
      "status": "UP",
      "databaseProduct": "MySQL",
      "databaseVersion": "8.0.36"
    }
  }
  ```

### 2. Live Statistics
- **Endpoint:** `GET /api/stats`
- **Access:** Public
- **Description:** Computes real-time counts from database tables.
- **Sample Response (HTTP 200 OK):**
  ```json
  {
    "totalDonors": 10,
    "availableDonors": 9,
    "totalRequests": 5,
    "pendingRequests": 2,
    "verifiedRequests": 2,
    "fulfilledRequests": 1,
    "rejectedRequests": 0,
    "donorsByBloodGroup": { "O+": 2, "A+": 1, "B+": 2, "AB+": 1, "O-": 1, "A-": 1, "B-": 1, "AB-": 1 },
    "requestsByBloodGroup": { "O-": 1, "A+": 1, "B+": 1, "AB+": 1, "O+": 1 }
  }
  ```

### 3. Public Donor Search
- **Endpoint:** `GET /api/donors?bloodGroup={bg}&city={city}&available={bool}&page={page}&size={size}`
- **Access:** Public
- **Privacy Policy:** Masked contact numbers (e.g. `+91 98******01`) for anonymous visitors. Full numbers returned if logged in as admin.
- **Sample Response (HTTP 200 OK):**
  ```json
  {
    "content": [
      {
        "id": 1,
        "fullName": "Aarav Sharma",
        "bloodGroup": "O+",
        "city": "Delhi",
        "area": "Connaught Place",
        "available": true,
        "contactNumber": "+91 98******01",
        "isContactMasked": true
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1,
    "medicalDisclaimer": "Compatibility must be medically verified by certified hospital blood bank staff prior to transfusion."
  }
  ```

### 4. Register Donor
- **Endpoint:** `POST /api/donors`
- **Access:** Public
- **Request Body (JSON):**
  ```json
  {
    "fullName": "Kavita Deshmukh",
    "email": "kavita.deshmukh@demo.mail",
    "contactNumber": "9876543210",
    "bloodGroup": "O+",
    "city": "Pune",
    "area": "Kothrud",
    "available": true
  }
  ```
- **Response (HTTP 201 Created):**
  ```json
  {
    "success": true,
    "message": "Donor registered successfully! Thank you for volunteering to save lives.",
    "data": { "id": 11, "fullName": "Kavita Deshmukh", "email": "kavita.deshmukh@demo.mail", "bloodGroup": "O+" }
  }
  ```
- **Error Responses:**
  - Duplicate email: `HTTP 409 Conflict`
  - Validation error: `HTTP 400 Bad Request`

### 5. Update Donor Record & Availability
- **Update Profile:** `PUT /api/donors/{id}` (Body: `DonorUpdateDto`)
- **Toggle Availability:** `PATCH /api/donors/{id}/availability?available=false`
- **Delete Donor:** `DELETE /api/donors/{id}` (**Admin Protected**)

### 6. Blood Requests
- **Submit Request (Public):** `POST /api/requests`
  ```json
  {
    "requesterName": "Dr. Sandeep Rao",
    "contactNumber": "9876592001",
    "patientBloodGroup": "O-",
    "hospitalName": "City Care Hospital",
    "city": "Delhi",
    "unitsRequested": 2,
    "urgency": "CRITICAL",
    "notes": "Emergency bypass requirement",
    "requestDate": "2026-10-22"
  }
  ```
  *Response:* Returns `HTTP 201 Created` with status `PENDING` (unverified).
- **List Requests (Admin Protected):** `GET /api/requests?status=PENDING&page=1&size=10`
- **Update Request Status (Admin Protected):** `PATCH /api/requests/{id}/status`
  ```json
  {
    "status": "VERIFIED",
    "notes": "Verified with hospital blood bank coordinator"
  }
  ```

### 7. Administrator Authentication
- **Login:** `POST /api/admin/login`
  ```json
  { "username": "admin", "password": "AdminPassword@123" }
  ```
  *Sets HTTP-only cookie `LIFESAVER_SESSION`.*
- **Verify Active Session:** `GET /api/admin/me` (Returns `AdminUserDto` or `HTTP 401 Unauthorized`).
- **Logout:** `POST /api/admin/logout` (Invalidates session).

---

## 9. Administrator Credentials & Demo Access

For local testing and viva demonstration, LifeSaver seeds a pre-configured administrator account:
- **Admin Username:** `admin`
- **Password:** `AdminPassword@123`
- **Hashing Algorithm:** `PBKDF2WithHmacSHA256`
- **Salt:** 16-byte cryptographically secure random salt
- **Iterations:** 65,536 rounds

To customize or create credentials, set environment variables prior to launch:
```powershell
$env:DEFAULT_ADMIN_USER = "customadmin"
$env:DEFAULT_ADMIN_PASS = "MyStrongPass#2026"
```

---

## 10. Controlled Contact & Demo Privacy Policy

To prevent mass scraping and automated telemarketing of registered voluntary donors:
1. All public donor searches via `/api/donors` return **masked contact numbers** (e.g., `+91 98******01`).
2. When a visitor clicks **"Request Contact"**, an accessible modal displays:
   - Mandatory medical verification notice.
   - Purpose specification.
   - Controlled demo reveal option with a clear notice that all records are fictional.
   - Direct shortcut button to file an official blood request for that blood group and city.
3. Authenticated administrators have direct access to unmasked contact numbers on the Admin Dashboard for official emergency coordination.

---

## 11. Screenshots to Capture for College Project Report

When preparing your mini project documentation and presentation slides, capture the following screens:
1. **Home Page (`/`):** Hero section with LifeSaver emblem, calls to action, and the Live Database Statistics strip.
2. **Compatibility Matrix (`/`):** The ABO/Rh compatibility table with medical crossmatching notes.
3. **Find Donors (`/donors`):** Search filter toolbar, filtered cards displaying blood group badges and masked contact details.
4. **Donor Contact Modal (`/donors`):** Modal dialog showing the privacy protection policy and demo contact reveal.
5. **Become a Donor (`/register-donor`):** Controlled registration form with real-time validation and privacy assurance box.
6. **Request Blood (`/request-blood`):** Emergency blood request form with urgency selector and unverified warning.
7. **About & Guidelines (`/about`):** Donor eligibility criteria, temporary deferral rules, and 4-stage donation timeline.
8. **Admin Login (`/admin/login`):** Login form with demo credentials notice.
9. **Admin Dashboard — Blood Requests (`/admin/dashboard`):** Real-time metric counters, status update modal changing a request from `PENDING` to `VERIFIED`.
10. **Admin Dashboard — Donor Registry (`/admin/dashboard`):** Unmasked donor table, availability toggle button, and delete confirmation modal.
11. **Not Found Page (`/404`):** Themed 404 error page.

---

## 12. Viva Voce Explanations (Full-Stack Java)

### Q1: Why did you use `PreparedStatement` instead of `Statement`?
**Answer:**
`PreparedStatement` precompiles SQL statements on the database server and parameterizes all inputs using placeholders (`?`). This provides two critical benefits:
1. **SQL Injection Prevention:** User inputs are treated strictly as literal data, never executable SQL commands, completely neutralizing SQL injection attacks.
2. **Performance Optimization:** The database compiles the query execution plan once and can reuse it efficiently for subsequent executions with different parameters.

### Q2: Why is the application separated into Controller, Service, and DAO layers?
**Answer:**
This follows the **Separation of Concerns (SoC)** principle in MVC:
- **Controller Layer:** Handles HTTP requests, extracts parameters, validates request formats, and returns HTTP status codes (`200 OK`, `201 Created`, `400 Bad Request`).
- **Service Layer:** Encapsulates business logic, domain rules (e.g., verifying unique emails, blood group validation, status lifecycle transitions), and transaction management.
- **DAO Layer:** Encapsulates raw JDBC data persistence operations (`Connection`, `PreparedStatement`, `ResultSet`), isolating database-specific SQL from business logic.

### Q3: How is password security implemented without Spring Security?
**Answer:**
We used Java's built-in cryptographic security library (`javax.crypto.SecretKeyFactory`):
1. **Cryptographic Salt:** A unique 16-byte random salt is generated for each user via `SecureRandom` and stored in the database. This renders precomputed Rainbow Table attacks useless.
2. **Key Stretching:** We use `PBKDF2WithHmacSHA256` with 65,536 iterations to computationally slow down dictionary and brute-force attacks.
3. **Constant-Time Verification:** Verification uses `MessageDigest.isEqual` to compare byte arrays in constant time, preventing side-channel timing attacks.

### Q4: How does session management work between React and Spring Boot?
**Answer:**
1. Upon successful login (`POST /api/admin/login`), Spring Boot creates an `HttpSession` and stores an `AdminUserDto` in `session.setAttribute("ADMIN_USER", dto)`.
2. The server responds with a `Set-Cookie: LIFESAVER_SESSION=...; HttpOnly; SameSite=Lax` header.
3. React makes all subsequent REST requests with `credentials: 'include'`.
4. Spring MVC's `AdminAuthInterceptor` intercepts protected endpoints and verifies that `request.getSession(false)` contains an authenticated admin attribute. If missing, it halts execution and throws `UnauthorizedException` (`HTTP 401`).

### Q5: What React hooks are utilized in this project?
**Answer:**
- `useState`: Manages component-level state (form inputs, lists, modals, filters).
- `useEffect`: Triggers side effects such as initial API data fetching and keyboard listeners.
- `useCallback`: Memoizes fetch functions to prevent unnecessary re-renders.
- `useContext`: Provides global access to authentication state via `AuthContext`.
- `useNavigate`: Programmatically redirects users upon login or form completion.
- `useSearchParams`: Synchronizes search filters with URL query parameters for bookmarkable searches.

---

## 13. Deployment Guide & Checklist

### Recommended Architecture
- **Frontend:** Static React build deployed to Vercel, Netlify, or Render Static Site.
- **Backend:** Spring Boot executable JAR deployed to Render, Railway, or Fly.io (Docker/Java runtime).
- **Database:** Managed MySQL instance (Aiven MySQL Free Tier, Clever Cloud MySQL, or Railway MySQL).

### Deployment Environment Variables
1. **Backend Variables:**
   - `DB_URL`: `jdbc:mysql://<cloud-host>:3306/<db-name>?useSSL=true&allowPublicKeyRetrieval=true`
   - `DB_USERNAME`: Cloud database username
   - `DB_PASSWORD`: Cloud database password
   - `CORS_ALLOWED_ORIGINS`: Deployed frontend origin (e.g. `https://lifesaver-app.vercel.app`)
   - `SERVER_PORT`: Assigned by host (e.g., `8080`)
2. **Frontend Variables:**
   - `VITE_API_BASE_URL`: Deployed backend API URL (e.g. `https://lifesaver-backend.onrender.com/api`)

### Production Build Commands
- **Backend:** `cd backend && mvn clean package -DskipTests` -> outputs `target/lifesaver-backend-1.0.0.jar`
- **Frontend:** `cd frontend && npm run build` -> outputs optimized static files to `dist/`
