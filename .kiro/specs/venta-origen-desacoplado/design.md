# Design Document — venta-origen-desacoplado

## Overview

Este feature elimina el acoplamiento estructural entre el módulo `billing` y el módulo `restaurant` reemplazando la columna `comanda_id` (con FK directa a `restaurant.comandas`) por una tabla puente `billing.venta_origen`. El origen de una venta pasa a ser un concepto propio del dominio `billing`, expresado mediante el value object `VentaOrigen` y el enum `TipoOrigen`.

El cambio es retrocompatible a nivel de datos: la migración Liquibase traslada los registros existentes con `comanda_id IS NOT NULL` a la nueva tabla antes de eliminar la columna. Las ventas sin origen (retail/servicio directo) simplemente no tienen fila en `venta_origen`.

**Motivación principal:** permitir que empresas sin módulo de restaurante usen `billing` sin que el esquema de BD ni el dominio contengan referencias a `restaurant`.

---

## Architecture

El cambio sigue la arquitectura hexagonal ya establecida en el proyecto. Las capas afectadas son:

```
┌─────────────────────────────────────────────────────────┐
│  Application Layer                                       │
│  ┌──────────────────┐   ┌──────────────────────────┐    │
│  │ ListVentasUseCase│   │ BuscarVentasUseCase       │    │
│  │  (mapeo origen)  │   │  (mapeo origen)           │    │
│  └────────┬─────────┘   └────────────┬─────────────┘    │
│           │                          │                   │
│           └──────────┬───────────────┘                   │
│                      ▼                                   │
│              VentaDTO (tipoOrigen, origenId)             │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│  Domain Layer                                            │
│  ┌──────────────────────────────────────────────────┐   │
│  │  Venta  ──── origen: VentaOrigen (nullable)      │   │
│  │  VentaOrigen { tipoOrigen: String, origenId: UUID}│   │
│  │  TipoOrigen { COMANDA, PEDIDO_ONLINE,             │   │
│  │               COTIZACION, DIRECTO }               │   │
│  └──────────────────────────────────────────────────┘   │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│  Infrastructure Layer                                    │
│  ┌──────────────────────────────────────────────────┐   │
│  │  VentaRepositoryAdapter                          │   │
│  │    save()  → persiste VentaOrigenEntity si       │   │
│  │              origen != null                      │   │
│  │    toDomain() → mapea VentaOrigenEntity →        │   │
│  │                 VentaOrigen                      │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────┐   │
│  │  VentaEntity  ──── origen: VentaOrigenEntity     │   │
│  │                    (@OneToOne, LAZY, optional)   │   │
│  │  VentaOrigenEntity { ventaId, tipoOrigen,        │   │
│  │                      origenId }                  │   │
│  └──────────────────────────────────────────────────┘   │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│  Database (PostgreSQL)                                   │
│  billing.ventas  (sin comanda_id)                        │
│  billing.venta_origen (venta_id PK+FK, tipo_origen,      │
│                        origen_id)                        │
└─────────────────────────────────────────────────────────┘
```

**Decisión de diseño — tabla separada vs columnas en `ventas`:** Se eligió tabla separada (`venta_origen`) en lugar de agregar columnas `tipo_origen` / `origen_id` directamente a `ventas`. Esto mantiene la tabla `ventas` limpia para ventas directas (la mayoría en contexto retail) y hace explícita la opcionalidad del origen sin columnas nullable en la tabla principal.

**Decisión de diseño — `@OneToOne` con `mappedBy`:** `VentaOrigenEntity` es el lado propietario (tiene la FK `venta_id`). `VentaEntity` declara `@OneToOne(mappedBy = "ventaId", fetch = LAZY, optional = true)`. Esto evita un JOIN innecesario al cargar ventas sin origen.

---

## Components and Interfaces

### Nuevas clases

#### `TipoOrigen.java`
```
package: com.audrey.soft.billing.domain.models
tipo: enum
valores: COMANDA, PEDIDO_ONLINE, COTIZACION, DIRECTO
```

#### `VentaOrigen.java`
```
package: com.audrey.soft.billing.domain.models
tipo: value object (clase inmutable)
campos:
  - tipoOrigen: String  (nombre del enum como String para desacoplar de la capa de infraestructura)
  - origenId: UUID
constructor: lanza IllegalArgumentException si tipoOrigen == null || origenId == null
```

