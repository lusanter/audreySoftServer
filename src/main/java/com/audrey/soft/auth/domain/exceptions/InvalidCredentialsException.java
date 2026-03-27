package com.audrey.soft.auth.domain.exceptions;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException() {
        super("Credenciales inválidas");
    }
}
