# Tasks: Refactorización Fiscal Multi-País

## Fase 1 — Base de datos (Liquibase)

- [x] 1.1 Crear migración `27-crear-esquema-fiscal.sql`
  - Crear esquema `fiscal`
  - Crear tabla `fiscal.fiscal_sistemas` con seed (SUNAT, SRI, DIAN, SAT, INTERNO)
  - Crear tabla `fiscal.impuesto_tipos` con seed (IGV, ISC, IVA_EC, IVA_CO, IVA_MX) — cada uno referencia su `fiscal_sistema_id` y define `tasa_default` y `tipo_calculo`
  - Crear tabla `fiscal.comprobante_tipos` con seed (tipos SUNAT y SRI)

- [x] 1.2 Crear migración `28-fiscal-config-por-sucursal.sql`
  - Crear tabla `fiscal.fiscal_config` con columnas: `sucursal_id`, `fiscal_sistema_id`, `moneda_codigo`, `ruc_empresa`, `razon_social`, `direccion_fiscal`, `impuestos_default varchar(20)[]`, `precios_incluyen_impuesto`
  - **Sin columnas de tasa ni nombre de impuesto** — esos datos viven en `impuesto_tipos`
  - Agregar columnas `pais_codigo`, `moneda_codigo` a `core.sucursales` (DEFAULT 'PER'/'PEN')
  - Insertar `fiscal_config` para todas las sucursales existentes (sistema SUNAT, `impuestos_default = '{IGV}'`, `precios_incluyen_impuesto = true`)

- [x] 1.3 Crear migración `29-venta-impuestos-y-limpieza.sql`
  - Crear tabla `billing.venta_impuestos` (snapshot inmutable: `impuesto_id`, `codigo`, `nombre`, `tasa`, `base`, `monto`, `incluido_precio`)
  - Agregar columnas `base_imponible`, `total_impuestos` a `billing.ventas`
  - Migrar IGV existente a `billing.venta_impuestos` usando datos de `billing.ventas.igv`
  - Actualizar `base_imponible = subtotal - descuento` y `total_impuestos = igv` en ventas existentes
  - DROP columna `igv` de `billing.ventas`

- [x] 1.4 Crear migración `30-venta-fiscal-y-limpieza-sunat.sql`
  - Crear tabla `fiscal.venta_fiscal`
  - Migrar datos `sunat_*` de `billing.ventas` → `fiscal.venta_fiscal` con `fiscal_sistema_id = 'SUNAT'`
  - DROP columnas `sunat_enviado`, `sunat_aceptado`, `sunat_codigo_hash`, `sunat_enviado_at` de `billing.ventas`
  - Ampliar `billing.comprobante_series.serie` de `varchar(4)` a `varchar(20)`

## Fase 2 — Dominio y entidades Java

- [x] 2.1 Crear entidades JPA
  - `FiscalSistemaEntity` → `fiscal.fiscal_sistemas`
  - `ImpuestoTipoEntity` → `fiscal.impuesto_tipos`
  - `FiscalConfigEntity` → `fiscal.fiscal_config` (sin campos de tasa ni nombre de impuesto)
  - `VentaFiscalEntity` → `fiscal.venta_fiscal`
  - `VentaImpuestoEntity` → `billing.venta_impuestos`

- [x] 2.2 Actualizar `VentaEntity`
  - Eliminar campos `igv`, `sunatEnviado`, `sunatAceptado`, `sunatCodigoHash`
  - Agregar `baseImponible`, `totalImpuestos`
  - Agregar relación `@OneToMany` con `VentaImpuestoEntity`
  - Agregar relación `@OneToOne` con `VentaFiscalEntity`

- [x] 2.3 Actualizar `SucursalEntity`
  - Agregar campos `paisCodigo`, `monedaCodigo`

- [x] 2.4 Actualizar modelo de dominio `Venta`
  - Eliminar campo `igv` y campos `sunat*`
  - Agregar `baseImponible`, `totalImpuestos`, `impuestos: List<VentaImpuesto>`

- [x] 2.5 Crear `NumeroComprobanteFormatter`
  - Interface con método `format(String serie, int correlativo): String`
  - `SunatFormatter`: `serie + "-" + String.format("%08d", correlativo)`
  - `SriFormatter`: `serie + "-" + String.format("%09d", correlativo)`
  - `NumeroComprobanteFormatterFactory.forSistema(String sistemaId)` — lanza excepción si sistema desconocido

