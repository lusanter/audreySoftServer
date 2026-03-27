package com.audrey.soft.auth.domain.ports;

import com.audrey.soft.auth.domain.models.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    Optional<User> findByUsername(String username);
    User save(User user);
    boolean existsByUsername(String username);
    Optional<User> findById(UUID id);
}