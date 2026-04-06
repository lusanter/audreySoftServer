package com.audrey.soft.design.infrastructure.web;

import com.audrey.soft.design.application.dtos.AiFlyerDTO;
import com.audrey.soft.design.application.dtos.FlyerRequestDTO;
import com.audrey.soft.design.domain.ports.DesignEnginePort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/design")
@RequiredArgsConstructor
public class DesignController {

    private final DesignEnginePort designEnginePort;

    /**
     * Genera un flyer delegando al MS de IA.
     * El Core extrae la paleta de la empresa y la inyecta en el request.
     */
    @PostMapping("/flyer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AiFlyerDTO> generateFlyer(@Valid @RequestBody FlyerRequestDTO request) {
        return ResponseEntity.ok(designEnginePort.generateFlyer(request));
    }
}
