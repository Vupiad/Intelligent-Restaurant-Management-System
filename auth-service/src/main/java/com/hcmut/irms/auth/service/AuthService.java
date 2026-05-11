package com.hcmut.irms.auth.service;

import com.hcmut.irms.auth.dto.AuthRequest;
import com.hcmut.irms.auth.dto.RegisterRequest;
import com.hcmut.irms.auth.exception.InvalidCredentialsException;
import com.hcmut.irms.auth.exception.UsernameAlreadyTakenException;
import com.hcmut.irms.auth.model.User;
import com.hcmut.irms.auth.repository.UserRepository;
import com.hcmut.irms.auth.usecase.AuthUseCase;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public String login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return tokenProvider.generateToken(user.getUsername(), user.getRole().name());
    }

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyTakenException();
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
