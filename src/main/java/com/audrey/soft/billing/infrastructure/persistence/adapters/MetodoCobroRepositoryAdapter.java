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
        MetodoCobroEntity entity = MetodoCobroEntity.builder()
                .id(m.getId()).sucursalId(m.getSucursalId())
                .nombre(m.getNombre()).codigo(m.getCodigo()).activo(m.isActivo()).build();
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
                e.getCodigo(), e.isActivo(), e.getCreatedAt());
    }
}
