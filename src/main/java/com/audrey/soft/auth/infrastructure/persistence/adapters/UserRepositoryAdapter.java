package com.audrey.soft.auth.infrastructure.persistence.adapters;

import com.audrey.soft.auth.domain.models.User;
import com.audrey.soft.auth.domain.ports.UserRepositoryPort;
import com.audrey.soft.auth.infrastructure.persistence.mappers.UserEntityMapper;
import com.audrey.soft.auth.infrastructure.persistence.repositories.SpringDataUserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository jpaRepository;
    private final UserEntityMapper mapper;

    public UserRepositoryAdapter(SpringDataUserRepository jpaRepository, UserEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
                .map(mapper::toDomain);
    }

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<User> getAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }
}