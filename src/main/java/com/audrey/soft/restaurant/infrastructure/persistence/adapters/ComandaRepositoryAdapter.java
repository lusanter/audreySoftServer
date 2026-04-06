package com.audrey.soft.restaurant.infrastructure.persistence.adapters;

import com.audrey.soft.restaurant.domain.models.*;
import com.audrey.soft.restaurant.domain.ports.ComandaRepositoryPort;
import com.audrey.soft.restaurant.infrastructure.persistence.entities.ComandaEntity;
import com.audrey.soft.restaurant.infrastructure.persistence.entities.ComandaItemEntity;
import com.audrey.soft.restaurant.infrastructure.persistence.repositories.SpringDataComandaRepository;
import com.audrey.soft.tenant.infrastructure.persistence.repositories.SpringDataSucursalRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ComandaRepositoryAdapter implements ComandaRepositoryPort {

    private final SpringDataComandaRepository jpa;
    private final SpringDataSucursalRepository sucursalJpa;

    public ComandaRepositoryAdapter(SpringDataComandaRepository jpa, SpringDataSucursalRepository sucursalJpa) {
        this.jpa = jpa;
        this.sucursalJpa = sucursalJpa;
    }

    @Override
    public Comanda save(Comanda comanda) {
        var sucursal = sucursalJpa.findById(comanda.getSucursalId())
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada: " + comanda.getSucursalId()));

        ComandaEntity entity = jpa.findById(comanda.getId() != null ? comanda.getId() : UUID.randomUUID())
                .orElse(ComandaEntity.builder().sucursal(sucursal).build());

        entity.setSucursal(sucursal);
        entity.setMesaId(comanda.getMesaId());
        entity.setClienteId(comanda.getClienteId());
        entity.setEstado(comanda.getEstado());
        entity.setTotal(comanda.getTotal());
        entity.setNotas(comanda.getNotas());
        entity.setClosedAt(comanda.getClosedAt());

        if (comanda.getItems() != null) {
            if (entity.getItems() == null) entity.setItems(new ArrayList<>());
            entity.getItems().clear();
            comanda.getItems().forEach(i -> {
                ComandaItemEntity itemEntity = ComandaItemEntity.builder()
                        .id(i.getId()).comanda(entity).productoId(i.getProductoId())
                        .cantidad(i.getCantidad()).precioUnitario(i.getPrecioUnitario())
                        .notas(i.getNotas()).subCuenta(i.getSubCuenta()).estado(i.getEstado()).build();
                entity.getItems().add(itemEntity);
            });
        }

        return toDomain(jpa.save(entity));
    }

    @Override
    public Optional<Comanda> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Comanda> findBySucursalId(UUID sucursalId) {
        return jpa.findBySucursalId(sucursalId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Comanda> findBySucursalIdAndEstado(UUID sucursalId, EstadoComanda estado) {
        return jpa.findBySucursalIdAndEstado(sucursalId, estado).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Comanda> findOpenByMesaId(UUID mesaId) {
        return jpa.findOpenByMesaId(mesaId).map(this::toDomain);
    }

    private Comanda toDomain(ComandaEntity e) {
        List<ComandaItem> items = e.getItems() != null ? e.getItems().stream().map(i ->
                new ComandaItem(i.getId(), e.getId(), i.getProductoId(), i.getCantidad(),
                        i.getPrecioUnitario(), i.getNotas(), i.getSubCuenta(), i.getEstado(), i.getCreatedAt(), i.getUpdatedAt())
        ).toList() : new ArrayList<>();
        return new Comanda(e.getId(), e.getSucursal().getId(), e.getMesaId(), e.getClienteId(),
                e.getEstado(), e.getTotal(), e.getNotas(), items, e.getCreatedAt(), e.getClosedAt());
    }
}
