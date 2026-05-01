package com.audrey.soft.billing.infrastructure.web;

import com.audrey.soft.billing.app.dtos.CreateVentaDirectaRequest;
import com.audrey.soft.billing.app.dtos.MetodoCobroDTO;
import com.audrey.soft.billing.app.dtos.MetodoCobroRequestDTO;
import com.audrey.soft.billing.app.dtos.VentaDTO;
import com.audrey.soft.billing.app.dtos.VentaFiltroDTO;
import com.audrey.soft.billing.app.usecases.MetodoCobro.CreateMetodoCobroUseCase;
import com.audrey.soft.billing.app.usecases.MetodoCobro.ListMetodosCobroUseCase;
import com.audrey.soft.billing.app.usecases.MetodoCobro.UpdateMetodoCobroUseCase;
import com.audrey.soft.billing.app.usecases.Venta.BuscarVentasUseCase;
import com.audrey.soft.billing.app.usecases.Venta.CreateVentaDirectaUseCase;
import com.audrey.soft.billing.app.usecases.Venta.GetVentaByIdUseCase;
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
    private final GetVentaByIdUseCase getVentaByIdUseCase;
    private final CreateVentaDirectaUseCase createVentaDirectaUseCase;
    private final ListMetodosCobroUseCase listMetodosCobroUseCase;
    private final CreateMetodoCobroUseCase createMetodoCobroUseCase;
    private final UpdateMetodoCobroUseCase updateMetodoCobroUseCase;

    public BillingController(ListVentasUseCase listVentasUseCase,
                             BuscarVentasUseCase buscarVentasUseCase,
                             GetVentaByIdUseCase getVentaByIdUseCase,
                             CreateVentaDirectaUseCase createVentaDirectaUseCase,
                             ListMetodosCobroUseCase listMetodosCobroUseCase,
                             CreateMetodoCobroUseCase createMetodoCobroUseCase,
                             UpdateMetodoCobroUseCase updateMetodoCobroUseCase) {
        this.listVentasUseCase = listVentasUseCase;
        this.buscarVentasUseCase = buscarVentasUseCase;
        this.getVentaByIdUseCase = getVentaByIdUseCase;
        this.createVentaDirectaUseCase = createVentaDirectaUseCase;
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
            @RequestParam(required = false) Boolean fiscalEnviado) {
        var filtro = new VentaFiltroDTO(desde, hasta, estado, tipoComprobante, serie, fiscalEnviado);
        return ResponseEntity.ok(buscarVentasUseCase.execute(sucursalId, filtro));
    }

    @GetMapping("/ventas/{ventaId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO','CAJERO')")
    public ResponseEntity<VentaDTO> getVenta(@PathVariable UUID sucursalId,
                                              @PathVariable UUID ventaId) {
        return ResponseEntity.ok(getVentaByIdUseCase.execute(ventaId));
    }

    // ── Métodos de cobro ───────────────────────────────────────────────────────

    @GetMapping("/metodos-cobro")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO','CAJERO')")
    public ResponseEntity<List<MetodoCobroDTO>> listarMetodosCobro(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(listMetodosCobroUseCase.execute(sucursalId));
    }

    @PostMapping("/metodos-cobro")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
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
