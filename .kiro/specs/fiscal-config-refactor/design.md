# Diseño: Refactorización Fiscal Multi-País

## Problema

La tabla `billing.ventas` tiene campos SUNAT hardcodeados (`sunat_enviado`, `sunat_aceptado`, `sunat_codigo_hash`). La tabla `billing.comprobante_series` asume el formato SUNAT (serie de 4 chars: 1 letra + 3 dígitos, correlativo de 8 dígitos). No existe concepto de país, moneda ni sistema tributario en la sucursal. Agregar Ecuador (SRI), Colombia (DIAN) u otro país requeriría modificar el modelo central.

---

## Principio de Diseño

> La venta es un hecho comercial universal. El comprobante tributario es un anexo que depende del país y del sistema fiscal configurado en la sucursal.

---

## Alto Nivel: Módulos y Esquemas

```
core
 └── sucursales          ← agrega: pais_codigo, moneda_codigo, fiscal_sistema_id

fiscal  (nuevo esquema)
 ├── fiscal_sistemas     ← catálogo: SUNAT, SRI, DIAN, SAT, etc.
 ├── fiscal_config       ← configuración por sucursal (1:1 con sucursal)
 ├── comprobante_tipos   ← tipos de comprobante por sistema (BOLETA/FACTURA/NOTA_VENTA para SUNAT, FACTURA/NOTA_CREDITO para SRI, etc.)
 └── venta_fiscal        ← anexo tributario de cada venta (reemplaza campos sunat_* en billing.ventas)

billing
 ├── ventas              ← limpia: sin campos sunat_*
 ├── comprobante_series  ← generalizada: sin asumir formato SUNAT
 ├── venta_items
 ├── venta_cobros
 └── venta_origen
```

---

## Bajo Nivel: Modelo de Datos

### `fiscal.fiscal_sistemas` — Catálogo de sistemas tributarios

```sql
CREATE TABLE fiscal.fiscal_sistemas (
    id              varchar(20) PRIMARY KEY,  -- 'SUNAT', 'SRI', 'DIAN', 'SAT', 'INTERNO'
    nombre          varchar(100) NOT NULL,    -- 'SUNAT - Perú', 'SRI - Ecuador', etc.
    pais_codigo     varchar(3)  NOT NULL,     -- ISO 3166-1 alpha-3: 'PER', 'ECU', 'COL', 'MEX'
    moneda_default  varchar(3)  NOT NULL,     -- ISO 4217: 'PEN', 'USD', 'COP', 'MXN'
    -- Formato de serie: expresión que define cómo se construye la serie
    -- SUNAT: 1 letra + 3 dígitos → regex: [A-Z][0-9]{3}  ejemplo: B001, F001, NV01
    -- SRI:   3 dígitos (establecimiento) + 3 dígitos (punto emisión) → 001-001
    -- DIAN:  prefijo libre
    serie_formato   varchar(50),             -- descripción del formato (informativo)
    serie_regex     varchar(100),            -- regex de validación
    correlativo_padding int NOT NULL DEFAULT 8,  -- dígitos del correlativo: SUNAT=8, SRI=9
    separador       varchar(5) NOT NULL DEFAULT '-',  -- separador serie-correlativo
    activo          boolean NOT NULL DEFAULT true
);

-- Seed inicial
INSERT INTO fiscal.fiscal_sistemas VALUES
  ('SUNAT',    'SUNAT - Perú',     'PER', 'PEN', '[A-Z][0-9]{3}',    '[A-Z][0-9]{3}',    8, '-', true),
  ('SRI',      'SRI - Ecuador',    'ECU', 'USD', '[0-9]{3}-[0-9]{3}','[0-9]{3}-[0-9]{3}',9, '-', true),
  ('DIAN',     'DIAN - Colombia',  'COL', 'COP', NULL,               NULL,               10, '-', true),
  ('SAT',      'SAT - México',     'MEX', 'MXN', NULL,               NULL,               8,  '-', true),
  ('INTERNO',  'Interno (sin ente','---', '---', NULL,               NULL,               8,  '-', true);
```

