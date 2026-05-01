package com.audrey.soft.fiscal.infrastructure.web;

import com.audrey.soft.fiscal.app.dtos.FiscalSistemaDTO;
import com.audrey.soft.fiscal.app.dtos.ImpuestoTipoDTO;
import com.audrey.soft.fiscal.infrastructure.persistence.entities.FiscalSistemaEntity;
import com.audrey.soft.fiscal.infrastructure.persistence.entities.ImpuestoTipoEntity;
import com.audrey.soft.fiscal.infrastructure.persistence.repositories.SpringDataFiscalSistemaRepository;
import com.audrey.soft.fiscal.infrastructure.persistence.repositories.SpringDataImpuestoTipoRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Endpoints de administración global del catálogo fiscal.
 * Solo accesibles por SUPER_ADMIN — no requieren sucursalId.
 */
@RestController
@RequestMapping("/api/v1/fiscal-admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class FiscalAdminController {

    private final SpringDataFiscalSistemaRepository sistemaRepo;
    private final SpringDataImpuestoTipoRepository impuestoRepo;

    public FiscalAdminController(SpringDataFiscalSistemaRepository sistemaRepo,
                                 SpringDataImpuestoTipoRepository impuestoRepo) {
        this.sistemaRepo = sistemaRepo;
        this.impuestoRepo = impuestoRepo;
    }

    // ── Sistemas Fiscales ──────────────────────────────────────────────────────

    @GetMapping("/sistemas")
    public ResponseEntity<List<FiscalSistemaDTO>> listarSistemas() {
        var list = sistemaRepo.findAll().stream().map(this::toSistemaDTO).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/sistemas/{id}")
    public ResponseEntity<FiscalSistemaDTO> getSistema(@PathVariable String id) {
        return sistemaRepo.findById(id)
                .map(e -> ResponseEntity.ok(toSistemaDTO(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/sistemas")
    @Transactional
    public ResponseEntity<FiscalSistemaDTO> crearSistema(@RequestBody FiscalSistemaDTO dto) {
        if (sistemaRepo.existsById(dto.id())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        FiscalSistemaEntity entity = FiscalSistemaEntity.builder()
                .id(dto.id().toUpperCase())
                .nombre(dto.nombre())
                .paisCodigo(dto.paisCodigo())
                .monedaDefault(dto.monedaDefault())
                .serieFormato(dto.serieFormato())
                .serieRegex(dto.serieRegex())
                .correlativoPadding(dto.correlativoPadding() > 0 ? dto.correlativoPadding() : 8)
                .separador(dto.separador() != null ? dto.separador() : "-")
                .activo(dto.activo())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(toSistemaDTO(sistemaRepo.save(entity)));
    }

    @PutMapping("/sistemas/{id}")
    @Transactional
    public ResponseEntity<FiscalSistemaDTO> actualizarSistema(@PathVariable String id,
                                                               @RequestBody FiscalSistemaDTO dto) {
        return sistemaRepo.findById(id).map(entity -> {
            entity.setNombre(dto.nombre());
            entity.setPaisCodigo(dto.paisCodigo());
            entity.setMonedaDefault(dto.monedaDefault());
            entity.setSerieFormato(dto.serieFormato());
            entity.setSerieRegex(dto.serieRegex());
            if (dto.correlativoPadding() > 0) entity.setCorrelativoPadding(dto.correlativoPadding());
            if (dto.separador() != null) entity.setSeparador(dto.separador());
            entity.setActivo(dto.activo());
            return ResponseEntity.ok(toSistemaDTO(sistemaRepo.save(entity)));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── Tipos de Impuesto ──────────────────────────────────────────────────────

    @GetMapping("/impuestos")
    public ResponseEntity<List<ImpuestoTipoDTO>> listarImpuestos(
            @RequestParam(required = false) String sistemaId) {
        var list = impuestoRepo.findAll().stream()
                .filter(i -> sistemaId == null || sistemaId.equals(i.getFiscalSistemaId()))
                .map(this::toImpuestoDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/impuestos")
    @Transactional
    public ResponseEntity<ImpuestoTipoDTO> crearImpuesto(@RequestBody ImpuestoTipoDTO dto) {
        if (impuestoRepo.existsById(dto.id())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        ImpuestoTipoEntity entity = ImpuestoTipoEntity.builder()
                .id(dto.id().toUpperCase())
                .fiscalSistemaId(dto.fiscalSistemaId())
                .codigo(dto.codigo())
                .nombre(dto.nombre())
                .tasaDefault(dto.tasaDefault())
                .tipoCalculo(dto.tipoCalculo() != null ? dto.tipoCalculo() : "PORCENTAJE")
                .activo(true)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(toImpuestoDTO(impuestoRepo.save(entity)));
    }

    @PutMapping("/impuestos/{id}")
    @Transactional
    public ResponseEntity<ImpuestoTipoDTO> actualizarImpuesto(@PathVariable String id,
                                                               @RequestBody ImpuestoTipoDTO dto) {
        return impuestoRepo.findById(id).map(entity -> {
            entity.setCodigo(dto.codigo());
            entity.setNombre(dto.nombre());
            if (dto.tasaDefault() != null) entity.setTasaDefault(dto.tasaDefault());
            if (dto.tipoCalculo() != null) entity.setTipoCalculo(dto.tipoCalculo());
            entity.setActivo(dto.activo() != null ? dto.activo() : entity.isActivo());
            return ResponseEntity.ok(toImpuestoDTO(impuestoRepo.save(entity)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/impuestos/{id}")
    @Transactional
    public ResponseEntity<Void> toggleImpuesto(@PathVariable String id) {
        return impuestoRepo.findById(id).map(entity -> {
            entity.setActivo(!entity.isActivo());
            impuestoRepo.save(entity);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── Mappers ────────────────────────────────────────────────────────────────

    private FiscalSistemaDTO toSistemaDTO(FiscalSistemaEntity e) {
        return new FiscalSistemaDTO(e.getId(), e.getNombre(), e.getPaisCodigo(),
                e.getMonedaDefault(), e.getSerieFormato(), e.getSerieRegex(),
                e.getCorrelativoPadding(), e.getSeparador(), e.isActivo());
    }

    private ImpuestoTipoDTO toImpuestoDTO(ImpuestoTipoEntity e) {
        return new ImpuestoTipoDTO(e.getId(), e.getFiscalSistemaId(), e.getCodigo(),
                e.getNombre(), e.getTasaDefault(), e.getTipoCalculo(), e.isActivo());
    }
}
