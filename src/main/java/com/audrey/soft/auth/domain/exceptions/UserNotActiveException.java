package com.audrey.soft.auth.domain.exceptions;


public class UserNotActiveException extends RuntimeException {
    public UserNotActiveException(String username) {
        super("El usuario " + username + " está inactivo y no puede operar en el sistema.");
    }
}