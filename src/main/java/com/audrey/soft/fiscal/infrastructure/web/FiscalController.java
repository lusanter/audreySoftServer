package com.audrey.soft.fiscal.infrastructure.web;

import com.audrey.soft.fiscal.app.dtos.ComprobanteSerieDTO;
import com.audrey.soft.fiscal.app.dtos.FiscalConfigDTO;
import com.audrey.soft.fiscal.app.dtos.ImpuestoTipoDTO;
import com.audrey.soft.fiscal.app.usecases.FiscalConfig.GetFiscalConfigUseCase;
import com.audrey.soft.fiscal.app.usecases.FiscalConfig.UpdateFiscalConfigUseCase;
import com.audrey.soft.fiscal.app.usecases.Serie.CreateComprobanteSerieUseCase;
import com.audrey.soft.fiscal.app.usecases.Serie.ListComprobanteSeriesUseCase;
import com.audrey.soft.fiscal.app.usecases.Serie.UpdateComprobanteSerieUseCase;
import com.audrey.soft.fiscal.infrastructure.persistence.repositories.SpringDataImpuestoTipoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fiscal/{sucursalId}")
public class FiscalController {

    private final GetFiscalConfigUseCase getFiscalConfigUseCase;
    private final UpdateFiscalConfigUseCase updateFiscalConfigUseCase;
    private final SpringDataImpuestoTipoRepository impuestoTipoRepository;
    private final CreateComprobanteSerieUseCase createSerieUseCase;
    private final ListComprobanteSeriesUseCase listSeriesUseCase;
    private final UpdateComprobanteSerieUseCase updateSerieUseCase;

    public FiscalController(GetFiscalConfigUseCase getFiscalConfigUseCase,
                            UpdateFiscalConfigUseCase updateFiscalConfigUseCase,
                            SpringDataImpuestoTipoRepository impuestoTipoRepository,
                            CreateComprobanteSerieUseCase createSerieUseCase,
                            ListComprobanteSeriesUseCase listSeriesUseCase,
                            UpdateComprobanteSerieUseCase updateSerieUseCase) {
        this.getFiscalConfigUseCase = getFiscalConfigUseCase;
        this.updateFiscalConfigUseCase = updateFiscalConfigUseCase;
        this.impuestoTipoRepository = impuestoTipoRepository;
        this.createSerieUseCase = createSerieUseCase;
        this.listSeriesUseCase = listSeriesUseCase;
        this.updateSerieUseCase = updateSerieUseCase;
    }

    // ── Configuración fiscal ───────────────────────────────────────────────────

    @GetMapping("/config")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<FiscalConfigDTO> getFiscalConfig(@PathVariable UUID sucursalId) {
        FiscalConfigDTO config = getFiscalConfigUseCase.execute(sucursalId);
        if (config == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(config);
    }

    @PutMapping("/config")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<FiscalConfigDTO> updateFiscalConfig(@PathVariable UUID sucursalId,
                                                               @RequestBody FiscalConfigDTO request) {
        return ResponseEntity.ok(updateFiscalConfigUseCase.execute(sucursalId, request));
    }

    /**
     * Retorna los tipos de impuesto para un sistema fiscal dado (ej. SUNAT, SRI).
     * Usado por el frontend para mostrar qué impuestos aplican (solo lectura).
     */
    @GetMapping("/config/impuestos/{fiscalSistemaId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<List<ImpuestoTipoDTO>> getImpuestosTipo(
            @PathVariable UUID sucursalId,
            @PathVariable String fiscalSistemaId) {
        var list = impuestoTipoRepository.findAll().stream()
                .filter(i -> fiscalSistemaId.equals(i.getFiscalSistemaId()) && i.isActivo())
                .map(i -> new ImpuestoTipoDTO(i.getId(), i.getFiscalSistemaId(),
                        i.getCodigo(), i.getNombre(), i.getTasaDefault(), i.getTipoCalculo(), i.isActivo()))
                .toList();
        return ResponseEntity.ok(list);
    }

    // ── Series de comprobante ──────────────────────────────────────────────────

    @PostMapping("/series")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<ComprobanteSerieDTO> crearSerie(@PathVariable UUID sucursalId,
                                                          @RequestBody ComprobanteSerieDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createSerieUseCase.execute(sucursalId, request));
    }

    @GetMapping("/series")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<List<ComprobanteSerieDTO>> listarSeries(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(listSeriesUseCase.execute(sucursalId));
    }

    @PutMapping("/series/{serieId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<ComprobanteSerieDTO> actualizarSerie(@PathVariable UUID sucursalId,
                                                               @PathVariable UUID serieId,
                                                               @RequestBody ComprobanteSerieDTO request) {
        return ResponseEntity.ok(updateSerieUseCase.execute(serieId, request));
    }
}
