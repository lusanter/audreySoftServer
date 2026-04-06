package com.audrey.soft.restaurant.app.mappers;

import com.audrey.soft.restaurant.app.dtos.ComandaDTO;
import com.audrey.soft.restaurant.domain.models.Comanda;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ComandaMapper {
    ComandaDTO toDto(Comanda comanda);
    Comanda toDomain(ComandaDTO dto);
}
