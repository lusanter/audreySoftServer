package com.audrey.soft.billing.infrastructure.web;

import com.audrey.soft.billing.app.dtos.ComprobanteSerieDTO;
import com.audrey.soft.billing.app.dtos.MetodoCobroDTO;
import com.audrey.soft.billing.app.dtos.VentaDTO;
import com.audrey.soft.billing.app.usecases.MetodoCobro.ListMetodosCobroUseCase;
import com.audrey.soft.billing.app.usecases.Serie.CreateComprobanteSerieUseCase;
import com.audrey.soft.billing.app.usecases.Serie.ListComprobanteSeriesUseCase;
import com.audrey.soft.billing.app.usecases.Venta.ListVentasUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing/{sucursalId}")
public class BillingController {

    private final ListVentasUseCase listVentasUseCase;
    private final CreateComprobanteSerieUseCase createSerieUseCase;
    private final ListComprobanteSeriesUseCase listSeriesUseCase;
    private final ListMetodosCobroUseCase listMetodosCobroUseCase;

    public BillingController(ListVentasUseCase listVentasUseCase,
                             CreateComprobanteSerieUseCase createSerieUseCase,
                             ListComprobanteSeriesUseCase listSeriesUseCase,
                             ListMetodosCobroUseCase listMetodosCobroUseCase) {
        this.listVentasUseCase = listVentasUseCase;
        this.createSerieUseCase = createSerieUseCase;
        this.listSeriesUseCase = listSeriesUseCase;
        this.listMetodosCobroUseCase = listMetodosCobroUseCase;
    }

    // ── Ventas ─────────────────────────────────────────────────────────────────

    @GetMapping("/ventas")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<List<VentaDTO>> listarVentas(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(listVentasUseCase.execute(sucursalId));
    }

    // ── Series de comprobante ──────────────────────────────────────────────────

    @PostMapping("/series")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ComprobanteSerieDTO> crearSerie(@PathVariable UUID sucursalId,
                                                          @RequestBody ComprobanteSerieDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createSerieUseCase.execute(sucursalId, request));
    }

    @GetMapping("/series")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<List<ComprobanteSerieDTO>> listarSeries(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(listSeriesUseCase.execute(sucursalId));
    }

    // ── Métodos de cobro ───────────────────────────────────────────────────────

    @GetMapping("/metodos-cobro")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO','CAJERO')")
    public ResponseEntity<List<MetodoCobroDTO>> listarMetodosCobro(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(listMetodosCobroUseCase.execute(sucursalId));
    }
}
