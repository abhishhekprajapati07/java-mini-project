package com.lifesaver.dao;

import com.lifesaver.model.Donor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DonorDaoImpl
 * Implements pure JDBC data access using PreparedStatements and Connection from DataSource.
 * 
 * Educational concepts:
 * 1. PreparedStatement prevents SQL Injection attacks by separating SQL code from user parameters.
 * 2. Try-with-resources guarantees automatic closure of Connection, PreparedStatement, and ResultSet.
 * 3. Parameterized dynamic query construction.
 */
@Repository
public class DonorDaoImpl implements DonorDao {

    private static final Logger log = LoggerFactory.getLogger(DonorDaoImpl.class);
    private final DataSource dataSource;

    public DonorDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Donor mapRow(ResultSet rs) throws SQLException {
        Donor donor = new Donor();
        donor.setId(rs.getInt("id"));
        donor.setFullName(rs.getString("full_name"));
        donor.setEmail(rs.getString("email"));
        donor.setContactNumber(rs.getString("contact_number"));
        donor.setBloodGroup(rs.getString("blood_group"));
        donor.setCity(rs.getString("city"));
        donor.setArea(rs.getString("area"));
        donor.setAvailable(rs.getBoolean("is_available"));

        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            donor.setCreatedAt(created.toLocalDateTime());
        }
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            donor.setUpdatedAt(updated.toLocalDateTime());
        }
        return donor;
    }

    @Override
    public Optional<Donor> findById(int id) {
        String sql = "SELECT * FROM donors WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error finding donor by id: {}", id, e);
            throw new RuntimeException("Database error finding donor by id", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Donor> findByEmail(String email) {
        String sql = "SELECT * FROM donors WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error finding donor by email: {}", email, e);
            throw new RuntimeException("Database error finding donor by email", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Donor> search(String bloodGroup, String city, Boolean available, int limit, int offset) {
        StringBuilder sql = new StringBuilder("SELECT * FROM donors WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (bloodGroup != null && !bloodGroup.trim().isEmpty() && !bloodGroup.equalsIgnoreCase("ALL")) {
            sql.append(" AND blood_group = ?");
            params.add(bloodGroup.trim().toUpperCase());
        }
        if (city != null && !city.trim().isEmpty()) {
            sql.append(" AND LOWER(city) LIKE LOWER(?)");
            params.add("%" + city.trim() + "%");
        }
        if (available != null) {
            sql.append(" AND is_available = ?");
            params.add(available);
        }

        sql.append(" ORDER BY is_available DESC, id DESC LIMIT ? OFFSET ?");
        params.add(Math.max(1, Math.min(100, limit)));
        params.add(Math.max(0, offset));

        List<Donor> results = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error executing donor search query", e);
            throw new RuntimeException("Database error searching donors", e);
        }
        return results;
    }

    @Override
    public long countSearch(String bloodGroup, String city, Boolean available) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM donors WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (bloodGroup != null && !bloodGroup.trim().isEmpty() && !bloodGroup.equalsIgnoreCase("ALL")) {
            sql.append(" AND blood_group = ?");
            params.add(bloodGroup.trim().toUpperCase());
        }
        if (city != null && !city.trim().isEmpty()) {
            sql.append(" AND LOWER(city) LIKE LOWER(?)");
            params.add("%" + city.trim() + "%");
        }
        if (available != null) {
            sql.append(" AND is_available = ?");
            params.add(available);
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
            log.error("Error counting donors in search", e);
            throw new RuntimeException("Database error counting donors", e);
        }
        return 0;
    }

    @Override
    public Donor save(Donor donor) {
        String sql = "INSERT INTO donors (full_name, email, contact_number, blood_group, city, area, is_available) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, donor.getFullName().trim());
            ps.setString(2, donor.getEmail().trim().toLowerCase());
            ps.setString(3, donor.getContactNumber().trim());
            ps.setString(4, donor.getBloodGroup().trim().toUpperCase());
            ps.setString(5, donor.getCity().trim());
            ps.setString(6, donor.getArea() != null ? donor.getArea().trim() : null);
            ps.setBoolean(7, donor.isAvailable());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating donor failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    donor.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating donor failed, no ID obtained.");
                }
            }
            return donor;
        } catch (SQLException e) {
            log.error("Error saving donor: {}", donor.getEmail(), e);
            throw new RuntimeException("Database error saving donor", e);
        }
    }

    @Override
    public boolean update(Donor donor) {
        String sql = "UPDATE donors SET full_name = ?, contact_number = ?, blood_group = ?, " +
                     "city = ?, area = ?, is_available = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, donor.getFullName().trim());
            ps.setString(2, donor.getContactNumber().trim());
            ps.setString(3, donor.getBloodGroup().trim().toUpperCase());
            ps.setString(4, donor.getCity().trim());
            ps.setString(5, donor.getArea() != null ? donor.getArea().trim() : null);
            ps.setBoolean(6, donor.isAvailable());
            ps.setInt(7, donor.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Error updating donor id: {}", donor.getId(), e);
            throw new RuntimeException("Database error updating donor", e);
        }
    }

    @Override
    public boolean updateAvailability(int id, boolean available) {
        String sql = "UPDATE donors SET is_available = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, available);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Error updating availability for donor id: {}", id, e);
            throw new RuntimeException("Database error updating donor availability", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM donors WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Error deleting donor id: {}", id, e);
            throw new RuntimeException("Database error deleting donor", e);
        }
    }
}
