package com.audrey.soft.inventory.infrastructure.web;

import com.audrey.soft.inventory.app.dtos.*;
import com.audrey.soft.inventory.app.usecases.Categoria.CreateCategoriaUseCase;
import com.audrey.soft.inventory.app.usecases.Categoria.ListCategoriasUseCase;
import com.audrey.soft.inventory.app.usecases.Categoria.UpdateCategoriaUseCase;
import com.audrey.soft.inventory.app.usecases.Cliente.CreateClienteUseCase;
import com.audrey.soft.inventory.app.usecases.Cliente.ListClientesUseCase;
import com.audrey.soft.inventory.app.usecases.Producto.CreateProductoUseCase;
import com.audrey.soft.inventory.app.usecases.Producto.ListProductosUseCase;
import com.audrey.soft.inventory.app.usecases.Producto.UpdateProductoUseCase;
import com.audrey.soft.inventory.app.usecases.StockMovement.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory/{sucursalId}")
public class InventoryController {

    private final CreateCategoriaUseCase createCategoriaUseCase;
    private final ListCategoriasUseCase listCategoriasUseCase;
    private final UpdateCategoriaUseCase updateCategoriaUseCase;
    private final CreateProductoUseCase createProductoUseCase;
    private final UpdateProductoUseCase updateProductoUseCase;
    private final ListProductosUseCase listProductosUseCase;
    private final CreateClienteUseCase createClienteUseCase;
    private final ListClientesUseCase listClientesUseCase;
    private final ListStockMovementsUseCase listStockMovementsUseCase;
    private final CreateStockEntradaUseCase createStockEntradaUseCase;
    private final CreateStockAjusteUseCase createStockAjusteUseCase;
    private final ListAjusteMotivosUseCase listAjusteMotivosUseCase;
    private final CreateAjusteMotivoUseCase createAjusteMotivoUseCase;
    private final UpdateAjusteMotivoUseCase updateAjusteMotivoUseCase;
    private final GetInventoryKpiUseCase getInventoryKpiUseCase;

    public InventoryController(CreateCategoriaUseCase createCategoriaUseCase,
                               ListCategoriasUseCase listCategoriasUseCase,
                               UpdateCategoriaUseCase updateCategoriaUseCase,
                               CreateProductoUseCase createProductoUseCase,
                               UpdateProductoUseCase updateProductoUseCase,
                               ListProductosUseCase listProductosUseCase,
                               CreateClienteUseCase createClienteUseCase,
                               ListClientesUseCase listClientesUseCase,
                               ListStockMovementsUseCase listStockMovementsUseCase,
                               CreateStockEntradaUseCase createStockEntradaUseCase,
                               CreateStockAjusteUseCase createStockAjusteUseCase,
                               ListAjusteMotivosUseCase listAjusteMotivosUseCase,
                               CreateAjusteMotivoUseCase createAjusteMotivoUseCase,
                               UpdateAjusteMotivoUseCase updateAjusteMotivoUseCase,
                               GetInventoryKpiUseCase getInventoryKpiUseCase) {
        this.createCategoriaUseCase = createCategoriaUseCase;
        this.listCategoriasUseCase = listCategoriasUseCase;
        this.updateCategoriaUseCase = updateCategoriaUseCase;
        this.createProductoUseCase = createProductoUseCase;
        this.updateProductoUseCase = updateProductoUseCase;
        this.listProductosUseCase = listProductosUseCase;
        this.createClienteUseCase = createClienteUseCase;
        this.listClientesUseCase = listClientesUseCase;
        this.listStockMovementsUseCase = listStockMovementsUseCase;
        this.createStockEntradaUseCase = createStockEntradaUseCase;
        this.createStockAjusteUseCase = createStockAjusteUseCase;
        this.listAjusteMotivosUseCase = listAjusteMotivosUseCase;
        this.createAjusteMotivoUseCase = createAjusteMotivoUseCase;
        this.updateAjusteMotivoUseCase = updateAjusteMotivoUseCase;
        this.getInventoryKpiUseCase = getInventoryKpiUseCase;
    }

