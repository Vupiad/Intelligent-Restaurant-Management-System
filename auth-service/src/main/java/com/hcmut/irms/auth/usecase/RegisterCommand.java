package com.hcmut.irms.auth.usecase;

import com.hcmut.irms.auth.model.Role;

public record RegisterCommand(
        String username,
        String password,
        Role role
) {
}
