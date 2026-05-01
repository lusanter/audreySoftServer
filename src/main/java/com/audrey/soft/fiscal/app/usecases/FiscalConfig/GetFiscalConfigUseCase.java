package com.audrey.soft.fiscal.app.usecases.FiscalConfig;

import com.audrey.soft.fiscal.app.dtos.FiscalConfigDTO;
import com.audrey.soft.fiscal.infrastructure.persistence.repositories.SpringDataFiscalConfigRepository;

import java.util.UUID;

public class GetFiscalConfigUseCase {

    private final SpringDataFiscalConfigRepository fiscalConfigRepository;

    public GetFiscalConfigUseCase(SpringDataFiscalConfigRepository fiscalConfigRepository) {
        this.fiscalConfigRepository = fiscalConfigRepository;
    }

    public FiscalConfigDTO execute(UUID sucursalId) {
        return fiscalConfigRepository.findBySucursalId(sucursalId)
                .map(e -> new FiscalConfigDTO(
                        e.getSucursalId(),
                        e.getFiscalSistemaId(),
                        e.getMonedaCodigo(),
                        e.getRucEmpresa(),
                        e.getRazonSocial(),
                        e.getDireccionFiscal(),
                        e.getImpuestosDefault(),
                        e.isPreciosIncluyenImpuesto()
                ))
                .orElse(null);
    }
}
