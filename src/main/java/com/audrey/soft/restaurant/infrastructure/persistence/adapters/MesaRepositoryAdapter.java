package com.audrey.soft.restaurant.infrastructure.persistence.adapters;

import com.audrey.soft.restaurant.domain.models.EstadoMesa;
import com.audrey.soft.restaurant.domain.models.Mesa;
import com.audrey.soft.restaurant.domain.ports.MesaRepositoryPort;
import com.audrey.soft.restaurant.infrastructure.persistence.entities.MesaEntity;
import com.audrey.soft.restaurant.infrastructure.persistence.repositories.SpringDataMesaRepository;
import com.audrey.soft.tenant.infrastructure.persistence.repositories.SpringDataSucursalRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class MesaRepositoryAdapter implements MesaRepositoryPort {

    private final SpringDataMesaRepository jpa;
    private final SpringDataSucursalRepository sucursalJpa;

    public MesaRepositoryAdapter(SpringDataMesaRepository jpa, SpringDataSucursalRepository sucursalJpa) {
        this.jpa = jpa;
        this.sucursalJpa = sucursalJpa;
    }

    @Override
    public Mesa save(Mesa mesa) {
        var sucursal = sucursalJpa.findById(mesa.getSucursalId())
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada: " + mesa.getSucursalId()));
        MesaEntity entity = MesaEntity.builder()
                .id(mesa.getId()).sucursal(sucursal).numero(mesa.getNumero())
                .capacidad(mesa.getCapacidad()).zona(mesa.getZona())
                .estado(mesa.getEstado()).active(mesa.isActive()).build();
        return toDomain(jpa.save(entity));
    }

    @Override
    public Optional<Mesa> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Mesa> findBySucursalId(UUID sucursalId) {
        return jpa.findBySucursalId(sucursalId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByNumeroAndSucursalId(int numero, UUID sucursalId) {
        return jpa.existsByNumeroAndSucursalId(numero, sucursalId);
    }

    @Override
    @Transactional
    public void updateEstado(UUID mesaId, EstadoMesa estado) {
        jpa.updateEstado(mesaId, estado);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }

    private Mesa toDomain(MesaEntity e) {
        return new Mesa(e.getId(), e.getSucursal().getId(), e.getNumero(),
                e.getCapacidad(), e.getEstado(), e.isActive(), e.getZona(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
