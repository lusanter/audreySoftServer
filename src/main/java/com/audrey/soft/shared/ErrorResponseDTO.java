package com.audrey.soft.shared;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {}
