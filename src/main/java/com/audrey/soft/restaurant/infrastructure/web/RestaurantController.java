package com.audrey.soft.restaurant.infrastructure.web;

import com.audrey.soft.restaurant.app.dtos.*;
import com.audrey.soft.restaurant.app.usecases.Comanda.AbrirComandaUseCase;
import com.audrey.soft.restaurant.app.usecases.Comanda.AgregarItemUseCase;
import com.audrey.soft.restaurant.app.usecases.Comanda.CerrarComandaUseCase;
import com.audrey.soft.restaurant.app.usecases.Comanda.ListComandasUseCase;
import com.audrey.soft.restaurant.app.usecases.Comanda.MoverItemSubcuentaUseCase;
import com.audrey.soft.restaurant.app.usecases.Mesa.CreateMesaUseCase;
import com.audrey.soft.restaurant.app.usecases.Mesa.DeleteMesaUseCase;
import com.audrey.soft.restaurant.app.usecases.Mesa.ListMesasUseCase;
import com.audrey.soft.restaurant.app.usecases.Mesa.UpdateMesaUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurant/{sucursalId}")
public class RestaurantController {

    private final CreateMesaUseCase createMesaUseCase;
    private final ListMesasUseCase listMesasUseCase;
    private final UpdateMesaUseCase updateMesaUseCase;
    private final DeleteMesaUseCase deleteMesaUseCase;
    private final AbrirComandaUseCase abrirComandaUseCase;
    private final AgregarItemUseCase agregarItemUseCase;
    private final CerrarComandaUseCase cerrarComandaUseCase;
    private final MoverItemSubcuentaUseCase moverItemSubcuentaUseCase;
    private final ListComandasUseCase listComandasUseCase;

    public RestaurantController(CreateMesaUseCase createMesaUseCase,
                                ListMesasUseCase listMesasUseCase,
                                UpdateMesaUseCase updateMesaUseCase,
                                DeleteMesaUseCase deleteMesaUseCase,
                                AbrirComandaUseCase abrirComandaUseCase,
                                AgregarItemUseCase agregarItemUseCase,
                                CerrarComandaUseCase cerrarComandaUseCase,
                                MoverItemSubcuentaUseCase moverItemSubcuentaUseCase,
                                ListComandasUseCase listComandasUseCase) {
        this.createMesaUseCase = createMesaUseCase;
        this.listMesasUseCase = listMesasUseCase;
        this.updateMesaUseCase = updateMesaUseCase;
        this.deleteMesaUseCase = deleteMesaUseCase;
        this.abrirComandaUseCase = abrirComandaUseCase;
        this.agregarItemUseCase = agregarItemUseCase;
        this.cerrarComandaUseCase = cerrarComandaUseCase;
        this.moverItemSubcuentaUseCase = moverItemSubcuentaUseCase;
        this.listComandasUseCase = listComandasUseCase;
    }

    // ── Mesas ──────────────────────────────────────────────────────────────────

    @PostMapping("/mesas")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<MesaDTO> crearMesa(@PathVariable UUID sucursalId,
                                             @RequestBody MesaDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createMesaUseCase.execute(sucursalId, request));
    }

    @GetMapping("/mesas")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO','CAJERO','MOZO')")
    public ResponseEntity<List<MesaDTO>> listarMesas(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(listMesasUseCase.execute(sucursalId));
    }

    @PutMapping("/mesas/{mesaId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<MesaDTO> actualizarMesa(@PathVariable UUID sucursalId,
                                                  @PathVariable UUID mesaId,
                                                  @RequestBody MesaDTO request) {
        return ResponseEntity.ok(updateMesaUseCase.execute(mesaId, request));
    }

    @DeleteMapping("/mesas/{mesaId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO')")
    public ResponseEntity<Void> eliminarMesa(@PathVariable UUID sucursalId,
                                             @PathVariable UUID mesaId) {
        deleteMesaUseCase.execute(mesaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/comandas")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO','CAJERO','MOZO','COCINERO')")
    public ResponseEntity<List<ComandaDTO>> listarComandas(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(listComandasUseCase.execute(sucursalId));
    }

    // ── Comandas ───────────────────────────────────────────────────────────────

    @PostMapping("/comandas")
    @PreAuthorize("hasAnyRole('CAJERO','MOZO','ENCARGADO','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ComandaDTO> abrirComanda(@PathVariable UUID sucursalId,
                                                   @RequestParam UUID mesaId,
                                                   @RequestParam(required = false) UUID clienteId,
                                                   @RequestParam(required = false) String notas) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(abrirComandaUseCase.execute(sucursalId, mesaId, clienteId, notas));
    }

    @PostMapping("/comandas/{comandaId}/items")
    @PreAuthorize("hasAnyRole('CAJERO','MOZO','ENCARGADO','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ComandaDTO> agregarItem(@PathVariable UUID sucursalId,
                                                  @PathVariable UUID comandaId,
                                                  @RequestBody AgregarItemRequest request) {
        return ResponseEntity.ok(agregarItemUseCase.execute(comandaId, request));
    }

    @PostMapping("/comandas/{comandaId}/cerrar")
    @PreAuthorize("hasAnyRole('CAJERO','ENCARGADO','ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ComandaDTO> cerrarComanda(@PathVariable UUID sucursalId,
                                                    @PathVariable UUID comandaId,
                                                    @RequestBody CerrarComandaRequest request) {
        return ResponseEntity.ok(cerrarComandaUseCase.execute(comandaId, request));
    }

    @PatchMapping("/comandas/{comandaId}/items/mover")
    @PreAuthorize("hasAnyRole('CAJERO','ENCARGADO','ADMIN','SUPER_ADMIN','MOZO')")
    public ResponseEntity<ComandaDTO> moverItem(@PathVariable UUID sucursalId,
                                                @PathVariable UUID comandaId,
                                                @RequestBody MoverItemRequest request) {
        return ResponseEntity.ok(moverItemSubcuentaUseCase.execute(comandaId, request.itemId(), request.subCuenta()));
    }
}
