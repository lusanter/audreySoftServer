package com.audrey.soft.billing.infrastructure.persistence.adapters;

import com.audrey.soft.billing.domain.models.MetodoCobro;
import com.audrey.soft.billing.domain.ports.MetodoCobroRepositoryPort;
import com.audrey.soft.billing.infrastructure.persistence.entities.MetodoCobroEntity;
import com.audrey.soft.billing.infrastructure.persistence.repositories.SpringDataMetodoCobroRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class MetodoCobroRepositoryAdapter implements MetodoCobroRepositoryPort {

    private final SpringDataMetodoCobroRepository jpa;

    public MetodoCobroRepositoryAdapter(SpringDataMetodoCobroRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public MetodoCobro save(MetodoCobro m) {
        if (m.getId() != null && jpa.existsById(m.getId())) {
            // Update directo sin pasar por merge
            jpa.updateFields(m.getId(), m.getNombre(), m.getCodigo(), m.getImagenUrl(), m.isActivo());
            return jpa.findById(m.getId()).map(this::toDomain).orElseThrow();
        }
        // Insert
        MetodoCobroEntity entity = new MetodoCobroEntity();
        entity.setId(m.getId());
        entity.setSucursalId(m.getSucursalId());
        entity.setNombre(m.getNombre());
        entity.setCodigo(m.getCodigo());
        entity.setImagenUrl(m.getImagenUrl());
        entity.setActivo(m.isActivo());
        return toDomain(jpa.save(entity));
    }

    @Override
    public Optional<MetodoCobro> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<MetodoCobro> findDisponiblesBySucursal(UUID sucursalId) {
        return jpa.findDisponiblesBySucursal(sucursalId).stream().map(this::toDomain).toList();
    }

    private MetodoCobro toDomain(MetodoCobroEntity e) {
        return new MetodoCobro(e.getId(), e.getSucursalId(), e.getNombre(),
                e.getCodigo(), e.getImagenUrl(), e.isActivo(), e.getCreatedAt());
    }
}
