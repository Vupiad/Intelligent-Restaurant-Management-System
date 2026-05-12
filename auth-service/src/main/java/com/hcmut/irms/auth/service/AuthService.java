package com.hcmut.irms.auth.service;

import com.hcmut.irms.auth.exception.InvalidCredentialsException;
import com.hcmut.irms.auth.exception.UsernameAlreadyTakenException;
import com.hcmut.irms.auth.model.User;
import com.hcmut.irms.auth.port.UserAccountStore;
import com.hcmut.irms.auth.usecase.LoginCommand;
import com.hcmut.irms.auth.usecase.LoginUseCase;
import com.hcmut.irms.auth.usecase.RegisterCommand;
import com.hcmut.irms.auth.usecase.RegisterUseCase;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements LoginUseCase, RegisterUseCase {

    private final UserAccountStore userAccountStore;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public AuthService(UserAccountStore userAccountStore, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.userAccountStore = userAccountStore;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public String login(LoginCommand command) {
        User user = userAccountStore.findByUsername(command.username())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return tokenProvider.generateToken(user.getUsername(), user.getRole().name());
    }

    @Override
    public void register(RegisterCommand command) {
        if (userAccountStore.findByUsername(command.username()).isPresent()) {
            throw new UsernameAlreadyTakenException();
        }

        String hashedPassword = passwordEncoder.encode(command.password());

        User newUser = new User(
                command.username(),
                hashedPassword,
                command.role()
        );
        userAccountStore.save(newUser);
    }
}