#### `VentaOrigenEntity.java`
```
package: com.audrey.soft.billing.infrastructure.persistence.entities
tipo: @Entity
tabla: billing.venta_origen
campos:
  - ventaId: UUID  (@Id, sin @GeneratedValue)
  - tipoOrigen: String  (@Column nullable=false, length=30)
  - origenId: UUID  (@Column nullable=false)
```

### Clases modificadas

#### `Venta.java`
- Eliminar: `private UUID comandaId` y su getter/setter
- Agregar: `private VentaOrigen origen` (nullable) y su getter/setter
- Actualizar constructor

#### `VentaEntity.java`
- Eliminar: `@Column(name = "comanda_id") private UUID comandaId`
- Agregar:
  ```java
  @OneToOne(mappedBy = "ventaId", fetch = FetchType.LAZY, optional = true)
  private VentaOrigenEntity origen;
  ```

#### `VentaRepositoryAdapter.java`
- En `save()`: eliminar `.comandaId(...)`, agregar lógica condicional para crear y persistir `VentaOrigenEntity` si `venta.getOrigen() != null`
- En `toDomain()`: eliminar `e.getComandaId()`, agregar mapeo de `e.getOrigen()` → `VentaOrigen`
- Requiere inyectar un repositorio JPA para `VentaOrigenEntity` (o usar `EntityManager` / cascade)

#### `VentaDTO.java`
- Eliminar: `UUID comandaId`
- Agregar: `String tipoOrigen` (nullable), `UUID origenId` (nullable)

#### `ListVentasUseCase.java` y `BuscarVentasUseCase.java`
- Reemplazar `v.getComandaId()` por lógica de mapeo:
  ```java
  v.getOrigen() != null ? v.getOrigen().getTipoOrigen() : null,
  v.getOrigen() != null ? v.getOrigen().getOrigenId() : null,
  ```

---

## Data Models

### Esquema de base de datos — estado final

```sql
-- billing.ventas (columna eliminada)
-- ANTES: comanda_id uuid  + FK fk_ventas_comanda + índice idx_ventas_comanda
-- DESPUÉS: columna, FK e índice eliminados

-- Nueva tabla
CREATE TABLE billing.venta_origen (
    venta_id    uuid        PRIMARY KEY,
    tipo_origen varchar(30) NOT NULL
        CHECK (tipo_origen IN ('COMANDA','PEDIDO_ONLINE','COTIZACION','DIRECTO')),
    origen_id   uuid        NOT NULL,
    CONSTRAINT fk_venta_origen_venta
        FOREIGN KEY (venta_id) REFERENCES billing.ventas(id) ON DELETE CASCADE
);

CREATE INDEX idx_venta_origen_origen_id ON billing.venta_origen (origen_id);
```

### Migración Liquibase — `22-billing-venta-origen.sql`

El archivo sigue la convención del proyecto y contiene los changesets en este orden:

1. `santer:22-create-venta-origen` — crea la tabla `billing.venta_origen` con PK, FK CASCADE y CHECK
2. `santer:22-migrate-comanda-id` — inserta filas en `venta_origen` para todas las ventas con `comanda_id IS NOT NULL`
3. `santer:22-drop-comanda-fk-index-col` — elimina FK `fk_ventas_comanda`, índice `idx_ventas_comanda` y columna `comanda_id`
4. `santer:22-create-origen-index` — crea `idx_venta_origen_origen_id`

Cada changeset tiene su bloque `-- rollback` correspondiente.

### Modelo de dominio

```
Venta
├── id: UUID
├── sucursalId: UUID
├── comprobanteSerieId: UUID (nullable)
├── origen: VentaOrigen (nullable)   ← reemplaza comandaId
├── clienteId: UUID (nullable)
├── tipoComprobante: String
├── serie: String (nullable)
├── correlativo: Integer (nullable)
├── numeroComprobante: String (nullable)
├── subtotal: BigDecimal
├── descuento: BigDecimal
├── igv: BigDecimal
├── total: BigDecimal
├── items: List<VentaItem>
├── cobros: List<VentaCobro>
└── createdAt: LocalDateTime

VentaOrigen
├── tipoOrigen: String   (valor de TipoOrigen)
└── origenId: UUID

TipoOrigen: COMANDA | PEDIDO_ONLINE | COTIZACION | DIRECTO
```

