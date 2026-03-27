package com.audrey.soft.auth.domain.exceptions;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID userId) {
        super("No se encontró el usuario con id: " + userId);
    }

    public UserNotFoundException(String username) {
        super("No se encontró el usuario: " + username);
    }
}
