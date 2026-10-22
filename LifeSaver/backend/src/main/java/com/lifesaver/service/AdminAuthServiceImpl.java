package com.lifesaver.service;

import com.lifesaver.dao.AdminUserDao;
import com.lifesaver.dto.AdminLoginDto;
import com.lifesaver.dto.AdminUserDto;
import com.lifesaver.exception.DuplicateResourceException;
import com.lifesaver.exception.ResourceNotFoundException;
import com.lifesaver.exception.UnauthorizedException;
import com.lifesaver.model.AdminUser;
import com.lifesaver.util.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * AdminAuthServiceImpl
 * Implements admin authentication using PBKDF2WithHmacSHA256 password verification.
 */
@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthServiceImpl.class);
    private final AdminUserDao adminUserDao;

    public AdminAuthServiceImpl(AdminUserDao adminUserDao) {
        this.adminUserDao = adminUserDao;
    }

    @Override
    public AdminUserDto login(AdminLoginDto loginDto) {
        log.info("Attempting admin login for username: {}", loginDto.getUsername());

        AdminUser user = adminUserDao.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid admin username or password"));

        boolean matches = PasswordHasher.verifyPassword(
                loginDto.getPassword(),
                user.getPasswordHash(),
                user.getPasswordSalt()
        );

        if (!matches) {
            log.warn("Failed password verification for admin: {}", loginDto.getUsername());
            throw new UnauthorizedException("Invalid admin username or password");
        }

        log.info("Admin login successful for username: {}", user.getUsername());
        return toDto(user);
    }

    @Override
    public AdminUserDto findById(int id) {
        return adminUserDao.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user", id));
    }

    @Override
    public AdminUser createAdmin(String username, String rawPassword, String email, String fullName, String role) {
        if (adminUserDao.findByUsername(username).isPresent()) {
            throw new DuplicateResourceException("Admin user '" + username + "' already exists");
        }

        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hashPassword(rawPassword, salt);

        AdminUser newUser = new AdminUser(
                username.trim(),
                email.trim().toLowerCase(),
                hash,
                salt,
                fullName.trim(),
                role != null ? role.trim().toUpperCase() : "ADMIN"
        );

        return adminUserDao.save(newUser);
    }

    @Override
    public boolean hasAdmins() {
        return adminUserDao.count() > 0;
    }

    private AdminUserDto toDto(AdminUser user) {
        return new AdminUserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
