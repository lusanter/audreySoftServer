package com.audrey.soft.billing.app.usecases.MetodoCobro;

import com.audrey.soft.billing.app.dtos.MetodoCobroDTO;
import com.audrey.soft.billing.app.dtos.MetodoCobroRequestDTO;
import com.audrey.soft.billing.domain.models.MetodoCobro;
import com.audrey.soft.billing.domain.ports.MetodoCobroRepositoryPort;

import java.util.UUID;

public class CreateMetodoCobroUseCase {
    private final MetodoCobroRepositoryPort repo;

    public CreateMetodoCobroUseCase(MetodoCobroRepositoryPort repo) {
        this.repo = repo;
    }

    public MetodoCobroDTO execute(UUID sucursalId, MetodoCobroRequestDTO req) {
        MetodoCobro m = new MetodoCobro(
                null, sucursalId,
                req.nombre(), req.codigo(), req.imagenUrl(), true, null
        );
        MetodoCobro saved = repo.save(m);
        return toDTO(saved);
    }

    private MetodoCobroDTO toDTO(MetodoCobro m) {
        return new MetodoCobroDTO(m.getId(), m.getSucursalId(), m.getNombre(),
                m.getCodigo(), m.getImagenUrl(), m.isActivo());
    }
}
