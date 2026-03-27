package com.audrey.soft.auth.infrastructure.persistence.mappers;

import com.audrey.soft.auth.domain.models.User;
import com.audrey.soft.auth.infrastructure.persistence.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserEntityMapper {

    User toDomain(UserEntity entity);
    UserEntity toEntity(User domain);
}
