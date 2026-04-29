package com.audrey.soft.design.application.usecases;

import com.audrey.soft.design.application.dtos.FlyerGeneradoDTO;
import com.audrey.soft.design.application.dtos.FlyerRequestDTO;
import com.audrey.soft.design.application.dtos.FlyerVariacionesResponseDTO;
import com.audrey.soft.design.domain.ports.FlyerGeneradoRepositoryPort;
import com.audrey.soft.design.infrastructure.persistence.entities.FlyerGeneradoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistorialFlyerUseCase {

    private final FlyerGeneradoRepositoryPort flyerGeneradoPort;
    private final GenerarFlyerUseCase generarFlyerUseCase;

    public Page<FlyerGeneradoDTO> listarHistorial(UUID empresaId, int page) {
        PageRequest pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending());
        return flyerGeneradoPort.findByEmpresaId(empresaId, pageable)
                .map(this::toDTO);
    }

    public FlyerGeneradoDTO obtenerFlyer(UUID flyerId, UUID empresaId) {
        return flyerGeneradoPort.findByIdAndEmpresaId(flyerId, empresaId)
                .map(this::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Flyer no encontrado o no pertenece a esta empresa"));
    }

    public FlyerVariacionesResponseDTO regenerarFlyer(UUID flyerId, UUID empresaId, UUID usuarioId) {
        FlyerGeneradoEntity existing = flyerGeneradoPort.findByIdAndEmpresaId(flyerId, empresaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Flyer no encontrado o no pertenece a esta empresa"));

        List<String> productoIds = existing.getProductoIds();
        FlyerRequestDTO.ProductInfoDTO productInfo = new FlyerRequestDTO.ProductInfoDTO(
                productoIds.isEmpty() ? "Producto" : productoIds.get(0), 0.0, null);

        @SuppressWarnings("unchecked")
        Map<String, Object> paletteMap = existing.getPalette();
        FlyerRequestDTO.PaletteDTO palette = new FlyerRequestDTO.PaletteDTO(
                (String) paletteMap.getOrDefault("principal", "#6366f1"),
                (String) paletteMap.getOrDefault("secundario", "#818cf8"),
                (String) paletteMap.getOrDefault("contraste", "#f59e0b"));

        FlyerRequestDTO req = new FlyerRequestDTO(
                productInfo, null, palette,
                existing.getFormato(),
                existing.getPlantillaId() != null ? existing.getPlantillaId().toString() : null,
                existing.getTipoUso());

        return generarFlyerUseCase.generarFlyer(req, empresaId, usuarioId);
    }

    private FlyerGeneradoDTO toDTO(FlyerGeneradoEntity entity) {
        @SuppressWarnings("unchecked")
        Map<String, Object> paletteMap = entity.getPalette() != null ? entity.getPalette() : Map.of();
        FlyerRequestDTO.PaletteDTO palette = new FlyerRequestDTO.PaletteDTO(
                (String) paletteMap.getOrDefault("principal", "#6366f1"),
                (String) paletteMap.getOrDefault("secundario", "#818cf8"),
                (String) paletteMap.getOrDefault("contraste", "#f59e0b"));

        // ai_flyer_dto stored as lightweight reference — return empty variaciones for history listing
        FlyerVariacionesResponseDTO aiResult = new FlyerVariacionesResponseDTO(List.of());

        return new FlyerGeneradoDTO(
                entity.getId(),
                entity.getProductoIds(),
                entity.getTipoUso(),
                entity.getFormato(),
                palette,
                aiResult,
                entity.getPlantillaId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
