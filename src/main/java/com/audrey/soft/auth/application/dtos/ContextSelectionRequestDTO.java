package com.audrey.soft.auth.application.dtos;

import java.util.UUID;

public record ContextSelectionRequestDTO(UUID assignmentId) {
    public ContextSelectionRequestDTO {
        if (assignmentId == null)
            throw new IllegalArgumentException("Debe proporcionar un assignmentId de contexto.");
    }
}