### `fiscal.comprobante_tipos` — Tipos de comprobante por sistema

```sql
CREATE TABLE fiscal.comprobante_tipos (
    id                  uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    fiscal_sistema_id   varchar(20) NOT NULL REFERENCES fiscal.fiscal_sistemas(id),
    codigo              varchar(30) NOT NULL,   -- 'BOLETA', 'FACTURA', 'NOTA_VENTA', 'NOTA_CREDITO'
    nombre              varchar(100) NOT NULL,
    requiere_ruc        boolean NOT NULL DEFAULT false,  -- FACTURA requiere RUC del cliente
    genera_igv          boolean NOT NULL DEFAULT true,
    activo              boolean NOT NULL DEFAULT true,
    UNIQUE (fiscal_sistema_id, codigo)
);

-- Seed SUNAT
INSERT INTO fiscal.comprobante_tipos (fiscal_sistema_id, codigo, nombre, requiere_ruc, genera_igv) VALUES
  ('SUNAT', 'NOTA_VENTA', 'Nota de Venta',  false, false),
  ('SUNAT', 'BOLETA',     'Boleta de Venta',false, true),
  ('SUNAT', 'FACTURA',    'Factura',         true,  true);

-- Seed SRI
INSERT INTO fiscal.comprobante_tipos (fiscal_sistema_id, codigo, nombre, requiere_ruc, genera_igv) VALUES
  ('SRI', 'FACTURA',      'Factura',         true,  true),
  ('SRI', 'NOTA_CREDITO', 'Nota de Crédito', true,  false),
  ('SRI', 'TICKET',       'Ticket',          false, false);
```

### `fiscal.fiscal_config` — Configuración fiscal por sucursal (1:1)

```sql
CREATE TABLE fiscal.fiscal_config (
    sucursal_id         uuid PRIMARY KEY REFERENCES core.sucursales(id) ON DELETE CASCADE,
    fiscal_sistema_id   varchar(20) NOT NULL REFERENCES fiscal.fiscal_sistemas(id),
    moneda_codigo       varchar(3)  NOT NULL DEFAULT 'PEN',  -- puede diferir del default del sistema
    ruc_empresa         varchar(20),          -- RUC/NIT/RIF del emisor
    razon_social        varchar(200),         -- razón social del emisor
    direccion_fiscal    varchar(300),
    -- Configuración de impuestos
    tasa_igv            numeric(5,4) NOT NULL DEFAULT 0.18,  -- 0.18 = 18%
    igv_incluido        boolean NOT NULL DEFAULT true,       -- true = precio ya incluye IGV
    created_at          timestamp NOT NULL DEFAULT now(),
    updated_at          timestamp
);
```

### `billing.ventas` — Limpia, sin campos tributarios

```sql
-- Columnas a ELIMINAR de billing.ventas:
--   sunat_enviado, sunat_aceptado, sunat_codigo_hash, sunat_enviado_at
--   igv  ← también se elimina (pasa a venta_impuestos)

-- La tabla queda:
billing.ventas (
    id, sucursal_id, comprobante_serie_id,
    cliente_id, tipo_comprobante,
    serie, correlativo, numero_comprobante,
    subtotal,           -- suma de líneas antes de impuestos y descuentos
    descuento,          -- descuento global
    base_imponible,     -- subtotal - descuento (base sobre la que se calculan impuestos)
    total_impuestos,    -- suma de todas las líneas de venta_impuestos (calculado)
    total,              -- base_imponible + total_impuestos
    estado, created_at, anulada_at
)
```

### `billing.venta_impuestos` — Desglose de impuestos por venta

