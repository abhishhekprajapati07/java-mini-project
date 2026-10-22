package com.lifesaver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * HealthController
 * Provides /api/health endpoint.
 * Requirement: Does not require a database query merely to establish that the application is running;
 * clearly distinguishes application health from database connectivity health.
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "LifeSaver - Blood Donation and Donor Management System");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now());

        // Check database connectivity independently
        Map<String, Object> dbHealth = new HashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            boolean valid = conn.isValid(2);
            dbHealth.put("status", valid ? "UP" : "DEGRADED");
            dbHealth.put("databaseProduct", conn.getMetaData().getDatabaseProductName());
            dbHealth.put("databaseVersion", conn.getMetaData().getDatabaseProductVersion());
        } catch (Exception e) {
            dbHealth.put("status", "DOWN");
            dbHealth.put("error", e.getMessage());
            dbHealth.put("hint", "Ensure MySQL server is running on the configured port (default 3306)");
        }
        response.put("database", dbHealth);

        return ResponseEntity.ok(response);
    }
}
