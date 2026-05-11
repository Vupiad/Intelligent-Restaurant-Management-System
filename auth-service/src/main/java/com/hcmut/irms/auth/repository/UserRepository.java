package com.hcmut.irms.auth.repository;

import com.hcmut.irms.auth.model.User;
import com.hcmut.irms.auth.port.UserAccountStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserAccountStore {
    Optional<User> findByUsername(String username);
}
