package com.audrey.soft.design.infrastructure.adapter;

import com.audrey.soft.design.application.dtos.AiFlyerDTO;
import com.audrey.soft.design.application.dtos.FlyerRequestDTO;
import com.audrey.soft.design.domain.ports.DesignEnginePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

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
}
