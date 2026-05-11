package com.hcmut.irms.auth.controller;

import com.hcmut.irms.auth.dto.AuthRequest;
import com.hcmut.irms.auth.dto.AuthResponse;
import com.hcmut.irms.auth.dto.RegisterRequest;
import com.hcmut.irms.auth.usecase.LoginCommand;
import com.hcmut.irms.auth.usecase.LoginUseCase;
import com.hcmut.irms.auth.usecase.RegisterCommand;
import com.hcmut.irms.auth.usecase.RegisterUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;

    public AuthController(LoginUseCase loginUseCase, RegisterUseCase registerUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        String token = loginUseCase.login(new LoginCommand(request.getUsername(), request.getPassword()));
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        registerUseCase.register(new RegisterCommand(request.getUsername(), request.getPassword(), request.getRole()));
        return ResponseEntity.ok("Account created successfully. Employee can now log in.");
    }
}
