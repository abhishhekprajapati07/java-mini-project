package com.lifesaver.dao;

import com.lifesaver.model.BloodRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * BloodRequestDaoImpl
 * Pure JDBC implementation for blood requests using PreparedStatements.
 */
@Repository
public class BloodRequestDaoImpl implements BloodRequestDao {

    private static final Logger log = LoggerFactory.getLogger(BloodRequestDaoImpl.class);
    private final DataSource dataSource;

    public BloodRequestDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private BloodRequest mapRow(ResultSet rs) throws SQLException {
        BloodRequest req = new BloodRequest();
        req.setId(rs.getInt("id"));
        req.setRequesterName(rs.getString("requester_name"));
        req.setContactNumber(rs.getString("contact_number"));
        req.setPatientBloodGroup(rs.getString("patient_blood_group"));
        req.setHospitalName(rs.getString("hospital_name"));
        req.setCity(rs.getString("city"));
        req.setUnitsRequested(rs.getInt("units_requested"));
        req.setUrgency(rs.getString("urgency"));
        req.setStatus(rs.getString("status"));
        req.setNotes(rs.getString("notes"));

        Date reqDate = rs.getDate("request_date");
        if (reqDate != null) {
            req.setRequestDate(reqDate.toLocalDate());
        }
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            req.setCreatedAt(created.toLocalDateTime());
        }
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            req.setUpdatedAt(updated.toLocalDateTime());
        }
        return req;
    }

    @Override
    public Optional<BloodRequest> findById(int id) {
        String sql = "SELECT * FROM blood_requests WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error finding blood request by id: {}", id, e);
            throw new RuntimeException("Database error finding blood request by id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<BloodRequest> findAll(String status, String city, String bloodGroup, int limit, int offset) {
        StringBuilder sql = new StringBuilder("SELECT * FROM blood_requests WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            sql.append(" AND status = ?");
            params.add(status.trim().toUpperCase());
        }
        if (city != null && !city.trim().isEmpty()) {
            sql.append(" AND LOWER(city) LIKE LOWER(?)");
            params.add("%" + city.trim() + "%");
        }
        if (bloodGroup != null && !bloodGroup.trim().isEmpty() && !bloodGroup.equalsIgnoreCase("ALL")) {
            sql.append(" AND patient_blood_group = ?");
            params.add(bloodGroup.trim().toUpperCase());
        }

        sql.append(" ORDER BY CASE status WHEN 'PENDING' THEN 1 WHEN 'VERIFIED' THEN 2 WHEN 'FULFILLED' THEN 3 ELSE 4 END, " +
                   "CASE urgency WHEN 'CRITICAL' THEN 1 WHEN 'URGENT' THEN 2 ELSE 3 END, id DESC LIMIT ? OFFSET ?");
        params.add(Math.max(1, Math.min(100, limit)));
        params.add(Math.max(0, offset));

        List<BloodRequest> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error finding blood requests", e);
            throw new RuntimeException("Database error finding blood requests", e);
        }
        return list;
    }

    @Override
    public long count(String status, String city, String bloodGroup) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM blood_requests WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            sql.append(" AND status = ?");
            params.add(status.trim().toUpperCase());
        }
        if (city != null && !city.trim().isEmpty()) {
            sql.append(" AND LOWER(city) LIKE LOWER(?)");
            params.add("%" + city.trim() + "%");
        }
        if (bloodGroup != null && !bloodGroup.trim().isEmpty() && !bloodGroup.equalsIgnoreCase("ALL")) {
            sql.append(" AND patient_blood_group = ?");
            params.add(bloodGroup.trim().toUpperCase());
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            log.error("Error counting blood requests", e);
            throw new RuntimeException("Database error counting blood requests", e);
        }
        return 0;
    }

    @Override
    public BloodRequest save(BloodRequest request) {
        String sql = "INSERT INTO blood_requests (requester_name, contact_number, patient_blood_group, " +
                     "hospital_name, city, units_requested, urgency, status, notes, request_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, request.getRequesterName().trim());
            ps.setString(2, request.getContactNumber().trim());
            ps.setString(3, request.getPatientBloodGroup().trim().toUpperCase());
            ps.setString(4, request.getHospitalName().trim());
            ps.setString(5, request.getCity().trim());
            ps.setInt(6, request.getUnitsRequested());
            ps.setString(7, request.getUrgency());
            ps.setString(8, request.getStatus());
            ps.setString(9, request.getNotes() != null ? request.getNotes().trim() : null);
            ps.setDate(10, Date.valueOf(request.getRequestDate()));

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating blood request failed, no rows affected.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    request.setId(keys.getInt(1));
                }
            }
            return request;
        } catch (SQLException e) {
            log.error("Error saving blood request", e);
            throw new RuntimeException("Database error saving blood request", e);
        }
    }

    @Override
    public boolean updateStatus(int id, String status, String notes) {
        String sql = "UPDATE blood_requests SET status = ?, notes = COALESCE(?, notes) WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.trim().toUpperCase());
            ps.setString(2, notes != null && !notes.trim().isEmpty() ? notes.trim() : null);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Error updating blood request status for id: {}", id, e);
            throw new RuntimeException("Database error updating blood request status", e);
        }
    }
}
