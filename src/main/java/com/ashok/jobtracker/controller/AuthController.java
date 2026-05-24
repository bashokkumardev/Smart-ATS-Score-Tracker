package com.ashok.jobtracker.controller;

import com.ashok.jobtracker.dto.auth.AuthResponse;
import com.ashok.jobtracker.dto.auth.LoginRequest;
import com.ashok.jobtracker.dto.auth.RegisterRequest;
import com.ashok.jobtracker.dto.auth.RegisterResponse;
import com.ashok.jobtracker.service.AuthService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @GetMapping({"/register", "/register/"})
    public List<RegisterResponse> listRegistrations() {
        return authService.listRegistrations();
    }

    @GetMapping({"/register/{registerId}", "/register/{registerId}/"})
    public RegisterResponse getRegistration(@PathVariable String registerId) {
        return authService.getRegistration(registerId);
    }

    @DeleteMapping({"/register", "/register/"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllRegistrations() {
        authService.deleteAllRegistrations();
    }

    @DeleteMapping({"/register/{registerId}", "/register/{registerId}/"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRegistration(@PathVariable String registerId) {
        authService.deleteRegistration(registerId);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
