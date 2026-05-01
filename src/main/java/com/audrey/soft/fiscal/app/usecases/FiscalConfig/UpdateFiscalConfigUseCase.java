package com.audrey.soft.fiscal.app.usecases.FiscalConfig;

import com.audrey.soft.fiscal.app.dtos.FiscalConfigDTO;
import com.audrey.soft.fiscal.infrastructure.persistence.entities.FiscalConfigEntity;
import com.audrey.soft.fiscal.infrastructure.persistence.repositories.SpringDataFiscalConfigRepository;
import jakarta.transaction.Transactional;

import java.util.UUID;

public class UpdateFiscalConfigUseCase {

    private final SpringDataFiscalConfigRepository fiscalConfigRepository;

    public UpdateFiscalConfigUseCase(SpringDataFiscalConfigRepository fiscalConfigRepository) {
        this.fiscalConfigRepository = fiscalConfigRepository;
    }

    @Transactional
    public FiscalConfigDTO execute(UUID sucursalId, FiscalConfigDTO request) {
        FiscalConfigEntity entity = fiscalConfigRepository.findBySucursalId(sucursalId)
                .orElseGet(() -> FiscalConfigEntity.builder()
                        .sucursalId(sucursalId)
                        .fiscalSistemaId("INTERNO")
                        .build());

        if (request.rucEmpresa() != null)      entity.setRucEmpresa(request.rucEmpresa());
        if (request.razonSocial() != null)     entity.setRazonSocial(request.razonSocial());
        if (request.direccionFiscal() != null) entity.setDireccionFiscal(request.direccionFiscal());
        entity.setPreciosIncluyenImpuesto(request.preciosIncluyenImpuesto());

        FiscalConfigEntity saved = fiscalConfigRepository.save(entity);

        return new FiscalConfigDTO(
                saved.getSucursalId(),
                saved.getFiscalSistemaId(),
                saved.getMonedaCodigo(),
                saved.getRucEmpresa(),
                saved.getRazonSocial(),
                saved.getDireccionFiscal(),
                saved.getImpuestosDefault(),
                saved.isPreciosIncluyenImpuesto()
        );
    }
}
