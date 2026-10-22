package com.lifesaver.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * AdminLoginDto
 * Credentials payload for administrative session login.
 */
public class AdminLoginDto {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    public AdminLoginDto() {
    }

    public AdminLoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
