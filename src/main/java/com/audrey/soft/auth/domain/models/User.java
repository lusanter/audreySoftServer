package com.audrey.soft.auth.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Identidad pura del sistema. Solamente guarda los datos de la cuenta personal,
 * aislando la lógica de permisos y empresas a la tabla UserRoleAssignment.
 */
public class User {

    private UUID id;
    private String username;
    private String password;
    private String email;
    private String profilePictureUrl;
    private boolean active;
    private LocalDateTime lastLogin;


    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(UUID id, String username, String password, String email, String profilePictureUrl,
                boolean active, LocalDateTime lastLogin, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.profilePictureUrl = profilePictureUrl;
        this.active = active;
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void desactivate() { this.active = false; }
    public void activate() { this.active = true; }
    public void updateLastLogin() { this.lastLogin = LocalDateTime.now(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}