### DTO

```
VentaDTO (record)
├── id: UUID
├── sucursalId: UUID
├── tipoOrigen: String (nullable)   ← reemplaza comandaId
├── origenId: UUID (nullable)       ← nuevo
├── clienteId: UUID (nullable)
├── tipoComprobante: String
├── serie: String (nullable)
├── correlativo: Integer (nullable)
├── numeroComprobante: String (nullable)
├── subtotal: BigDecimal
├── descuento: BigDecimal
├── igv: BigDecimal
├── total: BigDecimal
├── estado: String
├── sunatEnviado: boolean
├── items: List<VentaItemDTO>
├── cobros: List<VentaCobroDTO>
└── createdAt: LocalDateTime
```

---

## Correctness Properties

*Una propiedad es una característica o comportamiento que debe cumplirse en todas las ejecuciones válidas del sistema — esencialmente, una afirmación formal sobre lo que el sistema debe hacer. Las propiedades sirven como puente entre especificaciones legibles por humanos y garantías de corrección verificables automáticamente.*

### Property 1: Constructor de VentaOrigen rechaza argumentos nulos

*Para cualquier* combinación de argumentos donde `tipoOrigen` sea `null` o `origenId` sea `null`, la construcción de un `VentaOrigen` debe lanzar `IllegalArgumentException`. Para cualquier `tipoOrigen != null` y `origenId != null`, la construcción debe tener éxito y los campos deben ser accesibles con los valores proporcionados.

**Validates: Requirements 3.3**

### Property 2: Round-trip de origen en persistencia

*Para cualquier* `Venta` con un `VentaOrigen` arbitrario (tipoOrigen y origenId aleatorios), después de persistirla y leerla mediante `VentaRepositoryAdapter`, el campo `origen` de la `Venta` recuperada debe ser igual al `origen` original (mismo `tipoOrigen` y mismo `origenId`). Para cualquier `Venta` con `origen == null`, la `Venta` recuperada también debe tener `origen == null`.

**Validates: Requirements 5.1, 5.2, 5.3, 5.4**

### Property 3: Mapeo Venta → VentaDTO preserva el origen (BuscarVentasUseCase)

*Para cualquier* `Venta` con `origen != null`, el `VentaDTO` producido por `BuscarVentasUseCase` debe tener `tipoOrigen == venta.getOrigen().getTipoOrigen()` y `origenId == venta.getOrigen().getOrigenId()`. Para cualquier `Venta` con `origen == null`, ambos campos del DTO deben ser `null`.

**Validates: Requirements 6.2**

### Property 4: Mapeo Venta → VentaDTO preserva el origen (ListVentasUseCase)

*Para cualquier* `Venta` con `origen != null`, el `VentaDTO` producido por `ListVentasUseCase` debe tener `tipoOrigen == venta.getOrigen().getTipoOrigen()` y `origenId == venta.getOrigen().getOrigenId()`. Para cualquier `Venta` con `origen == null`, ambos campos del DTO deben ser `null`.

**Validates: Requirements 6.3**

---

## Error Handling

| Escenario | Comportamiento esperado |
|-----------|------------------------|
| `VentaOrigen` construido con `tipoOrigen == null` | `IllegalArgumentException` en el constructor |
| `VentaOrigen` construido con `origenId == null` | `IllegalArgumentException` en el constructor |
| Migración Liquibase falla a mitad de ejecución | Liquibase revierte el changeset completo (transaccional); el esquema queda en estado previo |
| Se intenta insertar en `venta_origen` un `tipo_origen` fuera del CHECK | La BD rechaza con `PSQLException` (constraint violation); el adaptador debe dejar propagar la excepción |
| Se elimina una `Venta` con origen asociado | La FK `ON DELETE CASCADE` elimina automáticamente la fila en `venta_origen` |
| `VentaEntity` cargada sin `VentaOrigenEntity` (venta directa) | `toDomain()` asigna `origen = null`; no se lanza excepción |

---

## Testing Strategy

### Enfoque dual

