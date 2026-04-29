package com.audrey.soft.billing.app.usecases.MetodoCobro;

import com.audrey.soft.billing.app.dtos.MetodoCobroDTO;
import com.audrey.soft.billing.app.dtos.MetodoCobroRequestDTO;
import com.audrey.soft.billing.domain.models.MetodoCobro;
import com.audrey.soft.billing.domain.ports.MetodoCobroRepositoryPort;

import java.util.UUID;

public class UpdateMetodoCobroUseCase {
    private final MetodoCobroRepositoryPort repo;

    public UpdateMetodoCobroUseCase(MetodoCobroRepositoryPort repo) {
        this.repo = repo;
    }

    public MetodoCobroDTO execute(UUID id, MetodoCobroRequestDTO req) {
        MetodoCobro m = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Método de cobro no encontrado"));
        m.setNombre(req.nombre());
        m.setCodigo(req.codigo());
        m.setImagenUrl(req.imagenUrl());
        m.setActivo(req.activo());
        MetodoCobro saved = repo.save(m);
        return new MetodoCobroDTO(saved.getId(), saved.getSucursalId(), saved.getNombre(),
                saved.getCodigo(), saved.getImagenUrl(), saved.isActivo());
    }
}
