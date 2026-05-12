package com.hcmut.irms.auth.port;

import com.hcmut.irms.auth.model.User;

import java.util.Optional;

public interface UserAccountStore {
    Optional<User> findByUsername(String username);

    User save(User user);
}
