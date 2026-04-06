package com.audrey.soft.restaurant.app.mappers;

import com.audrey.soft.restaurant.app.dtos.MesaDTO;
import com.audrey.soft.restaurant.domain.models.Mesa;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MesaMapper {
    MesaDTO toDto(Mesa mesa);
    Mesa toDomain(MesaDTO dto);
}
