package com.audrey.soft.tenant.infrastructure.web;

import com.audrey.soft.auth.infrastructure.security.AudreyAuthPrincipal;
import com.audrey.soft.tenant.app.dtos.EmpresaDTO;
import com.audrey.soft.tenant.app.dtos.SucursalDTO;
import com.audrey.soft.tenant.app.usecases.Empresa.CreateEnterpriseUseCase;
import com.audrey.soft.tenant.app.usecases.Empresa.UpdateEnterpriseUseCase;
import com.audrey.soft.tenant.app.usecases.Empresa.ListEnterprisesUseCase;
import com.audrey.soft.tenant.app.usecases.Empresa.ToggleEnterpriseStatusUseCase;
import com.audrey.soft.tenant.app.usecases.Sucursal.CreateBranchUseCase;
import com.audrey.soft.tenant.app.usecases.Sucursal.GetBranchByIdUseCase;
import com.audrey.soft.tenant.app.usecases.Sucursal.ListBranchesUseCase;
import com.audrey.soft.tenant.app.usecases.Sucursal.UpdateBranchUseCase;
import com.audrey.soft.tenant.app.usecases.Sucursal.ToggleBranchStatusUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/empresas")
public class EmpresaController {

    private final CreateEnterpriseUseCase createEnterpriseUseCase;
    private final UpdateEnterpriseUseCase updateEnterpriseUseCase;
    private final ListEnterprisesUseCase listEnterprisesUseCase;
    private final ToggleEnterpriseStatusUseCase toggleEnterpriseStatusUseCase;
    private final CreateBranchUseCase createBranchUseCase;
    private final GetBranchByIdUseCase getBranchByIdUseCase;
    private final ListBranchesUseCase listBranchesUseCase;
    private final UpdateBranchUseCase updateBranchUseCase;
    private final ToggleBranchStatusUseCase toggleBranchStatusUseCase;

    public EmpresaController(CreateEnterpriseUseCase createEnterpriseUseCase,
                             UpdateEnterpriseUseCase updateEnterpriseUseCase,
                             ListEnterprisesUseCase listEnterprisesUseCase,
                             ToggleEnterpriseStatusUseCase toggleEnterpriseStatusUseCase,
                             CreateBranchUseCase createBranchUseCase,
                             GetBranchByIdUseCase getBranchByIdUseCase,
                             ListBranchesUseCase listBranchesUseCase,
                             UpdateBranchUseCase updateBranchUseCase,
                             ToggleBranchStatusUseCase toggleBranchStatusUseCase) {
        this.createEnterpriseUseCase = createEnterpriseUseCase;
        this.updateEnterpriseUseCase = updateEnterpriseUseCase;
        this.listEnterprisesUseCase = listEnterprisesUseCase;
        this.toggleEnterpriseStatusUseCase = toggleEnterpriseStatusUseCase;
        this.createBranchUseCase = createBranchUseCase;
        this.getBranchByIdUseCase = getBranchByIdUseCase;
        this.listBranchesUseCase = listBranchesUseCase;
        this.updateBranchUseCase = updateBranchUseCase;
        this.toggleBranchStatusUseCase = toggleBranchStatusUseCase;
    }

    // ── Empresas ───────────────────────────────────────────────────────────────

    /**
     * Solo SUPER_ADMIN puede crear empresas
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<EmpresaDTO> crearEmpresa(@RequestBody EmpresaDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createEnterpriseUseCase.execute(request));
    }

    /**
     * Solo SUPER_ADMIN puede alterar contratos B2B de clientes
     */
    @PutMapping("/update/{empresaId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<EmpresaDTO> actualizarEmpresa(@PathVariable java.util.UUID empresaId,
                                                        @RequestBody EmpresaDTO request) {
        return ResponseEntity.ok(updateEnterpriseUseCase.execute(empresaId, request));
    }

    /**
     * SUPER_ADMIN puede listar todas las empresas
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<EmpresaDTO>> listarEmpresas() {
        return ResponseEntity.ok(listEnterprisesUseCase.execute());
    }

    // ── Sucursales ─────────────────────────────────────────────────────────────

    /**
     * SUPER_ADMIN y ADMIN pueden crear sucursales
     */
    @PostMapping("/create/branch/{empresaId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<SucursalDTO> crearSucursal(@PathVariable UUID empresaId,
                                                     @RequestBody SucursalDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createBranchUseCase.execute(empresaId, request));
    }

    /**
     * SUPER_ADMIN y ADMIN pueden listar sucursales
     */
    @GetMapping("list/branch/{empresaId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<List<SucursalDTO>> listarSucursalByEmpresa(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(listBranchesUseCase.executeById(empresaId));
    }

    @GetMapping("list/branch")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<List<SucursalDTO>> listarSucursales() {
        return ResponseEntity.ok(listBranchesUseCase.execute());
    }

    /**
     * Devuelve la sucursal del usuario autenticado según su scope_id del JWT.
     * Accesible para cualquier rol con scope SUCURSAL.
     */
    @GetMapping("/me/sucursal")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','ENCARGADO','CAJERO','MOZO','COCINERO')")
    public ResponseEntity<SucursalDTO> miSucursal(@AuthenticationPrincipal AudreyAuthPrincipal principal) {
        if (principal.scopeId() == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(getBranchByIdUseCase.execute(principal.scopeId()));
    }

    @PutMapping("/update/branch/{sucursalId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<SucursalDTO> actualizarSucursal(@PathVariable java.util.UUID sucursalId, @RequestBody SucursalDTO request) {
        return ResponseEntity.ok(updateBranchUseCase.execute(sucursalId, request));
    }

    // ── Toggle Estado ──────────────────────────────────────────────────────────

    @PutMapping("/toggle/{empresaId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> toggleEmpresa(@PathVariable UUID empresaId, @RequestParam boolean activate) {
        toggleEnterpriseStatusUseCase.execute(empresaId, activate);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/toggle/branch/{sucursalId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> toggleSucursal(@PathVariable UUID sucursalId, @RequestParam boolean activate) {
        toggleBranchStatusUseCase.execute(sucursalId, activate);
        return ResponseEntity.noContent().build();
    }

}
