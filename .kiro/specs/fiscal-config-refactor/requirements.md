# Requerimientos: Refactorización Fiscal Multi-País

## R1 — Configuración fiscal por sucursal

- Al crear una sucursal se debe poder seleccionar el país (ISO 3166-1 alpha-3) y la moneda (ISO 4217)
- El sistema tributario se asigna automáticamente según el país (PER→SUNAT, ECU→SRI, COL→DIAN, MEX→SAT)
- La configuración fiscal de la sucursal incluye: RUC/NIT del emisor, razón social, dirección fiscal, tasa de impuesto y si el precio incluye impuesto
- Una sucursal sin `fiscal_config` puede operar en modo `INTERNO` (sin envío a ente tributario)

## R2 — Catálogo de sistemas tributarios

- El sistema debe tener un catálogo de sistemas tributarios (`fiscal_sistemas`) con: id, nombre, país, moneda default, formato de serie, padding del correlativo y separador
- El catálogo es mantenido por SUPER_ADMIN y no por el cliente
- Los tipos de comprobante (`comprobante_tipos`) son específicos por sistema tributario

## R3 — Series de comprobante generalizadas

- El formato de la serie debe respetar el `serie_regex` del sistema tributario configurado en la sucursal
- El padding del correlativo debe usar el `correlativo_padding` del sistema (SUNAT=8, SRI=9)
- El número de comprobante se genera como: `serie + separador + correlativo.padStart(padding, '0')`
- La validación del formato de serie al crear/editar debe usar el regex del sistema

## R4 — Desacoplamiento de datos tributarios de la venta

- La tabla `billing.ventas` no debe contener campos específicos de ningún sistema tributario ni impuesto fijo
- El campo `igv` se elimina de `billing.ventas` y se reemplaza por `billing.venta_impuestos` (N líneas por venta)
- Los datos de envío al ente tributario viven en `fiscal.venta_fiscal`
- `venta_fiscal` es opcional: una venta puede existir sin anexo fiscal (modo INTERNO)

## R5 — Modelo de impuestos por línea

- Cada venta puede tener cero o más líneas de impuesto en `billing.venta_impuestos`
- Cada línea referencia un `fiscal.impuesto_tipos` (IGV, IVA, ISC, IEPS, etc.)
- La línea guarda un snapshot de: código, nombre, tasa y monto calculado (inmutable después de emitida)
- El campo `incluido_precio` indica si el impuesto ya estaba dentro del precio o se suma encima
- `billing.ventas.total_impuestos` es la suma de todas las líneas (campo calculado/desnormalizado para performance)
- Una venta con `tipo_comprobante = NOTA_VENTA` puede tener 0 líneas de impuesto

## R5 — Compatibilidad hacia atrás

- Las ventas existentes deben migrarse: sus datos `sunat_*` pasan a `fiscal.venta_fiscal` con `fiscal_sistema_id = 'SUNAT'`
- Las sucursales existentes reciben `pais_codigo = 'PER'`, `moneda_codigo = 'PEN'` y una `fiscal_config` con sistema SUNAT
- El comportamiento actual de generación de comprobantes SUNAT no debe cambiar para sucursales peruanas

## R6 — Extensibilidad

- Agregar un nuevo sistema tributario debe requerir solo: insertar en `fiscal_sistemas`, insertar en `comprobante_tipos`, e implementar `NumeroComprobanteFormatter`
- No debe requerir cambios en `billing.ventas`, `VentaEntity` ni en los use cases principales