    // ── Categorías ─────────────────────────────────────────────────────────────

    @PostMapping("/categorias")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<CategoriaDTO> crearCategoria(@PathVariable UUID sucursalId,
                                                       @RequestBody CategoriaDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createCategoriaUseCase.execute(sucursalId, request));
    }

    @GetMapping("/categorias")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO','CAJERO')")
    public ResponseEntity<List<CategoriaDTO>> listarCategorias(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(listCategoriasUseCase.execute(sucursalId));
    }

    @PutMapping("/categorias/{categoriaId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<CategoriaDTO> actualizarCategoria(@PathVariable UUID sucursalId,
                                                            @PathVariable UUID categoriaId,
                                                            @RequestBody CategoriaDTO request) {
        return ResponseEntity.ok(updateCategoriaUseCase.execute(categoriaId, request));
    }

    // ── Productos ──────────────────────────────────────────────────────────────

    @PostMapping("/productos")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<ProductoDTO> crearProducto(@PathVariable UUID sucursalId,
                                                     @RequestBody ProductoDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createProductoUseCase.execute(sucursalId, request));
    }

    @PutMapping("/productos/{productoId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable UUID sucursalId,
                                                          @PathVariable UUID productoId,
                                                          @RequestBody ProductoDTO request) {
        return ResponseEntity.ok(updateProductoUseCase.execute(productoId, request));
    }

    @GetMapping("/productos")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO','CAJERO','MOZO','COCINERO')")
    public ResponseEntity<List<ProductoDTO>> listarProductos(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(listProductosUseCase.execute(sucursalId));
    }

    // ── Clientes ───────────────────────────────────────────────────────────────

    @PostMapping("/clientes")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO','CAJERO')")
    public ResponseEntity<ClienteDTO> crearCliente(@PathVariable UUID sucursalId,
                                                   @RequestBody ClienteDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createClienteUseCase.execute(sucursalId, request));
    }

    @GetMapping("/clientes")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO','CAJERO')")
    public ResponseEntity<List<ClienteDTO>> listarClientes(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(listClientesUseCase.execute(sucursalId));
    }

    // ── Movimientos ─────────────────────────────────────────────────────────────

    @GetMapping("/movimientos")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<List<StockMovementDTO>> listarMovimientos(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(listStockMovementsUseCase.execute(sucursalId));
    }

    @PostMapping("/movimientos/entrada")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<StockMovementDTO> registrarEntrada(@PathVariable UUID sucursalId,
                                                             @RequestBody StockEntradaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createStockEntradaUseCase.execute(request));
    }

    @PostMapping("/movimientos/ajuste")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<StockMovementDTO> registrarAjuste(@PathVariable UUID sucursalId,
                                                             @RequestBody StockAjusteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createStockAjusteUseCase.execute(request));
    }

    @GetMapping("/motivos-ajuste")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<List<AjusteMotivoDTO>> listarMotivosAjuste(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(listAjusteMotivosUseCase.execute(sucursalId));
    }
    @PostMapping("/motivos-ajuste")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<AjusteMotivoDTO> crearMotivoAjuste(@PathVariable UUID sucursalId,
                                                             @RequestBody AjusteMotivoDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createAjusteMotivoUseCase.execute(sucursalId, request));
    }

    @PutMapping("/motivos-ajuste/{motivoId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<AjusteMotivoDTO> actualizarMotivoAjuste(@PathVariable UUID sucursalId,
                                                                   @PathVariable UUID motivoId,
                                                                   @RequestBody AjusteMotivoDTO request) {
        return ResponseEntity.ok(updateAjusteMotivoUseCase.execute(motivoId, request));
    }

    // ── KPIs ──────────────────────────────────────────────────────────────────

    @GetMapping("/kpis")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<InventoryKpiDTO> obtenerKpis(
            @PathVariable UUID sucursalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(getInventoryKpiUseCase.execute(sucursalId, start, end));
    }
}
