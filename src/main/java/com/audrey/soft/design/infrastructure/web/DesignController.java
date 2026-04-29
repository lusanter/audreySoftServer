package com.audrey.soft.design.infrastructure.web;

import com.audrey.soft.auth.infrastructure.security.AudreyAuthPrincipal;
import com.audrey.soft.design.application.dtos.*;
import com.audrey.soft.design.application.usecases.AnalyticsUseCase;
import com.audrey.soft.design.application.usecases.GenerarFlyerUseCase;
import com.audrey.soft.design.application.usecases.HistorialFlyerUseCase;
import com.audrey.soft.design.application.usecases.PlantillaUseCase;
import com.audrey.soft.design.domain.ports.DesignEnginePort;
import com.audrey.soft.shared.TenantContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/design")
@RequiredArgsConstructor
public class DesignController {

    private final DesignEnginePort designEnginePort;
    private final PlantillaUseCase plantillaUseCase;
    private final GenerarFlyerUseCase generarFlyerUseCase;
    private final HistorialFlyerUseCase historialFlyerUseCase;
    private final AnalyticsUseCase analyticsUseCase;

    // ── Flyer ─────────────────────────────────────────────────────────────────

    @PostMapping("/flyer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FlyerVariacionesResponseDTO> generateFlyer(
            @Valid @RequestBody FlyerRequestDTO request,
            @AuthenticationPrincipal AudreyAuthPrincipal principal) {
        UUID empresaId = extractEmpresaId(principal);
        UUID usuarioId = extractUsuarioId(principal);
        return ResponseEntity.ok(generarFlyerUseCase.generarFlyer(request, empresaId, usuarioId));
    }

    @GetMapping("/flyers")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<FlyerGeneradoDTO>> listarHistorial(
            @AuthenticationPrincipal AudreyAuthPrincipal principal,
            @RequestParam(defaultValue = "0") int page) {
        UUID empresaId = extractEmpresaId(principal);
        return ResponseEntity.ok(historialFlyerUseCase.listarHistorial(empresaId, page));
    }

    @GetMapping("/flyers/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FlyerGeneradoDTO> obtenerFlyer(
            @PathVariable UUID id,
            @AuthenticationPrincipal AudreyAuthPrincipal principal) {
        UUID empresaId = extractEmpresaId(principal);
        return ResponseEntity.ok(historialFlyerUseCase.obtenerFlyer(id, empresaId));
    }

    @PostMapping("/flyers/{id}/regenerar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FlyerVariacionesResponseDTO> regenerarFlyer(
            @PathVariable UUID id,
            @AuthenticationPrincipal AudreyAuthPrincipal principal) {
        UUID empresaId = extractEmpresaId(principal);
        UUID usuarioId = extractUsuarioId(principal);
        return ResponseEntity.ok(historialFlyerUseCase.regenerarFlyer(id, empresaId, usuarioId));
    }

    @GetMapping("/analytics/productos-mas-promovidos")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AnalyticsUseCase.ProductoPromovidoDTO>> productosMasPromovidos(
            @AuthenticationPrincipal AudreyAuthPrincipal principal) {
        UUID empresaId = extractEmpresaId(principal);
        return ResponseEntity.ok(analyticsUseCase.productosMasPromovidos(empresaId, 10));
    }

    // ── Plantillas ────────────────────────────────────────────────────────────

    @PostMapping("/plantillas")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<PlantillaResponseDTO> crearPlantilla(
            @Valid @RequestBody PlantillaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(plantillaUseCase.crearPlantilla(request, null));
    }

    @GetMapping("/plantillas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlantillaResponseDTO>> obtenerPlantillas() {
        return ResponseEntity.ok(plantillaUseCase.obtenerPlantillasActivas());
    }

    @GetMapping("/plantillas/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlantillaResponseDTO> obtenerPlantilla(@PathVariable String id) {
        return ResponseEntity.ok(plantillaUseCase.obtenerPlantilla(id));
    }

    @PutMapping("/plantillas/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<PlantillaResponseDTO> actualizarPlantilla(
            @PathVariable String id, @Valid @RequestBody PlantillaRequestDTO request) {
        return ResponseEntity.ok(plantillaUseCase.actualizarPlantilla(id, request));
    }

    @DeleteMapping("/plantillas/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> eliminarPlantilla(@PathVariable String id) {
        plantillaUseCase.eliminarPlantilla(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/plantillas/tipo/{tipoUso}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlantillaResponseDTO>> obtenerPorTipo(
            @PathVariable PlantillaRequestDTO.TipoUso tipoUso) {
        return ResponseEntity.ok(plantillaUseCase.obtenerPlantillasPorTipo(tipoUso));
    }

    @GetMapping("/plantillas/formato/{formato}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlantillaResponseDTO>> obtenerPorFormato(
            @PathVariable PlantillaRequestDTO.Formato formato) {
        return ResponseEntity.ok(plantillaUseCase.obtenerPlantillasPorFormato(formato));
    }

    // ── Recursos visuales (proxy al MS de IA) ────────────────────────────────

    @GetMapping("/recursos")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> listarRecursos(
            @RequestParam(required = false) String tipo) {
        return ResponseEntity.ok(designEnginePort.listarRecursos(tipo));
    }

    @PostMapping("/recursos")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> indexarRecurso(
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(designEnginePort.indexarRecurso(request));
    }

    @PutMapping("/recursos/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> actualizarRecurso(
            @PathVariable String id, @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(designEnginePort.actualizarRecurso(id, request));
    }

    @DeleteMapping("/recursos/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> eliminarRecurso(@PathVariable String id) {
        designEnginePort.eliminarRecurso(id);
        return ResponseEntity.noContent().build();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private UUID extractEmpresaId(AudreyAuthPrincipal principal) {
        // Prefer TenantContext (set by JwtAuthenticationFilter for EMPRESA scope)
        UUID fromContext = TenantContext.getEmpresaId();
        if (fromContext != null) return fromContext;
        // Fallback: use scopeId from principal if scope is EMPRESA
        if (principal != null && principal.scopeId() != null) return principal.scopeId();
        return UUID.fromString("00000000-0000-0000-0000-000000000000");
    }

    private UUID extractUsuarioId(AudreyAuthPrincipal principal) {
        // The username in AudreyAuthPrincipal is the user's identifier
        // If it's a UUID string, parse it; otherwise return nil UUID
        if (principal != null) {
            try {
                return UUID.fromString(principal.username());
            } catch (IllegalArgumentException ignored) {
                // username is not a UUID (e.g. email), return nil
            }
        }
        return UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}
