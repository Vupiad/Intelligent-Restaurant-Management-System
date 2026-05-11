package com.hcmut.irms.auth.controller;

import com.hcmut.irms.auth.dto.AuthRequest;
import com.hcmut.irms.auth.dto.AuthResponse;
import com.hcmut.irms.auth.dto.RegisterRequest;
import com.hcmut.irms.auth.usecase.AuthUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        String token = authUseCase.login(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authUseCase.register(request);
        return ResponseEntity.ok("Account created successfully. Employee can now log in.");
    }
}
