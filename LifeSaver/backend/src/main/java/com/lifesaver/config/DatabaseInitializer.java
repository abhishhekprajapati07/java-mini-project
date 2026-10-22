package com.lifesaver.config;

import com.lifesaver.dao.AdminUserDao;
import com.lifesaver.dao.BloodRequestDao;
import com.lifesaver.dao.DonorDao;
import com.lifesaver.model.BloodRequest;
import com.lifesaver.model.Donor;
import com.lifesaver.model.RequestStatus;
import com.lifesaver.model.UrgencyLevel;
import com.lifesaver.service.AdminAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.stream.Collectors;

/**
 * DatabaseInitializer
 * Executes on application startup:
 * 1. Initializes database schema (creates tables if not existing) using schema.sql.
 * 2. Seeds initial administrator account if no admin is present.
 * 3. Seeds sample fictional donor and request records if tables are empty.
 * 
 * Educational note: Demonstrates ApplicationRunner lifecycle hook in Spring Boot.
 */
@Component
public class DatabaseInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final DataSource dataSource;
    private final AdminAuthService adminAuthService;
    private final AdminUserDao adminUserDao;
    private final DonorDao donorDao;
    private final BloodRequestDao bloodRequestDao;

    @Value("${app.admin.default-username:admin}")
    private String defaultAdminUsername;

    @Value("${app.admin.default-password:AdminPassword@123}")
    private String defaultAdminPassword;

    @Value("${app.admin.default-email:admin@lifesaver.demo}")
    private String defaultAdminEmail;

    @Value("${app.admin.default-fullname:System Administrator}")
    private String defaultAdminFullName;

    public DatabaseInitializer(DataSource dataSource,
                               AdminAuthService adminAuthService,
                               AdminUserDao adminUserDao,
                               DonorDao donorDao,
                               BloodRequestDao bloodRequestDao) {
        this.dataSource = dataSource;
        this.adminAuthService = adminAuthService;
        this.adminUserDao = adminUserDao;
        this.donorDao = donorDao;
        this.bloodRequestDao = bloodRequestDao;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Checking database schema and initial data setup...");
        try (Connection conn = dataSource.getConnection()) {
            executeSchemaScript(conn);
            seedDefaultAdmin();
            seedSampleDonors();
            seedSampleRequests();
            log.info("Database schema and bootstrap records verified successfully.");
        } catch (Exception e) {
            log.warn("Notice: Database initialization could not be completed on startup (Database might be offline or starting up). " +
                     "The application will start, but database operations require MySQL to be active. Reason: {}", e.getMessage());
        }
    }

    private void executeSchemaScript(Connection conn) {
        try {
            ClassPathResource resource = new ClassPathResource("schema.sql");
            if (!resource.exists()) {
                log.warn("schema.sql not found on classpath, skipping script execution.");
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String fullSql = reader.lines()
                        .map(String::trim)
                        .filter(line -> !line.startsWith("--"))
                        .collect(Collectors.joining("\n"));
                String[] statements = fullSql.split(";");
                try (Statement stmt = conn.createStatement()) {
                    for (String sql : statements) {
                        String trimmed = sql.trim();
                        if (!trimmed.isEmpty()) {
                            stmt.execute(trimmed);
                        }
                    }
                }
            }
            log.info("Executed schema.sql successfully.");
        } catch (Exception e) {
            log.error("Error executing schema script: {}", e.getMessage());
        }
    }

    private void seedDefaultAdmin() {
        try {
            if (adminUserDao.count() == 0) {
                log.info("No admin accounts detected. Bootstrapping default administrator: '{}'", defaultAdminUsername);
                adminAuthService.createAdmin(
                        defaultAdminUsername,
                        defaultAdminPassword,
                        defaultAdminEmail,
                        defaultAdminFullName,
                        "ADMIN"
                );
                log.info("Default administrator created with username: '{}' (Password configured in application.properties)", defaultAdminUsername);
            }
        } catch (Exception e) {
            log.warn("Could not seed default admin: {}", e.getMessage());
        }
    }

    private void seedSampleDonors() {
        try {
            if (donorDao.countSearch(null, null, null) == 0) {
                log.info("Seeding initial fictional donor records for demonstration...");
                donorDao.save(new Donor("Aarav Sharma", "aarav.sharma@demo.mail", "+91 98765 01001", "O+", "Delhi", "Connaught Place", true));
                donorDao.save(new Donor("Priya Patel", "priya.patel@demo.mail", "+91 98765 01002", "A+", "Mumbai", "Andheri West", true));
                donorDao.save(new Donor("Rohan Iyer", "rohan.iyer@demo.mail", "+91 98765 01003", "B+", "Bengaluru", "Indiranagar", true));
                donorDao.save(new Donor("Ananya Gupta", "ananya.gupta@demo.mail", "+91 98765 01004", "AB+", "Pune", "Kothrud", true));
                donorDao.save(new Donor("Karthik Reddy", "karthik.reddy@demo.mail", "+91 98765 01005", "O-", "Hyderabad", "Banjara Hills", true));
                donorDao.save(new Donor("Sneha Nair", "sneha.nair@demo.mail", "+91 98765 01006", "A-", "Chennai", "Adyar", false));
                donorDao.save(new Donor("Vikram Das", "vikram.das@demo.mail", "+91 98765 01007", "B-", "Kolkata", "Salt Lake", true));
                donorDao.save(new Donor("Deepa Mehta", "deepa.mehta@demo.mail", "+91 98765 01008", "AB-", "Ahmedabad", "Navrangpura", true));
                donorDao.save(new Donor("Rahul Verma", "rahul.verma@demo.mail", "+91 98765 01009", "O+", "Jaipur", "Malviya Nagar", true));
                donorDao.save(new Donor("Pooja Kulkarni", "pooja.kulkarni@demo.mail", "+91 98765 01010", "B+", "Mumbai", "Bandra East", true));
                log.info("Seeded 10 fictional donors successfully.");
            }
        } catch (Exception e) {
            log.warn("Could not seed sample donors: {}", e.getMessage());
        }
    }

    private void seedSampleRequests() {
        try {
            if (bloodRequestDao.count(null, null, null) == 0) {
                log.info("Seeding initial fictional blood requests for demonstration...");
                bloodRequestDao.save(new BloodRequest(
                        "Dr. Sandeep Rao", "+91 98765 92001", "O-",
                        "City Care Super Speciality Hospital", "Delhi", 2,
                        UrgencyLevel.CRITICAL.name(), RequestStatus.VERIFIED.name(),
                        "Emergency surgery requirement in ICU Ward 4", LocalDate.now().minusDays(1)
                ));
                bloodRequestDao.save(new BloodRequest(
                        "Meera Joshi", "+91 98765 92002", "A+",
                        "Apex General Hospital", "Mumbai", 1,
                        UrgencyLevel.NORMAL.name(), RequestStatus.PENDING.name(),
                        "Scheduled thalassemia transfusion support", LocalDate.now()
                ));
                bloodRequestDao.save(new BloodRequest(
                        "Ramesh Kumar", "+91 98765 92003", "B+",
                        "Fortis Life Clinic", "Bengaluru", 3,
                        UrgencyLevel.URGENT.name(), RequestStatus.FULFILLED.name(),
                        "Trauma care unit patient, units received from community donors", LocalDate.now().minusDays(3)
                ));
                bloodRequestDao.save(new BloodRequest(
                        "Sunita Deshmukh", "+91 98765 92004", "AB+",
                        "Ruby Hall Medical Center", "Pune", 1,
                        UrgencyLevel.NORMAL.name(), RequestStatus.PENDING.name(),
                        "Dialysis support requirement", LocalDate.now()
                ));
                bloodRequestDao.save(new BloodRequest(
                        "Dr. Farooq Ali", "+91 98765 92005", "O+",
                        "Care Hospitals", "Hyderabad", 2,
                        UrgencyLevel.URGENT.name(), RequestStatus.VERIFIED.name(),
                        "Cardiac bypass surgical reserve", LocalDate.now().minusDays(2)
                ));
                log.info("Seeded 5 fictional blood requests successfully.");
            }
        } catch (Exception e) {
            log.warn("Could not seed sample blood requests: {}", e.getMessage());
        }
    }
}
