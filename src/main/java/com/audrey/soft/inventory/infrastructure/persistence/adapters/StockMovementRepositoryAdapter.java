package com.audrey.soft.inventory.infrastructure.persistence.adapters;

import com.audrey.soft.inventory.app.dtos.StockMovementDTO;
import com.audrey.soft.inventory.domain.ports.StockMovementRepositoryPort;
import com.audrey.soft.inventory.infrastructure.persistence.entities.StockMovementEntity;
import com.audrey.soft.inventory.infrastructure.persistence.repositories.SpringDataAjusteMotivoRepository;
import com.audrey.soft.inventory.infrastructure.persistence.repositories.SpringDataProductoRepository;
import com.audrey.soft.inventory.infrastructure.persistence.repositories.SpringDataStockMovementRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class StockMovementRepositoryAdapter implements StockMovementRepositoryPort {

    private final SpringDataStockMovementRepository jpa;
    private final SpringDataProductoRepository productoJpa;
    private final SpringDataAjusteMotivoRepository motivoJpa;

    public StockMovementRepositoryAdapter(SpringDataStockMovementRepository jpa,
                                          SpringDataProductoRepository productoJpa,
                                          SpringDataAjusteMotivoRepository motivoJpa) {
        this.jpa = jpa;
        this.productoJpa = productoJpa;
        this.motivoJpa = motivoJpa;
    }

    @Override
    public List<StockMovementDTO> findBySucursalId(UUID sucursalId) {
        return jpa.findBySucursalId(sucursalId).stream()
                .map(sm -> new StockMovementDTO(
                        sm.getId(),
                        sm.getProducto().getId(),
                        sm.getProducto().getNombre(),
                        sm.getTipo(),
                        sm.getCantidad(),
                        sm.getPrecioCosto(),
                        sm.getReferenciaId(),
                        sm.getAjusteMotivo() != null ? sm.getAjusteMotivo().getNombre() : null,
                        sm.getAjusteMotivo() != null ? sm.getAjusteMotivo().getTipo() : (sm.getTipo().equals("ENTRADA") ? "INCREMENTO" : "DECREMENTO"),
                        sm.getNota(),
                        sm.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public void save(UUID productoId, String tipo, BigDecimal cantidad, BigDecimal precioCosto,
                     UUID referenciaId, UUID motivoId, String nota, LocalDateTime createdAt) {
        var producto = productoJpa.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoId));

        var builder = StockMovementEntity.builder()
                .producto(producto)
                .tipo(tipo)
                .cantidad(cantidad)
                .precioCosto(precioCosto)
                .referenciaId(referenciaId)
                .nota(nota)
                .createdAt(createdAt);

        if (motivoId != null) {
            var motivo = motivoJpa.findById(motivoId)
                    .orElseThrow(() -> new RuntimeException("Motivo de ajuste no encontrado: " + motivoId));
            builder.ajusteMotivo(motivo);
        }

        jpa.save(builder.build());
    }
}
