# Requirements Document

## Introduction

Este feature refactoriza la relación entre el módulo `billing` y el módulo `restaurant` eliminando el acoplamiento directo entre `billing.ventas` y `restaurant.comandas`. Actualmente `billing.ventas` tiene una columna `comanda_id uuid` con FK directa al schema `restaurant`, lo que impide que empresas de tipo retail o servicio (sin comandas) usen el módulo de ventas limpiamente.

La solución introduce una tabla puente `billing.venta_origen` que registra el origen de una venta de forma opcional y extensible. Solo existe el registro si la venta tiene un origen identificable; las ventas directas (retail/servicio) simplemente no tienen fila en esa tabla.

## Glossary

- **Billing_Module**: El módulo de facturación del sistema, responsable de gestionar ventas, comprobantes y cobros.
- **Restaurant_Module**: El módulo de restaurante, responsable de gestionar comandas, mesas y pedidos.
- **Venta**: Entidad del dominio `billing` que representa una transacción de venta con su comprobante fiscal.
- **Comanda**: Entidad del dominio `restaurant` que representa un pedido de mesa en un restaurante.
- **VentaOrigen**: Objeto de valor del dominio `billing` que describe el origen opcional de una venta (comanda, pedido online, cotización, etc.).
- **VentaOrigenEntity**: Entidad JPA que mapea la tabla `billing.venta_origen`.
- **TipoOrigen**: Enumeración con los valores `COMANDA`, `PEDIDO_ONLINE`, `COTIZACION`, `DIRECTO`.
- **Liquibase_Migration**: Archivo SQL versionado que aplica cambios de esquema de base de datos de forma controlada.
- **VentaRepositoryPort**: Puerto de dominio (interfaz) que abstrae el acceso a datos de `Venta`.
- **VentaRepositoryAdapter**: Adaptador de infraestructura que implementa `VentaRepositoryPort` usando JPA.
- **VentaDTO**: Record de transferencia de datos que expone los datos de una `Venta` hacia la capa web.

---

## Requirements

### Requirement 1: Migración de esquema de base de datos

**User Story:** Como DBA / desarrollador backend, quiero que el esquema de base de datos elimine la FK directa entre `billing.ventas` y `restaurant.comandas`, para que el módulo `billing` no dependa estructuralmente del schema `restaurant`.

#### Acceptance Criteria

1. THE Liquibase_Migration SHALL crear la tabla `billing.venta_origen` con las columnas `venta_id uuid` (PK y FK a `billing.ventas`), `tipo_origen varchar(30)` y `origen_id uuid`.
2. THE Liquibase_Migration SHALL definir `venta_id` como clave primaria de `billing.venta_origen` garantizando la relación uno-a-uno con `billing.ventas`.
3. THE Liquibase_Migration SHALL definir un constraint `CHECK` en `tipo_origen` que acepte únicamente los valores `COMANDA`, `PEDIDO_ONLINE`, `COTIZACION` y `DIRECTO`.
4. WHEN existen filas en `billing.ventas` con `comanda_id IS NOT NULL`, THE Liquibase_Migration SHALL insertar una fila en `billing.venta_origen` por cada una de esas filas, con `tipo_origen = 'COMANDA'` y `origen_id = comanda_id`.
5. AFTER the data migration in criterion 4, THE Liquibase_Migration SHALL eliminar el constraint FK `fk_ventas_comanda` de `billing.ventas`.
6. AFTER removing the FK constraint, THE Liquibase_Migration SHALL eliminar el índice `idx_ventas_comanda` de `billing.ventas`.
7. AFTER removing the index, THE Liquibase_Migration SHALL eliminar la columna `comanda_id` de `billing.ventas`.
8. THE Liquibase_Migration SHALL crear un índice `idx_venta_origen_origen_id` sobre `billing.venta_origen(origen_id)` para soportar búsquedas por origen.
9. THE Liquibase_Migration SHALL registrarse en `db.changelog-master.yaml` como el archivo `changes/22-billing-venta-origen.sql` en el orden correcto después de `21-ventas-add-remaining-columns.sql`.

---

### Requirement 2: Nueva entidad JPA VentaOrigenEntity

**User Story:** Como desarrollador backend, quiero una entidad JPA `VentaOrigenEntity` que mapee la tabla `billing.venta_origen`, para que la capa de persistencia pueda gestionar el origen de una venta sin referencias a entidades del módulo `restaurant`.

#### Acceptance Criteria

1. THE VentaOrigenEntity SHALL anotarse con `@Entity` y `@Table(name = "venta_origen", schema = "billing")`.
2. THE VentaOrigenEntity SHALL declarar `ventaId` de tipo `UUID` como `@Id` sin generación automática, ya que el ID es el mismo que el de la venta asociada.
3. THE VentaOrigenEntity SHALL declarar `tipoOrigen` de tipo `String` con longitud máxima de 30 caracteres y restricción `nullable = false`.
4. THE VentaOrigenEntity SHALL declarar `origenId` de tipo `UUID` con restricción `nullable = false`.
5. THE VentaOrigenEntity SHALL ubicarse en el paquete `com.audrey.soft.billing.infrastructure.persistence.entities`.

---

### Requirement 3: Objeto de valor VentaOrigen en el dominio