```sql
-- Cada fila es una línea de impuesto aplicado a la venta.
-- Una venta peruana típica tiene 1 fila: IGV 18%.
-- Una venta con ISC tendría 2 filas: IGV 18% + ISC 17%.
-- Una venta interna (NOTA_VENTA sin impuesto) tiene 0 filas.

CREATE TABLE billing.venta_impuestos (
    id              uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    venta_id        uuid NOT NULL REFERENCES billing.ventas(id) ON DELETE CASCADE,
    -- Referencia al tipo de impuesto del catálogo fiscal
    impuesto_id     varchar(20) NOT NULL REFERENCES fiscal.impuesto_tipos(id),
    -- Snapshot al momento de la venta (el catálogo puede cambiar)
    codigo          varchar(20) NOT NULL,   -- 'IGV', 'IVA', 'ISC', 'IEPS'
    nombre          varchar(100) NOT NULL,  -- 'Impuesto General a las Ventas'
    tasa            numeric(7,4) NOT NULL,  -- 0.1800 = 18%
    base            numeric(12,2) NOT NULL, -- monto sobre el que se aplica la tasa
    monto           numeric(12,2) NOT NULL, -- base * tasa (o monto fijo si es por unidad)
    incluido_precio boolean NOT NULL DEFAULT true  -- true = precio ya incluye este impuesto
);
```

### `fiscal.impuesto_tipos` — Catálogo de tipos de impuesto

```sql
CREATE TABLE fiscal.impuesto_tipos (
    id                  varchar(20) PRIMARY KEY,  -- 'IGV', 'IVA', 'ISC', 'IEPS', 'IVA_CO'
    fiscal_sistema_id   varchar(20) NOT NULL REFERENCES fiscal.fiscal_sistemas(id),
    codigo              varchar(20) NOT NULL,      -- código del ente tributario
    nombre              varchar(100) NOT NULL,
    descripcion         text,
    tasa_default        numeric(7,4) NOT NULL,     -- tasa vigente por defecto
    tipo_calculo        varchar(20) NOT NULL DEFAULT 'PORCENTAJE',
                        -- PORCENTAJE: monto = base * tasa
                        -- MONTO_FIJO: monto = tasa (por unidad)
                        -- PORCENTAJE_SOBRE_PRECIO: para ISC ad valorem
    activo              boolean NOT NULL DEFAULT true
);

-- Seed
INSERT INTO fiscal.impuesto_tipos VALUES
  ('IGV',    'SUNAT', 'IGV',  'Impuesto General a las Ventas',    0.1800, 'PORCENTAJE', true),
  ('ISC',    'SUNAT', 'ISC',  'Impuesto Selectivo al Consumo',    0.1700, 'PORCENTAJE', true),
  ('IVA_EC', 'SRI',   'IVA',  'Impuesto al Valor Agregado',       0.1200, 'PORCENTAJE', true),
  ('IVA_CO', 'DIAN',  'IVA',  'Impuesto al Valor Agregado',       0.1900, 'PORCENTAJE', true),
  ('IVA_MX', 'SAT',   'IVA',  'Impuesto al Valor Agregado',       0.1600, 'PORCENTAJE', true);
```

### `fiscal.fiscal_config` — Impuestos aplicables por sucursal

```sql
CREATE TABLE fiscal.fiscal_config (
    sucursal_id         uuid PRIMARY KEY REFERENCES core.sucursales(id) ON DELETE CASCADE,
    fiscal_sistema_id   varchar(20) NOT NULL REFERENCES fiscal.fiscal_sistemas(id),
    moneda_codigo       varchar(3)  NOT NULL DEFAULT 'PEN',
    ruc_empresa         varchar(20),
    razon_social        varchar(200),
    direccion_fiscal    varchar(300),
    -- Impuestos que aplican por defecto a las ventas de esta sucursal.
    -- Array de impuesto_tipos.id — el sistema no asume nombres de impuestos.
    -- Ejemplo Perú:          ARRAY['IGV']
    -- Ejemplo Perú con ISC:  ARRAY['IGV', 'ISC']
    -- Ejemplo Ecuador:       ARRAY['IVA_EC']
    -- Ejemplo sin impuesto:  ARRAY[]::varchar[]
    impuestos_default        varchar(20)[] NOT NULL DEFAULT ARRAY['IGV'],
    precios_incluyen_impuesto boolean NOT NULL DEFAULT true,
    created_at          timestamp NOT NULL DEFAULT now(),
    updated_at          timestamp
);
```

