package com.audrey.soft.billing.infrastructure.persistence.adapters;

import com.audrey.soft.billing.domain.models.ComprobanteSerie;
import com.audrey.soft.billing.domain.ports.ComprobanteSerieRepositoryPort;
import com.audrey.soft.billing.infrastructure.persistence.entities.ComprobanteSerieEntity;
import com.audrey.soft.billing.infrastructure.persistence.repositories.SpringDataComprobanteSerieRepository;
import com.audrey.soft.tenant.infrastructure.persistence.repositories.SpringDataSucursalRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ComprobanteSerieRepositoryAdapter implements ComprobanteSerieRepositoryPort {

    private final SpringDataComprobanteSerieRepository jpa;
    private final SpringDataSucursalRepository sucursalJpa;

    public ComprobanteSerieRepositoryAdapter(SpringDataComprobanteSerieRepository jpa,
                                             SpringDataSucursalRepository sucursalJpa) {
        this.jpa = jpa;
        this.sucursalJpa = sucursalJpa;
    }

    @Override
    public ComprobanteSerie save(ComprobanteSerie serie) {
        var sucursal = sucursalJpa.findById(serie.getSucursalId())
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada: " + serie.getSucursalId()));
        ComprobanteSerieEntity entity = ComprobanteSerieEntity.builder()
                .id(serie.getId())
                .sucursal(sucursal)
                .tipoComprobante(serie.getTipoComprobante())
                .serie(serie.getSerie())
                .correlativoActual(serie.getCorrelativoActual())
                .correlativoMax(serie.getCorrelativoMax())
                .activo(serie.isActivo())
                .build();
        return toDomain(jpa.save(entity));
    }

    @Override
    public Optional<ComprobanteSerie> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<ComprobanteSerie> findBySucursalId(UUID sucursalId) {
        return jpa.findBySucursalId(sucursalId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<ComprobanteSerie> findActivaByTipoAndSucursal(String tipoComprobante, UUID sucursalId) {
        return jpa.findByTipoComprobanteAndSucursalIdAndActivoTrue(tipoComprobante, sucursalId)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public int incrementarCorrelativo(UUID serieId) {
        // Lock pesimista: bloquea la fila hasta que termine la transacción
        ComprobanteSerieEntity serie = jpa.findByIdWithLock(serieId)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada: " + serieId));

        if (serie.getCorrelativoActual() >= serie.getCorrelativoMax())
            throw new IllegalStateException("La serie " + serie.getSerie() + " ha alcanzado el correlativo máximo. Crea una nueva serie.");

        jpa.incrementarCorrelativo(serieId);
        return serie.getCorrelativoActual() + 1;
    }

    private ComprobanteSerie toDomain(ComprobanteSerieEntity e) {
        return new ComprobanteSerie(e.getId(), e.getSucursal().getId(), e.getTipoComprobante(),
                e.getSerie(), e.getCorrelativoActual(), e.getCorrelativoMax(),
                e.isActivo(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
