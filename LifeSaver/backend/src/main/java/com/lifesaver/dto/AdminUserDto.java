package com.lifesaver.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AdminUserDto
 * Safe administrative user representation returned to clients and stored in HttpSession.
 * Security notice: Never leaks password hash or salt.
 */
public class AdminUserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private LocalDateTime createdAt;

    public AdminUserDto() {
    }

    public AdminUserDto(Integer id, String username, String email, String fullName, String role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
