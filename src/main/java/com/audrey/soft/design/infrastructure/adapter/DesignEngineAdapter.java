package com.audrey.soft.design.infrastructure.adapter;

import com.audrey.soft.design.application.dtos.AiFlyerDTO;
import com.audrey.soft.design.application.dtos.FlyerRequestDTO;
import com.audrey.soft.design.application.dtos.FlyerVariacionesResponseDTO;
import com.audrey.soft.design.domain.ports.DesignEnginePort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DesignEngineAdapter implements DesignEnginePort {

    private final RestClient restClient;

    @Value("${audrey.design-engine.url:http://localhost:8081}")
    private String engineUrl;

    @Override
    public AiFlyerDTO generateFlyer(FlyerRequestDTO request) {
        return restClient.post()
                .uri(engineUrl + "/api/v1/design/flyer")
                .body(request)
                .retrieve()
                .body(AiFlyerDTO.class);
    }

    @Override
    @CircuitBreaker(name = "designEngine", fallbackMethod = "fallbackGenerateV2")
    public FlyerVariacionesResponseDTO generateFlyerV2(FlyerRequestDTO request) {
        GenerateBody body = new GenerateBody(
                request.productosNormalizados(),
                request.palette(),
                request.formato(),
                request.tipoUso()
        );
        return restClient.post()
                .uri(engineUrl + "/api/v1/design/generate")
                .body(body)
                .retrieve()
                .body(FlyerVariacionesResponseDTO.class);
    }

    @SuppressWarnings("unused")
    private FlyerVariacionesResponseDTO fallbackGenerateV2(FlyerRequestDTO request, Throwable t) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Design Engine no disponible. Intenta en unos momentos.");
    }

    @Override
    public List<Map<String, Object>> listarRecursos(String tipo) {
        String uri = engineUrl + "/api/v1/design/recursos"
                + (tipo != null ? "?tipo=" + tipo : "");
        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public Map<String, Object> indexarRecurso(Map<String, Object> request) {
        return restClient.post()
                .uri(engineUrl + "/api/v1/design/recursos")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public Map<String, Object> actualizarRecurso(String id, Map<String, Object> request) {
        return restClient.put()
                .uri(engineUrl + "/api/v1/design/recursos/" + id)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public void eliminarRecurso(String id) {
        restClient.delete()
                .uri(engineUrl + "/api/v1/design/recursos/" + id)
                .retrieve()
                .toBodilessEntity();
    }

    private record GenerateBody(
            List<FlyerRequestDTO.ProductInfoDTO> productos,
            FlyerRequestDTO.PaletteDTO palette,
            String formato,
            String tipoUso
    ) {}
}
