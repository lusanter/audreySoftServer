package com.audrey.soft.design.domain.ports;

import com.audrey.soft.design.application.dtos.AiFlyerDTO;
import com.audrey.soft.design.application.dtos.FlyerRequestDTO;
import com.audrey.soft.design.application.dtos.FlyerVariacionesResponseDTO;

import java.util.List;
import java.util.Map;

public interface DesignEnginePort {
    AiFlyerDTO generateFlyer(FlyerRequestDTO request);
    FlyerVariacionesResponseDTO generateFlyerV2(FlyerRequestDTO request);
    List<Map<String, Object>> listarRecursos(String tipo);
    Map<String, Object> indexarRecurso(Map<String, Object> request);
    Map<String, Object> actualizarRecurso(String id, Map<String, Object> request);
    void eliminarRecurso(String id);
}
