package com.audrey.soft.inventory.app.mappers;

import com.audrey.soft.inventory.app.dtos.ClienteDTO;
import com.audrey.soft.inventory.domain.models.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClienteMapper {
    ClienteDTO toDto(Cliente cliente);
    Cliente toDomain(ClienteDTO dto);
}
