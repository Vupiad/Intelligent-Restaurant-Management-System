package com.hcmut.irms.auth.controller;

import com.hcmut.irms.auth.dto.AuthRequest;
import com.hcmut.irms.auth.dto.AuthResponse;
import com.hcmut.irms.auth.dto.RegisterRequest;
import com.hcmut.irms.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // Spring injects the service we built in Phase 4
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // The AuthService will crash the request with an Exception if the password is wrong.
        // If the code makes it to the next line, the password was perfect.
        String token = authService.login(request);

        // Wrap the token in our DTO and send a 200 OK back to React
        return ResponseEntity.ok(new AuthResponse(token));
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Account created successfully. Employee can now log in.");
    }
}