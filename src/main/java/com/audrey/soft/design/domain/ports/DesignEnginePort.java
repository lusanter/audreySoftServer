package com.audrey.soft.design.domain.ports;

import com.audrey.soft.design.application.dtos.AiFlyerDTO;
import com.audrey.soft.design.application.dtos.FlyerRequestDTO;

public interface DesignEnginePort {
    AiFlyerDTO generateFlyer(FlyerRequestDTO request);
}
