package com.lifesaver.dao;

import com.lifesaver.model.AdminUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

/**
 * AdminUserDaoImpl
 * JDBC data access for admin credentials with PreparedStatements.
 */
@Repository
public class AdminUserDaoImpl implements AdminUserDao {

    private static final Logger log = LoggerFactory.getLogger(AdminUserDaoImpl.class);
    private final DataSource dataSource;

    public AdminUserDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private AdminUser mapRow(ResultSet rs) throws SQLException {
        AdminUser user = new AdminUser();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setPasswordSalt(rs.getString("password_salt"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));

        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            user.setCreatedAt(created.toLocalDateTime());
        }
        return user;
    }

    @Override
    public Optional<AdminUser> findByUsername(String username) {
        String sql = "SELECT * FROM admin_users WHERE LOWER(username) = LOWER(?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error finding admin by username: {}", username, e);
            throw new RuntimeException("Database error finding admin user", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<AdminUser> findById(int id) {
        String sql = "SELECT * FROM admin_users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error finding admin by id: {}", id, e);
            throw new RuntimeException("Database error finding admin user by id", e);
        }
        return Optional.empty();
    }

    @Override
    public AdminUser save(AdminUser adminUser) {
        String sql = "INSERT INTO admin_users (username, email, password_hash, password_salt, full_name, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, adminUser.getUsername().trim());
            ps.setString(2, adminUser.getEmail().trim().toLowerCase());
            ps.setString(3, adminUser.getPasswordHash());
            ps.setString(4, adminUser.getPasswordSalt());
            ps.setString(5, adminUser.getFullName().trim());
            ps.setString(6, adminUser.getRole() != null ? adminUser.getRole() : "ADMIN");

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    adminUser.setId(keys.getInt(1));
                }
            }
            return adminUser;
        } catch (SQLException e) {
            log.error("Error saving admin user", e);
            throw new RuntimeException("Database error saving admin user", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM admin_users";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            log.error("Error counting admin users", e);
            throw new RuntimeException("Database error counting admin users", e);
        }
        return 0;
    }
}
