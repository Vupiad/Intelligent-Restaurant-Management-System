package com.hcmut.irms.auth.service;

import com.hcmut.irms.auth.dto.AuthRequest;
import com.hcmut.irms.auth.dto.RegisterRequest;
import com.hcmut.irms.auth.model.User;
import com.hcmut.irms.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(AuthRequest request) {
        // 1. Find the user in Supabase
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Check if the raw password matches the BCrypt hash in the database
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 3. Generate and return the signed JWT
        return jwtService.generateToken(user.getUsername(), user.getRole().name());
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User(
                request.getUsername(),
                hashedPassword,
                request.getRole()
        );
        userRepository.save(newUser);
    }
}