### `fiscal.venta_fiscal` — Anexo tributario (reemplaza campos sunat_*)

```sql
CREATE TABLE fiscal.venta_fiscal (
    venta_id            uuid PRIMARY KEY REFERENCES billing.ventas(id) ON DELETE CASCADE,
    fiscal_sistema_id   varchar(20) NOT NULL REFERENCES fiscal.fiscal_sistemas(id),
    enviado             boolean NOT NULL DEFAULT false,
    enviado_at          timestamp,
    aceptado            boolean,
    aceptado_at         timestamp,
    codigo_respuesta    varchar(50),
    mensaje_respuesta   text,
    xml_firmado         text,
    extra               jsonb
);
```

### `billing.comprobante_series` — Generalizada

```sql
-- Cambios:
-- 1. Ampliar serie de varchar(4) a varchar(20) para soportar formatos como '001-001' (SRI)
-- 2. Agregar referencia al tipo de comprobante del sistema fiscal

ALTER TABLE billing.comprobante_series
    ALTER COLUMN serie TYPE varchar(20);

-- La tabla queda:
billing.comprobante_series (
    id, sucursal_id, tipo_comprobante varchar(30),
    serie varchar(20),   -- ← ampliado
    correlativo_actual, correlativo_max, activo,
    created_at, updated_at
)
```

### `core.sucursales` — Agrega país y moneda

```sql
ALTER TABLE core.sucursales
    ADD COLUMN pais_codigo  varchar(3)  NOT NULL DEFAULT 'PER',
    ADD COLUMN moneda_codigo varchar(3) NOT NULL DEFAULT 'PEN';
-- fiscal_config se crea por separado (1:1 opcional hasta que se configure)
```

---

## Flujo de Creación de Sucursal

```
1. Admin crea empresa
2. Admin crea sucursal → elige: país, moneda
3. Sistema crea fiscal_config con el sistema tributario del país
   (PER → SUNAT, ECU → SRI, COL → DIAN, etc.)
4. Admin configura: RUC/NIT, razón social, tasa IGV
5. Admin crea series de comprobante con el formato del sistema
   (SUNAT: B001, F001 | SRI: 001-001, 001-002)
```

---

## Flujo de Creación de Venta

```
Antes:
  CreateVentaDirectaUseCase
    → igv = base * 0.18 / 1.18  (hardcodeado)
    → venta.igv = igv
    → venta.sunat_enviado = false

Después:
  CreateVentaDirectaUseCase
    → lee fiscal_config de la sucursal
        → impuestos_default = ['IGV']
        → precios_incluyen_impuesto = true
    → para cada impuesto en impuestos_default:
        → lee impuesto_tipos → tasa = 0.18, tipo = PORCENTAJE, incluido = true
        → calcula: monto = base * tasa / (1 + tasa)  si incluido_precio
                   monto = base * tasa                si no incluido
        → crea línea en billing.venta_impuestos
    → total_impuestos = SUM(venta_impuestos.monto)
    → total = base_imponible + total_impuestos  (si no incluido)
           ó = subtotal - descuento             (si incluido, impuesto ya está dentro)
    → crea billing.ventas (sin igv fijo)
    → crea fiscal.venta_fiscal (enviado=false)
```

### Cálculo de impuestos incluidos vs. no incluidos

```
Precio incluye impuesto (Perú, precio de venta al público):
  subtotal     = 100.00  (precio con IGV)
  descuento    =   0.00
  base         = 100.00
  IGV (18%)    =  15.25  = 100 * 0.18 / 1.18
  total        = 100.00  (no cambia, el IGV ya estaba dentro)

Precio NO incluye impuesto (B2B, precio neto):
  subtotal     = 100.00  (precio sin IVA)
  descuento    =   0.00
  base         = 100.00
  IVA (19%)    =  19.00  = 100 * 0.19
  total        = 119.00
```

