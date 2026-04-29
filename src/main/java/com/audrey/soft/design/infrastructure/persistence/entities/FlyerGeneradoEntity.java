package com.audrey.soft.design.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "flyers_generados", schema = "design")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlyerGeneradoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "empresa_id", nullable = false)
    private UUID empresaId;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "producto_ids", columnDefinition = "jsonb", nullable = false)
    private List<String> productoIds;

    @Column(name = "tipo_uso", nullable = false)
    private String tipoUso;

    @Column(name = "formato", nullable = false)
    private String formato;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "palette", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> palette;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ai_flyer_dto", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> aiFlyerDto;

    @Column(name = "plantilla_id")
    private UUID plantillaId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (updatedAt == null) updatedAt = Instant.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
