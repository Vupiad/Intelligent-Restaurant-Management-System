package com.hcmut.irms.auth.usecase;

import com.hcmut.irms.auth.dto.AuthRequest;
import com.hcmut.irms.auth.dto.RegisterRequest;

public interface AuthUseCase {
    String login(AuthRequest request);

    void register(RegisterRequest request);
}
