package com.lifesaver.util;

import com.lifesaver.dto.AdminUserDto;
import com.lifesaver.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * AdminAuthInterceptor
 * Server-side security gate for protecting administrative endpoints.
 * 
 * Educational concept: Spring MVC HandlerInterceptor pattern for cross-cutting security concerns.
 * Protects endpoints on the server without relying solely on client-side React route guards.
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    public static final String SESSION_ADMIN_KEY = "ADMIN_USER";
    private static final Logger log = LoggerFactory.getLogger(AdminAuthInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Allow CORS pre-flight HTTP OPTIONS requests unconditionally
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Publicly allowed admin endpoint: login
        if ((path.endsWith("/api/admin/login") || path.endsWith("/api/admin/login/")) && "POST".equalsIgnoreCase(method)) {
            return true;
        }

        // Publicly allowed blood request submission: POST /api/requests
        if ((path.equals("/api/requests") || path.equals("/api/requests/")) && "POST".equalsIgnoreCase(method)) {
            return true;
        }

        // Publicly allowed donor operations: GET, POST, PUT, PATCH
        // Only DELETE /api/donors/* requires admin privileges
        if (path.startsWith("/api/donors") && !"DELETE".equalsIgnoreCase(method)) {
            return true;
        }

        // For all protected endpoints (admin routes, request listing, request status update, donor deletion),
        // check for an active HTTP session with an authenticated AdminUserDto
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.warn("Blocked unauthenticated access to {} [{}]: No active session", method, path);
            throw new UnauthorizedException("Administrative session expired or missing. Please login.");
        }

        AdminUserDto admin = (AdminUserDto) session.getAttribute(SESSION_ADMIN_KEY);
        if (admin == null) {
            log.warn("Blocked unauthorized access to {} [{}]: Session lacks admin credentials", method, path);
            throw new UnauthorizedException("Administrative privileges required for this action.");
        }

        return true;
    }
}
