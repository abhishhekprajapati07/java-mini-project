package com.lifesaver.dao;

import com.lifesaver.dto.StatsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * StatsDaoImpl
 * Computes aggregated statistics directly from the database using SQL COUNT and GROUP BY queries.
 * Educational concept: Aggregations in relational databases via JDBC.
 */
@Repository
public class StatsDaoImpl implements StatsDao {

    private static final Logger log = LoggerFactory.getLogger(StatsDaoImpl.class);
    private final DataSource dataSource;

    public StatsDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public StatsDto getStats() {
        StatsDto stats = new StatsDto();

        try (Connection conn = dataSource.getConnection()) {
            // 1. Total and available donors
            String donorSql = "SELECT COUNT(*) as total, SUM(CASE WHEN is_available = 1 THEN 1 ELSE 0 END) as available FROM donors";
            try (PreparedStatement ps = conn.prepareStatement(donorSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalDonors(rs.getLong("total"));
                    stats.setAvailableDonors(rs.getLong("available"));
                }
            }

            // 2. Request status counts
            String reqSql = "SELECT COUNT(*) as total, " +
                            "SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) as pending, " +
                            "SUM(CASE WHEN status = 'VERIFIED' THEN 1 ELSE 0 END) as verified, " +
                            "SUM(CASE WHEN status = 'FULFILLED' THEN 1 ELSE 0 END) as fulfilled, " +
                            "SUM(CASE WHEN status = 'REJECTED' THEN 1 ELSE 0 END) as rejected " +
                            "FROM blood_requests";
            try (PreparedStatement ps = conn.prepareStatement(reqSql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalRequests(rs.getLong("total"));
                    stats.setPendingRequests(rs.getLong("pending"));
                    stats.setVerifiedRequests(rs.getLong("verified"));
                    stats.setFulfilledRequests(rs.getLong("fulfilled"));
                    stats.setRejectedRequests(rs.getLong("rejected"));
                }
            }

            // 3. Donors grouped by blood group
            Map<String, Integer> donorGroups = new HashMap<>();
            String bgDonorSql = "SELECT blood_group, COUNT(*) as cnt FROM donors GROUP BY blood_group";
            try (PreparedStatement ps = conn.prepareStatement(bgDonorSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    donorGroups.put(rs.getString("blood_group"), rs.getInt("cnt"));
                }
            }
            stats.setDonorsByBloodGroup(donorGroups);

            // 4. Requests grouped by patient blood group
            Map<String, Integer> reqGroups = new HashMap<>();
            String bgReqSql = "SELECT patient_blood_group, COUNT(*) as cnt FROM blood_requests GROUP BY patient_blood_group";
            try (PreparedStatement ps = conn.prepareStatement(bgReqSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reqGroups.put(rs.getString("patient_blood_group"), rs.getInt("cnt"));
                }
            }
            stats.setRequestsByBloodGroup(reqGroups);

        } catch (SQLException e) {
            log.error("Error computing database stats", e);
            throw new RuntimeException("Database error computing stats", e);
        }

        return stats;
    }
}
