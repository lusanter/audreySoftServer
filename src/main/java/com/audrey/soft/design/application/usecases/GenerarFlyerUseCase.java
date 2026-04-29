package com.audrey.soft.design.application.usecases;

import com.audrey.soft.design.application.dtos.FlyerRequestDTO;
import com.audrey.soft.design.application.dtos.FlyerVariacionesResponseDTO;
import com.audrey.soft.design.domain.ports.DesignEnginePort;
import com.audrey.soft.design.domain.ports.FlyerGeneradoRepositoryPort;
import com.audrey.soft.design.infrastructure.persistence.entities.FlyerGeneradoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerarFlyerUseCase {

    private final DesignEnginePort designEnginePort;
    private final FlyerGeneradoRepositoryPort flyerGeneradoPort;

    public FlyerVariacionesResponseDTO generarFlyer(FlyerRequestDTO req, UUID empresaId, UUID usuarioId) {
        FlyerVariacionesResponseDTO result = designEnginePort.generateFlyerV2(req);

        List<String> productoIds = req.productosNormalizados().stream()
                .map(FlyerRequestDTO.ProductInfoDTO::nombre)
                .toList();

        // Build palette map manually — avoids ObjectMapper dependency
        FlyerRequestDTO.PaletteDTO p = req.palette();
        Map<String, Object> paletteMap = Map.of(
                "principal",  p.principal()  != null ? p.principal()  : "#6366f1",
                "secundario", p.secundario() != null ? p.secundario() : "#818cf8",
                "contraste",  p.contraste()  != null ? p.contraste()  : "#f59e0b"
        );

        // Store result as empty map — full DTO is reconstructable from productoIds + request
        // The JSONB field stores a lightweight reference; full result is returned to client directly
        Map<String, Object> resultMap = Map.of("variaciones", List.of());

        FlyerGeneradoEntity entity = FlyerGeneradoEntity.builder()
                .empresaId(empresaId)
                .usuarioId(usuarioId)
                .productoIds(productoIds)
                .tipoUso(req.tipoUso())
                .formato(req.formato())
                .palette(paletteMap)
                .aiFlyerDto(resultMap)
                .plantillaId(req.plantillaId() != null ? UUID.fromString(req.plantillaId()) : null)
                .build();

        flyerGeneradoPort.save(entity);
        log.debug("[FLYER] Guardado historial empresaId={}", empresaId);
        return result;
    }
}
