package com.ashok.jobtracker.dto.auth;

import com.ashok.jobtracker.entity.User;

public record RegisterResponse(String registerId, String name, String email, String role) {

    public static RegisterResponse from(User user) {
        return new RegisterResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
