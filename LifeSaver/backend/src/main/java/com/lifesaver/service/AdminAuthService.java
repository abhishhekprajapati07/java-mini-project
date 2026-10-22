package com.lifesaver.service;

import com.lifesaver.dto.AdminLoginDto;
import com.lifesaver.dto.AdminUserDto;
import com.lifesaver.model.AdminUser;

/**
 * AdminAuthService Interface
 * Handles administrator verification and account management.
 */
public interface AdminAuthService {

    AdminUserDto login(AdminLoginDto loginDto);

    AdminUserDto findById(int id);

    AdminUser createAdmin(String username, String rawPassword, String email, String fullName, String role);

    boolean hasAdmins();
}
