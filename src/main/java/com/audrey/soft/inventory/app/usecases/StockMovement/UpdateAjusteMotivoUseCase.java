package com.audrey.soft.inventory.app.usecases.StockMovement;

import com.audrey.soft.inventory.app.dtos.AjusteMotivoDTO;
import com.audrey.soft.inventory.infrastructure.persistence.entities.AjusteMotivoEntity;
import com.audrey.soft.inventory.infrastructure.persistence.repositories.SpringDataAjusteMotivoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class UpdateAjusteMotivoUseCase {

    private final SpringDataAjusteMotivoRepository repo;

    public UpdateAjusteMotivoUseCase(SpringDataAjusteMotivoRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public AjusteMotivoDTO execute(UUID motivoId, AjusteMotivoDTO dto) {
        AjusteMotivoEntity entity = repo.findById(motivoId)
                .orElseThrow(() -> new RuntimeException("Motivo de ajuste no encontrado: " + motivoId));

        entity.setNombre(dto.nombre());
        entity.setTipo(dto.tipo());
        entity.setActive(dto.active());

        AjusteMotivoEntity updated = repo.save(entity);
        return new AjusteMotivoDTO(updated.getId(), updated.getNombre(), updated.getTipo(), updated.isActive());
    }
}
