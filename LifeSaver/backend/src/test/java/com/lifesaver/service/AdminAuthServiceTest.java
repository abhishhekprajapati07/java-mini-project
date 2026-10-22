package com.lifesaver.service;

import com.lifesaver.dao.AdminUserDao;
import com.lifesaver.dto.AdminLoginDto;
import com.lifesaver.dto.AdminUserDto;
import com.lifesaver.exception.UnauthorizedException;
import com.lifesaver.model.AdminUser;
import com.lifesaver.util.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminAuthServiceTest {

    @Mock
    private AdminUserDao adminUserDao;

    private AdminAuthService adminAuthService;

    @BeforeEach
    void setUp() {
        adminAuthService = new AdminAuthServiceImpl(adminUserDao);
    }

    @Test
    @DisplayName("Should successfully login admin with correct credentials")
    void testAdminLoginSuccess() {
        String username = "admin";
        String password = "SecretAdminPassword#123";
        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hashPassword(password, salt);

        AdminUser user = new AdminUser(username, "admin@lifesaver.demo", hash, salt, "Main Admin", "ADMIN");
        user.setId(1);

        when(adminUserDao.findByUsername(username)).thenReturn(Optional.of(user));

        AdminLoginDto loginDto = new AdminLoginDto(username, password);
        AdminUserDto loggedIn = adminAuthService.login(loginDto);

        assertNotNull(loggedIn);
        assertEquals(1, loggedIn.getId());
        assertEquals("admin", loggedIn.getUsername());
        assertEquals("Main Admin", loggedIn.getFullName());
    }

    @Test
    @DisplayName("Should reject admin login with invalid password")
    void testAdminLoginInvalidPassword() {
        String username = "admin";
        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hashPassword("CorrectPass#123", salt);

        AdminUser user = new AdminUser(username, "admin@lifesaver.demo", hash, salt, "Main Admin", "ADMIN");

        when(adminUserDao.findByUsername(username)).thenReturn(Optional.of(user));

        AdminLoginDto loginDto = new AdminLoginDto(username, "WrongPass#123");
        assertThrows(UnauthorizedException.class, () -> adminAuthService.login(loginDto));
    }

    @Test
    @DisplayName("Should reject admin login with non-existent username")
    void testAdminLoginUnknownUser() {
        when(adminUserDao.findByUsername("unknown_user")).thenReturn(Optional.empty());

        AdminLoginDto loginDto = new AdminLoginDto("unknown_user", "AnyPassword123");
        assertThrows(UnauthorizedException.class, () -> adminAuthService.login(loginDto));
    }
}
