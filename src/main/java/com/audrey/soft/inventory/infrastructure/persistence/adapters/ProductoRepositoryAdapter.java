package com.audrey.soft.inventory.infrastructure.persistence.adapters;

import com.audrey.soft.inventory.domain.models.Producto;
import com.audrey.soft.inventory.domain.ports.ProductoRepositoryPort;
import com.audrey.soft.inventory.infrastructure.persistence.entities.ProductoEntity;
import com.audrey.soft.inventory.infrastructure.persistence.mappers.ProductoEntityMapper;
import com.audrey.soft.inventory.infrastructure.persistence.repositories.SpringDataCategoriaRepository;
import com.audrey.soft.inventory.infrastructure.persistence.repositories.SpringDataProductoRepository;
import com.audrey.soft.tenant.infrastructure.persistence.repositories.SpringDataSucursalRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProductoRepositoryAdapter implements ProductoRepositoryPort {

    private final SpringDataProductoRepository jpa;
    private final SpringDataSucursalRepository sucursalJpa;
    private final SpringDataCategoriaRepository categoriaJpa;
    private final ProductoEntityMapper mapper;

    public ProductoRepositoryAdapter(SpringDataProductoRepository jpa,
                                     SpringDataSucursalRepository sucursalJpa,
                                     SpringDataCategoriaRepository categoriaJpa,
                                     ProductoEntityMapper mapper) {
        this.jpa = jpa;
        this.sucursalJpa = sucursalJpa;
        this.categoriaJpa = categoriaJpa;
        this.mapper = mapper;
    }

    @Override
    public Producto save(Producto producto) {
        var sucursal = sucursalJpa.findById(producto.getSucursalId())
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada: " + producto.getSucursalId()));
        var categoria = producto.getCategoriaId() != null
                ? categoriaJpa.findById(producto.getCategoriaId()).orElse(null)
                : null;
        ProductoEntity entity = ProductoEntity.builder()
                .id(producto.getId())
                .sucursal(sucursal)
                .categoria(categoria)
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .precioCosto(producto.getPrecioCosto())
                .stockActual(producto.getStockActual())
                .stockMinimo(producto.getStockMinimo())
                .unidad(producto.getUnidad())
                .active(producto.isActive())
                .controlStock(producto.isControlStock())
                .imagenUrl(producto.getImagenUrl())
                .build();
        return mapper.toDomain(jpa.save(entity));
    }

    @Override
    public Optional<Producto> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Producto> findBySucursalId(UUID sucursalId) {
        return jpa.findBySucursalId(sucursalId).stream().map(mapper::toDomain).toList();
    }

    @Override
    @Transactional
    public void decrementarStock(UUID productoId, BigDecimal cantidad) {
        jpa.decrementarStock(productoId, cantidad);
    }

    @Override
    @Transactional
    public void incrementarStockYActualizarCosto(UUID productoId, BigDecimal cantidad, BigDecimal precioCosto) {
        jpa.incrementarStockYActualizarCosto(productoId, cantidad, precioCosto);
    }
}
