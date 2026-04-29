package com.audrey.soft.design.application.usecases;

import com.audrey.soft.design.domain.ports.FlyerGeneradoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsUseCase {

    private final FlyerGeneradoRepositoryPort flyerGeneradoPort;

    public record ProductoPromovidoDTO(String nombre, long frecuencia) {}

    public List<ProductoPromovidoDTO> productosMasPromovidos(UUID empresaId, int limit) {
        List<String> allProductoIds = flyerGeneradoPort.findProductoIdsByEmpresaId(empresaId);
        return allProductoIds.stream()
                .collect(Collectors.groupingBy(id -> id, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .map(e -> new ProductoPromovidoDTO(e.getKey(), e.getValue()))
                .toList();
    }
}