---

## Generación de Número de Comprobante

```java
// Antes (hardcodeado SUNAT):
String numeroComprobante = serie + "-" + String.format("%08d", correlativo);

// Después (por sistema):
interface NumeroComprobanteFormatter {
    String format(String serie, int correlativo);
}

// SUNAT: B001-00000001
class SunatFormatter implements NumeroComprobanteFormatter {
    public String format(String serie, int correlativo) {
        return serie + "-" + String.format("%08d", correlativo);
    }
}

// SRI: 001-001-000000001
class SriFormatter implements NumeroComprobanteFormatter {
    public String format(String serie, int correlativo) {
        return serie + "-" + String.format("%09d", correlativo);
    }
}

// Factory por sistema:
NumeroComprobanteFormatter.forSistema(fiscalSistemaId)
```

---

## Migración de Datos Existentes

```sql
-- Paso 1: Crear esquema fiscal y tablas (fiscal_sistemas, impuesto_tipos, comprobante_tipos)
-- Paso 2: Seed de catálogos
-- Paso 3: Crear fiscal_config para sucursales existentes (SUNAT, PEN, impuestos_default=['IGV'])
-- Paso 4: Crear tabla billing.venta_impuestos
-- Paso 5: Migrar IGV existente → venta_impuestos
INSERT INTO billing.venta_impuestos (venta_id, impuesto_id, codigo, nombre, tasa, base, monto, incluido_precio)
SELECT
    v.id,
    'IGV',
    'IGV',
    'Impuesto General a las Ventas',
    0.18,
    v.subtotal - v.descuento,
    v.igv,
    true
FROM billing.ventas v
WHERE v.igv > 0;
-- Paso 6: Migrar datos sunat_* → fiscal.venta_fiscal
INSERT INTO fiscal.venta_fiscal (venta_id, fiscal_sistema_id, enviado, aceptado, codigo_respuesta)
SELECT id, 'SUNAT', sunat_enviado, sunat_aceptado, sunat_codigo_hash
FROM billing.ventas;
-- Paso 7: Agregar base_imponible, total_impuestos a billing.ventas; DROP igv, sunat_*
-- Paso 8: Ampliar serie en comprobante_series
-- Paso 9: Agregar pais_codigo, moneda_codigo a core.sucursales
```

---

## Impacto en Código Java

| Clase | Cambio |
|---|---|
| `VentaEntity` | Eliminar `igv`, `sunatEnviado`, `sunatAceptado`, `sunatCodigoHash`; agregar `baseImponible`, `totalImpuestos`; relación con `VentaImpuestoEntity` |
| `Venta` (dominio) | Ídem |
| `VentaDTO` | Reemplazar `igv` por `totalImpuestos`; agregar `impuestos: List<VentaImpuestoDTO>`; reemplazar `sunatEnviado` por `fiscalEnviado` |
| `CreateVentaDirectaUseCase` | Leer `fiscal_config`, calcular impuestos por línea, usar formatter por sistema |
| `CerrarComandaUseCase` | Ídem |
| `BuscarVentasUseCase` | Incluir join con `venta_impuestos` y `venta_fiscal` |
| `SucursalEntity` | Agregar `paisCodigo`, `monedaCodigo` |
| Nuevo: `FiscalConfigEntity` | `fiscal.fiscal_config` |
| Nuevo: `VentaFiscalEntity` | `fiscal.venta_fiscal` |
| Nuevo: `VentaImpuestoEntity` | `billing.venta_impuestos` |
| Nuevo: `ImpuestoTipoEntity` | `fiscal.impuesto_tipos` |
| Nuevo: `NumeroComprobanteFormatter` | Interface + impl por sistema |
| Nuevo: `ImpuestoCalculator` | Calcula líneas de impuesto según `fiscal_config` |