**User Story:** Como desarrollador backend, quiero un objeto de valor `VentaOrigen` en el dominio `billing`, para que la entidad de dominio `Venta` pueda expresar su origen sin depender de tipos del módulo `restaurant`.

#### Acceptance Criteria

1. THE Billing_Module SHALL definir la clase `VentaOrigen` en el paquete `com.audrey.soft.billing.domain.models` con los campos `tipoOrigen` (String) y `origenId` (UUID).
2. THE Billing_Module SHALL definir el enum `TipoOrigen` en el paquete `com.audrey.soft.billing.domain.models` con los valores `COMANDA`, `PEDIDO_ONLINE`, `COTIZACION` y `DIRECTO`.
3. WHEN `tipoOrigen` es `null` o `origenId` es `null`, THE Billing_Module SHALL rechazar la construcción de un `VentaOrigen` lanzando `IllegalArgumentException`.

---

### Requirement 4: Actualización del modelo de dominio Venta

**User Story:** Como desarrollador backend, quiero que el modelo de dominio `Venta` reemplace el campo `comandaId: UUID` por un campo `origen: VentaOrigen` opcional, para que el dominio `billing` no contenga referencias directas a conceptos del módulo `restaurant`.

#### Acceptance Criteria

1. THE Billing_Module SHALL eliminar el campo `comandaId` de tipo `UUID` de la clase `Venta`.
2. THE Billing_Module SHALL agregar el campo `origen` de tipo `VentaOrigen` (nullable) a la clase `Venta`.
3. WHEN una `Venta` no tiene origen, THE Billing_Module SHALL representar ese estado con `origen = null` (venta directa retail/servicio).
4. WHEN una `Venta` tiene origen de tipo `COMANDA`, THE Billing_Module SHALL almacenar el ID de la comanda en `origen.origenId`.

---

### Requirement 5: Actualización del adaptador de persistencia VentaRepositoryAdapter

**User Story:** Como desarrollador backend, quiero que `VentaRepositoryAdapter` gestione la persistencia de `VentaOrigen` a través de `VentaOrigenEntity`, para que el guardado y la lectura de ventas incluyan correctamente el origen sin usar la columna `comanda_id` eliminada.

#### Acceptance Criteria

1. WHEN `VentaRepositoryAdapter` persiste una `Venta` con `origen != null`, THE VentaRepositoryAdapter SHALL crear y persistir una instancia de `VentaOrigenEntity` asociada a la venta guardada.
2. WHEN `VentaRepositoryAdapter` persiste una `Venta` con `origen == null`, THE VentaRepositoryAdapter SHALL omitir la creación de `VentaOrigenEntity`.
3. WHEN `VentaRepositoryAdapter` lee una `VentaEntity` que tiene `VentaOrigenEntity` asociada, THE VentaRepositoryAdapter SHALL construir el objeto `VentaOrigen` correspondiente y asignarlo al campo `origen` de la `Venta` de dominio.
4. WHEN `VentaRepositoryAdapter` lee una `VentaEntity` sin `VentaOrigenEntity` asociada, THE VentaRepositoryAdapter SHALL asignar `null` al campo `origen` de la `Venta` de dominio.
5. THE VentaRepositoryAdapter SHALL eliminar toda referencia al campo `comandaId` en la construcción de `VentaEntity`.

---

### Requirement 6: Actualización de DTOs y casos de uso

**User Story:** Como desarrollador backend, quiero que los DTOs y casos de uso del módulo `billing` reflejen el nuevo modelo con `VentaOrigen`, para que la API expuesta sea consistente con el dominio refactorizado.

#### Acceptance Criteria

1. THE Billing_Module SHALL reemplazar el campo `comandaId: UUID` en `VentaDTO` por los campos `tipoOrigen: String` (nullable) y `origenId: UUID` (nullable).
2. WHEN `BuscarVentasUseCase` mapea una `Venta` a `VentaDTO`, THE BuscarVentasUseCase SHALL leer `tipoOrigen` y `origenId` desde `venta.getOrigen()` cuando `origen != null`, y asignar `null` a ambos campos cuando `origen == null`.
3. WHEN `ListVentasUseCase` mapea una `Venta` a `VentaDTO`, THE ListVentasUseCase SHALL aplicar la misma lógica de mapeo descrita en el criterio 2.
4. THE Billing_Module SHALL eliminar toda referencia a `getComandaId()` en los casos de uso `BuscarVentasUseCase` y `ListVentasUseCase`.

---

### Requirement 7: Integridad referencial y consistencia de datos

**User Story:** Como arquitecto de software, quiero que la tabla `billing.venta_origen` mantenga integridad referencial con `billing.ventas` y que no existan datos huérfanos tras la migración, para garantizar la consistencia del sistema.

#### Acceptance Criteria

1. THE Liquibase_Migration SHALL definir la FK de `billing.venta_origen(venta_id)` hacia `billing.ventas(id)` con `ON DELETE CASCADE`, de modo que al anular/eliminar una venta se elimine automáticamente su origen.
2. IF la migración de datos falla por cualquier causa, THEN THE Liquibase_Migration SHALL revertir todos los cambios del changeset sin dejar el esquema en estado parcial.
3. WHEN se consulta `billing.venta_origen` después de ejecutar la migración, THE Billing_Module SHALL garantizar que el número de filas en `venta_origen` es igual al número de filas en `billing.ventas` que tenían `comanda_id IS NOT NULL` antes de la migración.
