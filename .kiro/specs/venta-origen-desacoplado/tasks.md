# Plan de Implementación: venta-origen-desacoplado

## Overview

Refactorizar la relación entre `billing` y `restaurant` eliminando `comanda_id` de `billing.ventas` y reemplazándolo por la tabla puente `billing.venta_origen`. El cambio abarca migración de BD, nuevas clases de dominio, entidad JPA, adaptador de persistencia, DTOs y casos de uso.

## Tasks

- [x] 1. Migración Liquibase — crear tabla `billing.venta_origen`
  - Crear `src/main/resources/db/changelog/changes/22-billing-venta-origen.sql` con cuatro changesets:
    - `santer:22-create-venta-origen`: crea la tabla con PK, FK `ON DELETE CASCADE` y CHECK sobre `tipo_origen`
    - `santer:22-migrate-comanda-id`: inserta en `venta_origen` las filas de `ventas` donde `comanda_id IS NOT NULL`
    - `santer:22-drop-comanda-fk-index-col`: elimina FK `fk_ventas_comanda`, índice `idx_ventas_comanda` y columna `comanda_id`
    - `santer:22-create-origen-index`: crea `idx_venta_origen_origen_id`
  - Cada changeset debe incluir su bloque `-- rollback`
  - Agregar la entrada `changes/22-billing-venta-origen.sql` al final de `db.changelog-master.yaml`
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 7.1, 7.2, 7.3_

- [x] 2. Dominio — `TipoOrigen` y `VentaOrigen`
  - [x] 2.1 Crear enum `TipoOrigen`
    - Crear `src/main/java/com/audrey/soft/billing/domain/models/TipoOrigen.java`
    - Valores: `COMANDA`, `PEDIDO_ONLINE`, `COTIZACION`, `DIRECTO`
    - _Requirements: 3.2_

  - [x] 2.2 Crear value object `VentaOrigen`
    - Crear `src/main/java/com/audrey/soft/billing/domain/models/VentaOrigen.java`
    - Campos: `tipoOrigen: String`, `origenId: UUID`; clase inmutable
    - Constructor lanza `IllegalArgumentException` si `tipoOrigen == null` o `origenId == null`
    - _Requirements: 3.1, 3.3_

  - [ ]* 2.3 Escribir property test — Property 1: Constructor rechaza argumentos nulos
    - **Property 1: Constructor de VentaOrigen rechaza argumentos nulos**
    - **Validates: Requirements 3.3**
    - Usar jqwik `@Property(tries = 100)` con `@ForAll @Nullable String` y `@ForAll @Nullable UUID`
    - Asumir que al menos uno de los dos es `null`; verificar que se lanza `IllegalArgumentException`
    - Verificar también el caso feliz: ambos no-null → construcción exitosa y getters devuelven los valores

  - [ ]* 2.4 Escribir tests unitarios para `VentaOrigen`
    - Construcción exitosa con valores válidos
    - Construcción fallida con `tipoOrigen = null`, `origenId = null`, y ambos `null`
    - _Requirements: 3.3_

- [x] 3. Checkpoint — compilación limpia del dominio
  - Verificar que `TipoOrigen` y `VentaOrigen` compilan sin errores. Preguntar al usuario si hay dudas antes de continuar.

- [x] 4. Infraestructura — `VentaOrigenEntity`
  - Crear `src/main/java/com/audrey/soft/billing/infrastructure/persistence/entities/VentaOrigenEntity.java`
  - Anotar con `@Entity` y `@Table(name = "venta_origen", schema = "billing")`
  - Campo `ventaId: UUID` como `@Id` sin `@GeneratedValue`
  - Campo `tipoOrigen: String` con `@Column(nullable = false, length = 30)`
  - Campo `origenId: UUID` con `@Column(nullable = false)`
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_

- [x] 5. Dominio — actualizar `Venta`
  - En `src/main/java/com/audrey/soft/billing/domain/models/Venta.java`:
    - Eliminar campo `comandaId: UUID` y su getter/setter
    - Agregar campo `origen: VentaOrigen` (nullable) con getter/setter
    - Actualizar el constructor para recibir `VentaOrigen origen` en lugar de `UUID comandaId`
  - _Requirements: 4.1, 4.2, 4.3, 4.4_

- [x] 6. Infraestructura — actualizar `VentaEntity`
  - En `src/main/java/com/audrey/soft/billing/infrastructure/persistence/entities/VentaEntity.java`:
    - Eliminar `@Column(name = "comanda_id") private UUID comandaId`
    - Agregar `@OneToOne(mappedBy = "ventaId", fetch = FetchType.LAZY, optional = true) private VentaOrigenEntity origen`
  - _Requirements: 2.2 (VentaOrigenEntity es lado propietario), 4.1_

