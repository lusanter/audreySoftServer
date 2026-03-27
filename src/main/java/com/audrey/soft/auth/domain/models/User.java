package com.audrey.soft.auth.domain.models;


import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    private UUID id;
    private String username;
    private String password; // Se puede actualizar (cambio de clave)
    private String email;
    private boolean active;
    private Role role;
    private UUID restauranteId; // El ID del restaurante al que pertenece
    private LocalDateTime lastLogin;

    public User(UUID id, String username, String password, String email, boolean active, Role role, UUID restauranteId, LocalDateTime lastLogin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.active = active;
        this.role = role;
        this.restauranteId = restauranteId;
        this.lastLogin = lastLogin;
    }

    public void desactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    public UUID getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(UUID restauranteId) {
        this.restauranteId = restauranteId;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }

    public Role getRole() {
        return role;
    }


    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}