Se combinan tests de ejemplo (unitarios) con tests basados en propiedades (PBT) para cubrir tanto casos concretos como el espacio general de inputs.

### Librería PBT

Se usará **[jqwik](https://jqwik.net/)** (versión compatible con JUnit 5, ya disponible en el ecosistema Spring Boot). Cada property test se configura con mínimo 100 iteraciones (`@Property(tries = 100)`).

### Tests unitarios (ejemplo-based)

- `VentaOrigenTest`: construcción exitosa con valores válidos, construcción fallida con cada combinación de null
- `VentaRepositoryAdapterTest`: mock de JPA, verificar que `save()` llama al repositorio de `VentaOrigenEntity` cuando `origen != null` y no lo llama cuando `origen == null`
- `ListVentasUseCaseTest` / `BuscarVentasUseCaseTest`: casos concretos con origen presente y ausente
- Migración Liquibase: test de integración con Testcontainers (PostgreSQL) que verifica el estado del esquema antes y después

### Tests de propiedades (PBT)

Cada test referencia su propiedad del documento de diseño mediante el tag:
`Feature: venta-origen-desacoplado, Property N: <texto>`

**Property 1 — Constructor VentaOrigen rechaza nulls**
```java
// Feature: venta-origen-desacoplado, Property 1: Constructor rechaza argumentos nulos
@Property(tries = 100)
void constructorRechazaNulls(@ForAll @Nullable String tipoOrigen,
                              @ForAll @Nullable UUID origenId) {
    Assume.that(tipoOrigen == null || origenId == null);
    assertThrows(IllegalArgumentException.class,
        () -> new VentaOrigen(tipoOrigen, origenId));
}
```

**Property 2 — Round-trip de origen en persistencia**
```java
// Feature: venta-origen-desacoplado, Property 2: Round-trip de origen en persistencia
@Property(tries = 100)
void origenRoundTrip(@ForAll("ventasConOrigen") Venta venta) {
    // Usar mocks de JPA para aislar la lógica del adaptador
    VentaOrigen origenOriginal = venta.getOrigen();
    Venta recuperada = adapter.toDomain(adapter.toEntity(venta));
    assertEquals(origenOriginal.getTipoOrigen(), recuperada.getOrigen().getTipoOrigen());
    assertEquals(origenOriginal.getOrigenId(), recuperada.getOrigen().getOrigenId());
}
```

**Property 3 — BuscarVentasUseCase preserva origen en DTO**
```java
// Feature: venta-origen-desacoplado, Property 3: Mapeo Venta→VentaDTO preserva origen (BuscarVentasUseCase)
@Property(tries = 100)
void buscarVentasDtoPreservaOrigen(@ForAll("ventasArbitrarias") Venta venta) {
    VentaDTO dto = useCase.mapToDto(venta);
    if (venta.getOrigen() != null) {
        assertEquals(venta.getOrigen().getTipoOrigen(), dto.tipoOrigen());
        assertEquals(venta.getOrigen().getOrigenId(), dto.origenId());
    } else {
        assertNull(dto.tipoOrigen());
        assertNull(dto.origenId());
    }
}
```

**Property 4 — ListVentasUseCase preserva origen en DTO**
```java
// Feature: venta-origen-desacoplado, Property 4: Mapeo Venta→VentaDTO preserva origen (ListVentasUseCase)
@Property(tries = 100)
void listVentasDtoPreservaOrigen(@ForAll("ventasArbitrarias") Venta venta) {
    VentaDTO dto = useCase.mapToDto(venta);
    if (venta.getOrigen() != null) {
        assertEquals(venta.getOrigen().getTipoOrigen(), dto.tipoOrigen());
        assertEquals(venta.getOrigen().getOrigenId(), dto.origenId());
    } else {
        assertNull(dto.tipoOrigen());
        assertNull(dto.origenId());
    }
}
```

### Tests de integración

- **Migración Liquibase** (Testcontainers + PostgreSQL): verificar que tras ejecutar `22-billing-venta-origen.sql`, la tabla `venta_origen` existe, la columna `comanda_id` no existe en `ventas`, y el número de filas migradas es correcto.
- **Persistencia end-to-end**: guardar una `Venta` con origen `COMANDA` y verificar que la fila aparece en `venta_origen` con los valores correctos.