- [x] 7. Infraestructura — actualizar `VentaRepositoryAdapter`
  - [x] 7.1 Inyectar repositorio JPA para `VentaOrigenEntity`
    - Crear `SpringDataVentaOrigenRepository` (interfaz `JpaRepository<VentaOrigenEntity, UUID>`) en el paquete de repositorios
    - Inyectarlo en `VentaRepositoryAdapter`
    - _Requirements: 5.1_

  - [x] 7.2 Actualizar método `save()`
    - Eliminar `.comandaId(venta.getComandaId())` del builder de `VentaEntity`
    - Después de `jpa.save(entity)`, si `venta.getOrigen() != null`, crear y persistir `VentaOrigenEntity` con `ventaId = saved.getId()`
    - Si `venta.getOrigen() == null`, no crear `VentaOrigenEntity`
    - _Requirements: 5.1, 5.2, 5.5_

  - [x] 7.3 Actualizar método `toDomain()`
    - Eliminar `e.getComandaId()` del constructor de `Venta`
    - Si `e.getOrigen() != null`, construir `VentaOrigen` con `tipoOrigen` y `origenId` de la entidad
    - Si `e.getOrigen() == null`, pasar `null` como `origen`
    - _Requirements: 5.3, 5.4, 5.5_

  - [ ]* 7.4 Escribir property test — Property 2: Round-trip de origen en persistencia
    - **Property 2: Round-trip de origen en persistencia**
    - **Validates: Requirements 5.1, 5.2, 5.3, 5.4**
    - Usar jqwik con `@ForAll` para generar `Venta` con `VentaOrigen` arbitrario (tipoOrigen de `TipoOrigen.values()`, origenId aleatorio)
    - Verificar que `toDomain(toEntity(venta)).getOrigen()` preserva `tipoOrigen` y `origenId`
    - Verificar también el caso `origen == null` → `toDomain` devuelve `origen == null`

  - [ ]* 7.5 Escribir tests unitarios para `VentaRepositoryAdapter`
    - Mock de `SpringDataVentaRepository`, `SpringDataVentaOrigenRepository`, `SpringDataSucursalRepository`, `SpringDataComprobanteSerieRepository`
    - Verificar que `save()` llama a `ventaOrigenJpa.save(...)` cuando `origen != null`
    - Verificar que `save()` no llama a `ventaOrigenJpa.save(...)` cuando `origen == null`
    - _Requirements: 5.1, 5.2_

- [x] 8. Checkpoint — compilación limpia de infraestructura
  - Verificar que `VentaOrigenEntity`, `VentaEntity` y `VentaRepositoryAdapter` compilan sin errores. Preguntar al usuario si hay dudas antes de continuar.

- [x] 9. Capa de aplicación — actualizar `VentaDTO`
  - En `src/main/java/com/audrey/soft/billing/app/dtos/VentaDTO.java`:
    - Eliminar campo `UUID comandaId`
    - Agregar `String tipoOrigen` (nullable) y `UUID origenId` (nullable) en la misma posición
  - _Requirements: 6.1_

- [x] 10. Capa de aplicación — actualizar `ListVentasUseCase`
  - En `src/main/java/com/audrey/soft/billing/app/usecases/Venta/ListVentasUseCase.java`:
    - Reemplazar `v.getComandaId()` por:
      ```java
      v.getOrigen() != null ? v.getOrigen().getTipoOrigen() : null,
      v.getOrigen() != null ? v.getOrigen().getOrigenId()   : null,
      ```
  - _Requirements: 6.3, 6.4_

  - [ ]* 10.1 Escribir property test — Property 4: ListVentasUseCase preserva origen en DTO
    - **Property 4: Mapeo Venta → VentaDTO preserva el origen (ListVentasUseCase)**
    - **Validates: Requirements 6.3**
    - Usar jqwik con `@ForAll("ventasArbitrarias")` para generar `Venta` con `origen` presente o `null`
    - Verificar que `dto.tipoOrigen()` y `dto.origenId()` coinciden con `venta.getOrigen()` o son `null`

  - [ ]* 10.2 Escribir tests unitarios para `ListVentasUseCase`
    - Caso: venta con `origen != null` → DTO tiene `tipoOrigen` y `origenId` correctos
    - Caso: venta con `origen == null` → DTO tiene `tipoOrigen == null` y `origenId == null`
    - _Requirements: 6.3, 6.4_

- [x] 11. Capa de aplicación — actualizar `BuscarVentasUseCase`
  - En `src/main/java/com/audrey/soft/billing/app/usecases/Venta/BuscarVentasUseCase.java`:
    - Reemplazar `v.getComandaId()` por la misma lógica de mapeo que en la tarea 10
  - _Requirements: 6.2, 6.4_

  - [ ]* 11.1 Escribir property test — Property 3: BuscarVentasUseCase preserva origen en DTO
    - **Property 3: Mapeo Venta → VentaDTO preserva el origen (BuscarVentasUseCase)**
    - **Validates: Requirements 6.2**
    - Misma estructura que el property test de `ListVentasUseCase` pero sobre `BuscarVentasUseCase`

  - [ ]* 11.2 Escribir tests unitarios para `BuscarVentasUseCase`
    - Caso: venta con `origen != null` → DTO tiene `tipoOrigen` y `origenId` correctos
    - Caso: venta con `origen == null` → DTO tiene `tipoOrigen == null` y `origenId == null`
    - _Requirements: 6.2, 6.4_

- [x] 12. Checkpoint final — todos los tests pasan
  - Ejecutar `./mvnw test` y verificar que todos los tests pasan sin errores de compilación ni fallos. Preguntar al usuario si hay dudas antes de cerrar el feature.

## Notes

- Las tareas marcadas con `*` son opcionales y pueden omitirse para un MVP más rápido.
- El orden de las tareas es importante: la migración (1) puede ejecutarse en paralelo con el dominio (2–5), pero la infraestructura (6–7) depende de ambas.
- `VentaOrigenEntity` es el lado propietario de la relación `@OneToOne`; no hay cascade desde `VentaEntity`, por eso el adaptador persiste `VentaOrigenEntity` explícitamente.
- Los property tests usan jqwik (`net.jqwik:jqwik`) con `@Property(tries = 100)`.
- Cada property test debe incluir el tag de trazabilidad: `// Feature: venta-origen-desacoplado, Property N: <texto>`.
