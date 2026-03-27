package com.audrey.soft.shared;

import com.audrey.soft.auth.domain.exceptions.InvalidCredentialsException;
import com.audrey.soft.auth.domain.exceptions.NoUserDBExeption;
import com.audrey.soft.auth.domain.exceptions.UserNotFoundException;
import com.audrey.soft.auth.domain.exceptions.UserNotActiveException;
import com.audrey.soft.auth.domain.exceptions.UsernameAlreadyExistsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage());
    }

    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotActive(UserNotActiveException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    @ExceptionHandler(NoUserDBExeption.class)
    public ResponseEntity<ErrorResponseDTO> handleNoUserDB(NoUserDBExeption ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String rawMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        String cleanMessage = "Error de integridad en la base de datos.";
        if (rawMessage != null && rawMessage.contains("Detail:")) {
            cleanMessage = rawMessage.split("\n")[0];
        } else if (rawMessage != null) {
            cleanMessage = rawMessage;
        }
        return buildResponse(HttpStatus.CONFLICT, "Database Conflict", cleanMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }

    private ResponseEntity<ErrorResponseDTO> buildResponse(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(
                new ErrorResponseDTO(status.value(), error, message, LocalDateTime.now())
        );
    }
}
