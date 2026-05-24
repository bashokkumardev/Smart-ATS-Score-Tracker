package com.ashok.jobtracker.dto.auth;

public record AuthResponse(String token, String type, String userId, String email, String name, String role) {

    public static AuthResponse of(String token, String userId, String email, String name, String role) {
        return new AuthResponse(token, "Bearer", userId, email, name, role);
    }
}
