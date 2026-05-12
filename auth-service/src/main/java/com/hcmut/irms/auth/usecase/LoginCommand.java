package com.hcmut.irms.auth.usecase;

public record LoginCommand(
        String username,
        String password
) {
}