- [x] 2.6 Crear `ImpuestoCalculator`
  - Recibe: `BigDecimal base`, `List<ImpuestoTipoEntity> impuestos`, `boolean preciosIncluyenImpuesto`
  - Para cada impuesto calcula el monto:
    - Si `incluido_precio = true` → `monto = base * tasa / (1 + tasa)`
    - Si `incluido_precio = false` → `monto = base * tasa`
  - Retorna `List<VentaImpuesto>` con snapshot (código, nombre, tasa, base, monto) — no referencias al catálogo

## Fase 3 — Repositorios y puertos

- [x] 3.1 Crear `SpringDataFiscalSistemaRepository`
- [x] 3.2 Crear `SpringDataImpuestoTipoRepository` con método `findAllByIdIn(List<String> ids)`
- [x] 3.3 Crear `SpringDataFiscalConfigRepository` con método `findBySucursalId(UUID sucursalId)`
- [x] 3.4 Crear `SpringDataVentaFiscalRepository`
- [x] 3.5 Crear `SpringDataVentaImpuestoRepository`
- [x] 3.6 Actualizar `VentaRepositoryAdapter`
  - Inyectar `FiscalConfigRepository`, `ImpuestoTipoRepository`, `VentaFiscalRepository`, `VentaImpuestoRepository`
  - En `save()`:
    1. Guardar `VentaEntity`
    2. Guardar líneas `VentaImpuestoEntity`
    3. Crear `VentaFiscalEntity` (enviado=false)
  - En `toDomain()`: mapear `venta_impuestos` y `venta_fiscal`

## Fase 4 — Use Cases

- [x] 4.1 Actualizar `CreateVentaDirectaUseCase`
  - Leer `fiscal_config` de la sucursal → obtener `impuestos_default` y `precios_incluyen_impuesto`
  - Leer `ImpuestoTipoEntity` para cada id en `impuestos_default`
  - Usar `ImpuestoCalculator` para calcular líneas de impuesto
  - Usar `NumeroComprobanteFormatterFactory` para generar el número de comprobante
  - Construir `Venta` con `baseImponible`, `totalImpuestos`, `impuestos` — sin campos `sunat*` ni `igv`

- [x] 4.2 Actualizar `CerrarComandaUseCase`
  - Ídem que 4.1

- [x] 4.3 Actualizar `BuscarVentasUseCase` y `ListVentasUseCase`
  - Incluir `impuestos` (lista) y `fiscalEnviado`/`fiscalAceptado` desde `venta_fiscal` en el DTO

- [x] 4.4 Actualizar `VentaDTO`
  - Reemplazar campo `igv` por `totalImpuestos`
  - Agregar `impuestos: List<VentaImpuestoDTO>` (código, nombre, tasa, monto)
  - Reemplazar `sunatEnviado` por `fiscalEnviado: boolean`
  - Agregar `fiscalSistemaId: String` (opcional, null si modo INTERNO)

## Fase 5 — Frontend

- [x] 5.1 Actualizar `pos.models.ts`
  - En `Venta`: reemplazar `igv` por `totalImpuestos`, agregar `impuestos: VentaImpuesto[]`
  - Renombrar `sunatEnviado` → `fiscalEnviado`, agregar `fiscalSistemaId`
  - Agregar interface `VentaImpuesto { codigo, nombre, tasa, monto }`

- [x] 5.2 Actualizar `cartera.component`
  - Columna "SUNAT" → columna "Fiscal" con badge genérico
  - Drawer: mostrar desglose de impuestos por línea en la sección Resumen
  - Badge de envío: "Enviado a SUNAT" / "Enviado a SRI" según `fiscalSistemaId`

- [x] 5.3 Actualizar filtro en cartera
  - Renombrar param `sunatEnviado` → `fiscalEnviado` en servicio y backend

## Fase 6 — Configuración de sucursal (UI)

- [x] 6.1 Agregar selector de país al formulario de creación de sucursal
  - El sistema tributario se asigna automáticamente según el país seleccionado

- [x] 6.2 Crear pantalla de configuración fiscal en el módulo de configuración de sucursal
  - Campos: RUC/NIT, razón social, dirección fiscal, `precios_incluyen_impuesto`
  - **Sin campo de tasa** — la tasa viene del catálogo `impuesto_tipos` según el sistema asignado
  - Mostrar los impuestos que aplican (readonly, del catálogo) para que el usuario sepa qué se calculará

- [x] 6.3 Validar formato de serie al crear `comprobante_series` según el `serie_regex` del sistema tributario de la sucursal
