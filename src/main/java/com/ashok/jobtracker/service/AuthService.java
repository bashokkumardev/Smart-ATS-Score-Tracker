package com.ashok.jobtracker.service;

import com.ashok.jobtracker.dto.auth.AuthResponse;
import com.ashok.jobtracker.dto.auth.LoginRequest;
import com.ashok.jobtracker.dto.auth.RegisterRequest;
import com.ashok.jobtracker.dto.auth.RegisterResponse;
import com.ashok.jobtracker.entity.Role;
import com.ashok.jobtracker.entity.User;
import com.ashok.jobtracker.exception.BadRequestException;
import com.ashok.jobtracker.exception.ResourceNotFoundException;
import com.ashok.jobtracker.exception.UnauthorizedException;
import com.ashok.jobtracker.repository.UserRepository;
import com.ashok.jobtracker.security.JwtService;
import com.ashok.jobtracker.security.SecurityUtils;
import com.ashok.jobtracker.security.UserPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email().toLowerCase())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email().toLowerCase())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        user = userRepository.save(user);
        return buildAuthResponse(user);
    }

    public List<RegisterResponse> listRegistrations() {
        SecurityUtils.requireAdmin();
        return userRepository.findAll().stream().map(RegisterResponse::from).toList();
    }

    public RegisterResponse getRegistration(String registerId) {
        SecurityUtils.requireSelfOrAdmin(registerId);
        return userRepository
                .findById(registerId)
                .map(RegisterResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
    }

    public void deleteRegistration(String registerId) {
        SecurityUtils.requireSelfOrAdmin(registerId);
        if (!userRepository.existsById(registerId)) {
            throw new ResourceNotFoundException("Registration not found");
        }
        userRepository.deleteById(registerId);
    }

    public void deleteAllRegistrations() {
        SecurityUtils.requireAdmin();
        userRepository.deleteAll();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository
                .findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtService.generateToken(principal);
        return AuthResponse.of(token, user.getId(), user.getEmail(), user.getName(), user.getRole().name());
    }
}
