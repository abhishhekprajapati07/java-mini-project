package com.lifesaver.controller;

import com.lifesaver.dto.AdminLoginDto;
import com.lifesaver.dto.AdminUserDto;
import com.lifesaver.dto.ApiResponse;
import com.lifesaver.exception.UnauthorizedException;
import com.lifesaver.service.AdminAuthService;
import com.lifesaver.util.AdminAuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AdminAuthController
 * Manages administrative session creation, session invalidation, and session profile inspection.
 * Educational concept: Stateful server-side session authentication with HTTP cookies.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    public AdminAuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    /**
     * POST /api/admin/login
     * Validates credentials against PBKDF2 hash, creates an active HttpSession on success.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AdminUserDto>> login(
            @Valid @RequestBody AdminLoginDto loginDto,
            HttpServletRequest request
    ) {
        AdminUserDto adminDto = adminAuthService.login(loginDto);

        // Invalidate any existing session to prevent session fixation attacks
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        // Create new session and store admin details
        HttpSession newSession = request.getSession(true);
        newSession.setAttribute(AdminAuthInterceptor.SESSION_ADMIN_KEY, adminDto);

        return ResponseEntity.ok(ApiResponse.ok("Login successful. Welcome back, " + adminDto.getFullName() + "!", adminDto));
    }

    /**
     * POST /api/admin/logout
     * Invalidates the active HttpSession.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully."));
    }

    /**
     * GET /api/admin/me
     * Returns details of currently authenticated admin session.
     */
    @GetMapping("/me")
    public ResponseEntity<AdminUserDto> getCurrentAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new UnauthorizedException("No active session found.");
        }
        AdminUserDto admin = (AdminUserDto) session.getAttribute(AdminAuthInterceptor.SESSION_ADMIN_KEY);
        if (admin == null) {
            throw new UnauthorizedException("Session does not contain admin credentials.");
        }
        return ResponseEntity.ok(admin);
    }
}
