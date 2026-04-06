package com.audrey.soft.inventory.infrastructure.persistence.mappers;

import com.audrey.soft.inventory.domain.models.Cliente;
import com.audrey.soft.inventory.infrastructure.persistence.entities.ClienteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClienteEntityMapper {
    @Mapping(target = "sucursalId", source = "sucursal.id")
    Cliente toDomain(ClienteEntity entity);

    @Mapping(target = "sucursal", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClienteEntity toEntity(Cliente domain);
}
