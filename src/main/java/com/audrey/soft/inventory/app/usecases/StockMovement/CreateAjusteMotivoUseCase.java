package com.audrey.soft.inventory.app.usecases.StockMovement;
 
import com.audrey.soft.inventory.app.dtos.AjusteMotivoDTO;
import com.audrey.soft.inventory.infrastructure.persistence.entities.AjusteMotivoEntity;
import com.audrey.soft.inventory.infrastructure.persistence.repositories.SpringDataAjusteMotivoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class CreateAjusteMotivoUseCase {

    private final SpringDataAjusteMotivoRepository repo;

    public CreateAjusteMotivoUseCase(SpringDataAjusteMotivoRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public AjusteMotivoDTO execute(UUID sucursalId, AjusteMotivoDTO dto) {
        AjusteMotivoEntity entity = new AjusteMotivoEntity();
        entity.setId(UUID.randomUUID());
        entity.setSucursalId(sucursalId);
        entity.setNombre(dto.nombre());
        entity.setTipo(dto.tipo());
        entity.setActive(dto.active());

        AjusteMotivoEntity saved = repo.save(entity);
        return new AjusteMotivoDTO(saved.getId(), saved.getNombre(), saved.getTipo(), saved.isActive());
    }
}
