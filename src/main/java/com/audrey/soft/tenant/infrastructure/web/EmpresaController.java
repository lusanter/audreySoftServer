package com.audrey.soft.tenant.infrastructure.web;

import com.audrey.soft.tenant.app.dtos.EmpresaDTO;
import com.audrey.soft.tenant.app.dtos.SucursalDTO;
import com.audrey.soft.tenant.app.usecases.CreateEnterpriseUseCase;
import com.audrey.soft.tenant.app.usecases.CreateBranchUseCase;
import com.audrey.soft.tenant.app.usecases.ListEnterprisesUseCase;
import com.audrey.soft.tenant.app.usecases.ListBranchesUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/empresas")
public class EmpresaController {

    private final CreateEnterpriseUseCase createEnterpriseUseCase;
    private final ListEnterprisesUseCase listEnterprisesUseCase;
    private final CreateBranchUseCase createBranchUseCase;
    private final ListBranchesUseCase listBranchesUseCase;

    public EmpresaController(CreateEnterpriseUseCase createEnterpriseUseCase,
                             ListEnterprisesUseCase listEnterprisesUseCase,
                             CreateBranchUseCase createBranchUseCase,
                             ListBranchesUseCase listBranchesUseCase) {
        this.createEnterpriseUseCase = createEnterpriseUseCase;
        this.listEnterprisesUseCase = listEnterprisesUseCase;
        this.createBranchUseCase = createBranchUseCase;
        this.listBranchesUseCase = listBranchesUseCase;
    }

    // ── Empresas ───────────────────────────────────────────────────────────────

    /** Solo SUPER_ADMIN puede crear empresas */
    @PostMapping("/create")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<EmpresaDTO> crearEmpresa(@RequestBody EmpresaDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createEnterpriseUseCase.execute(request));
    }

    /** SUPER_ADMIN puede listar todas las empresas */
    @GetMapping("/list")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<EmpresaDTO>> listarEmpresas() {
        return ResponseEntity.ok(listEnterprisesUseCase.execute());
    }

    // ── Sucursales ─────────────────────────────────────────────────────────────

    /** SUPER_ADMIN y ADMIN pueden crear sucursales */
    @PostMapping("/create/branch/{empresaId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<SucursalDTO> crearSucursal(@PathVariable UUID empresaId,
                                                     @RequestBody SucursalDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createBranchUseCase.execute(empresaId, request));
    }

    /** SUPER_ADMIN y ADMIN pueden listar sucursales */
    @GetMapping("list/branch/{empresaId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<List<SucursalDTO>> listarSucursales(@PathVariable UUID empresaId) {
        return ResponseEntity.ok(listBranchesUseCase.execute(empresaId));
    }
}
