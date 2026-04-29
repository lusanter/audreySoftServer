package com.audrey.soft.billing.infrastructure.web;

import com.audrey.soft.billing.app.dtos.ComprobanteSerieDTO;
import com.audrey.soft.billing.app.dtos.CreateVentaDirectaRequest;
import com.audrey.soft.billing.app.dtos.MetodoCobroDTO;
import com.audrey.soft.billing.app.dtos.MetodoCobroRequestDTO;
import com.audrey.soft.billing.app.dtos.VentaDTO;
import com.audrey.soft.billing.app.dtos.VentaFiltroDTO;
import com.audrey.soft.billing.app.usecases.MetodoCobro.CreateMetodoCobroUseCase;
import com.audrey.soft.billing.app.usecases.MetodoCobro.ListMetodosCobroUseCase;
import com.audrey.soft.billing.app.usecases.MetodoCobro.UpdateMetodoCobroUseCase;
import com.audrey.soft.billing.app.usecases.Serie.CreateComprobanteSerieUseCase;
import com.audrey.soft.billing.app.usecases.Serie.ListComprobanteSeriesUseCase;
import com.audrey.soft.billing.app.usecases.Serie.UpdateComprobanteSerieUseCase;
import com.audrey.soft.billing.app.usecases.Venta.BuscarVentasUseCase;
import com.audrey.soft.billing.app.usecases.Venta.CreateVentaDirectaUseCase;
import com.audrey.soft.billing.app.usecases.Venta.ListVentasUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/{sucursalId}")
public class BillingController {

    private final ListVentasUseCase listVentasUseCase;
    private final BuscarVentasUseCase buscarVentasUseCase;
    private final CreateVentaDirectaUseCase createVentaDirectaUseCase;
    private final CreateComprobanteSerieUseCase createSerieUseCase;
    private final ListComprobanteSeriesUseCase listSeriesUseCase;
    private final UpdateComprobanteSerieUseCase updateSerieUseCase;
    private final ListMetodosCobroUseCase listMetodosCobroUseCase;
    private final CreateMetodoCobroUseCase createMetodoCobroUseCase;
    private final UpdateMetodoCobroUseCase updateMetodoCobroUseCase;

    public BillingController(ListVentasUseCase listVentasUseCase,
                             BuscarVentasUseCase buscarVentasUseCase,
                             CreateVentaDirectaUseCase createVentaDirectaUseCase,
                             CreateComprobanteSerieUseCase createSerieUseCase,
                             ListComprobanteSeriesUseCase listSeriesUseCase,
                             UpdateComprobanteSerieUseCase updateSerieUseCase,
                             ListMetodosCobroUseCase listMetodosCobroUseCase,
                             CreateMetodoCobroUseCase createMetodoCobroUseCase,
                             UpdateMetodoCobroUseCase updateMetodoCobroUseCase) {
        this.listVentasUseCase = listVentasUseCase;
        this.buscarVentasUseCase = buscarVentasUseCase;
        this.createVentaDirectaUseCase = createVentaDirectaUseCase;
        this.createSerieUseCase = createSerieUseCase;
        this.listSeriesUseCase = listSeriesUseCase;
        this.updateSerieUseCase = updateSerieUseCase;
        this.listMetodosCobroUseCase = listMetodosCobroUseCase;
        this.createMetodoCobroUseCase = createMetodoCobroUseCase;
        this.updateMetodoCobroUseCase = updateMetodoCobroUseCase;
    }

    // ── Ventas ─────────────────────────────────────────────────────────────────

    @PostMapping("/ventas/directa")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO','CAJERO')")
    public ResponseEntity<VentaDTO> crearVentaDirecta(@PathVariable UUID sucursalId,
                                                       @RequestBody CreateVentaDirectaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createVentaDirectaUseCase.execute(sucursalId, request));
    }

    @GetMapping("/ventas")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<List<VentaDTO>> listarVentas(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(listVentasUseCase.execute(sucursalId));
    }

    @GetMapping("/ventas/buscar")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<List<VentaDTO>> buscarVentas(
            @PathVariable UUID sucursalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String tipoComprobante,
            @RequestParam(required = false) String serie,
            @RequestParam(required = false) Boolean sunatEnviado) {
        var filtro = new VentaFiltroDTO(desde, hasta, estado, tipoComprobante, serie, sunatEnviado);
        return ResponseEntity.ok(buscarVentasUseCase.execute(sucursalId, filtro));
    }

    // ── Series de comprobante ──────────────────────────────────────────────────

    @PostMapping("/series")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN', 'ENCARGADO')")
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

    // ── Métodos de cobro ───────────────────────────────────────────────────────

    @GetMapping("/metodos-cobro")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO','CAJERO')")
    public ResponseEntity<List<MetodoCobroDTO>> listarMetodosCobro(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(listMetodosCobroUseCase.execute(sucursalId));
    }

    @PostMapping("/metodos-cobro")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN', 'ENCARGADO')")
    public ResponseEntity<MetodoCobroDTO> crearMetodoCobro(@PathVariable UUID sucursalId,
                                                           @RequestBody MetodoCobroRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createMetodoCobroUseCase.execute(sucursalId, request));
    }

    @PutMapping("/metodos-cobro/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<MetodoCobroDTO> actualizarMetodoCobro(@PathVariable UUID sucursalId,
                                                                @PathVariable UUID id,
                                                                @RequestBody MetodoCobroRequestDTO request) {
        return ResponseEntity.ok(updateMetodoCobroUseCase.execute(id, request));
    }
}
