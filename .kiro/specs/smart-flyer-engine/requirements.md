# Requirements Document

## Introduction

El Smart Flyer Engine es una evolución del motor de diseño de flyers de la plataforma Audrey.
El sistema actual genera flyers rellenando capas con posiciones fijas usando un agente GOAP + RAG + LLM (gpt-4o-mini).
Esta feature transforma ese motor en un director creativo inteligente: el LLM pasa de rellenar plantillas rígidas
a tomar decisiones de composición visual dentro de zonas semánticas, analizar imágenes de productos con visión,
extraer paletas automáticamente, vectorizar plantillas para RAG semántico, generar múltiples variaciones en paralelo,
persistir el historial de flyers, soportar hasta 3 productos por flyer, y enriquecer el editor Fabric.js con
controles de texto, deshacer/rehacer, zoom y descarga en múltiples formatos/resoluciones.

El sistema abarca tres componentes: **audreySW** (frontend Angular 20 + Fabric.js), **audreySS** (API gateway Spring Boot 4 hexagonal) y **newEmbabelDesing** (microservicio Spring Boot 3.3 con Embabel Agent Framework GOAP, Spring AI, PgVector).

---

## Glossary

- **Design_Engine**: El microservicio `newEmbabelDesing` que ejecuta el agente GOAP de diseño.
- **API_Gateway**: El servicio `audreySS` que actúa como proxy HTTP entre el frontend y el Design_Engine.
- **Canvas_Editor**: El componente Angular en `audreySW` que renderiza y edita flyers con Fabric.js.
- **Flyer_Agent**: El agente GOAP `FlyerDesignAgent` dentro del Design_Engine.
- **Zona_Semantica**: Área lógica de una plantilla (ej. `zona_hero`, `zona_precio`, `zona_cta`, `zona_producto`) que define propósito y restricciones de espacio, sin coordenadas fijas.
- **ADN_Visual**: Conjunto de metadatos descriptivos de una plantilla: estilo, mood, industria, paleta sugerida, usados para RAG semántico.
- **Vision_Analyzer**: Componente del Flyer_Agent que usa gpt-4o con capacidad de visión para analizar imágenes de productos.
- **Color_Extractor**: Módulo ColorThief ejecutado en el Canvas_Editor para extraer paleta dominante de la imagen del producto.
- **Recurso_Visual**: Fondo u overlay almacenado en `ai_engine.recursos_visuales` con embedding vectorial y metadata enriquecida.
- **Plantilla_Vectorizada**: Plantilla almacenada en `ai_engine.plantillas_vectorizadas` con embedding de su ADN_Visual.
- **Flyer_Generado**: Registro persistido en `design.flyers_generados` que representa un flyer producido por el sistema.
- **Variacion**: Una de las 3 composiciones alternativas devueltas por el endpoint de generación.
- **LayerType**: Enum que define el tipo de capa: `BACKGROUND`, `OVERLAY`, `PRODUCT`, `PRODUCT_2`, `PRODUCT_3`, `TEXT_TITLE`, `TEXT_SUBTITLE`, `TEXT_PRICE`, `TEXT_CTA`, `LOGO`.
- **Palette**: Objeto con tres colores hexadecimales: `principal`, `secundario`, `contraste`.
- **TipoUso**: Enum de propósito del flyer: `IMPULSAR`, `OFERTA`, `NUEVO_PRODUCTO`, `EVENTO`.
- **Formato**: Relación de aspecto del canvas: `9:16`, `1:1`, `16:9`, `4:5`.
- **Similarity_Threshold**: Umbral mínimo de similitud coseno (0.0–1.0) para filtrar resultados del RAG.

---

## Requirements

### Requirement 1: LLM como Director Creativo con Zonas Semánticas

**User Story:** Como diseñador de marketing, quiero que el LLM decida la composición visual dentro de zonas semánticas, para obtener flyers con mayor creatividad y coherencia visual que los generados con posiciones fijas.

#### Acceptance Criteria

1. THE Design_Engine SHALL definir plantillas mediante Zonas_Semanticas (zona_hero, zona_precio, zona_cta, zona_producto) en lugar de coordenadas x/y/width/height fijas.
2. WHEN el Flyer_Agent recibe una solicitud de generación, THE Flyer_Agent SHALL enviar al LLM las Zonas_Semanticas de la plantilla seleccionada junto con el contexto del producto.
3. WHEN el LLM procesa las Zonas_Semanticas, THE Flyer_Agent SHALL permitir que el LLM decida para cada zona: fuente (fontFamily), tamaño de fuente (fontSize en px), peso (fontWeight), color de texto (colorHex), alineación (textAlign) y contenido textual.
4. THE Design_Engine SHALL usar el modelo gpt-4o (con capacidad de visión) como LLM principal para la generación de layout, reemplazando gpt-4o-mini en el paso de composición.
5. IF el LLM devuelve un layout con zonas faltantes o valores inválidos, THEN THE Flyer_Agent SHALL aplicar valores por defecto definidos en la Zona_Semantica correspondiente y registrar una advertencia en el log.
6. THE Flyer_Agent SHALL garantizar que las coordenadas relativas (x, y, width, height en rango 0.0–1.0) de cada zona no sean modificadas por el LLM; solo el contenido y estilo visual son mutables.
7. FOR ALL layouts generados por el LLM, THE Design_Engine SHALL producir un AiFlyerDTO con capas cuyas coordenadas relativas estén dentro del rango [0.0, 1.0] (invariante de coordenadas).

---

### Requirement 2: Vision API — Análisis de Imagen del Producto

**User Story:** Como usuario de Audrey, quiero que el sistema analice automáticamente la imagen de mi producto, para que la composición del flyer sea coherente con los colores y características visuales del producto.

#### Acceptance Criteria

1. WHEN el Flyer_Agent recibe una solicitud con `urlImagen` no nula, THE Vision_Analyzer SHALL invocar gpt-4o vision para analizar la imagen del producto antes de la generación del layout.
2. THE Vision_Analyzer SHALL extraer de la imagen: colores dominantes (máximo 5 valores hexadecimales), tipo de producto (categoría textual), y si el fondo es transparente o blanco (booleano).
3. WHEN el Vision_Analyzer completa el análisis, THE Flyer_Agent SHALL incluir los resultados del análisis en el prompt de composición enviado al LLM director creativo.
4. WHEN el análisis de visión indica que el fondo del producto es transparente o blanco, THE Flyer_Agent SHALL sugerir al LLM aplicar máscara circular (`applyCircularMask: true`) en la capa PRODUCT.
5. IF la URL de imagen del producto no es accesible o el análisis de visión falla, THEN THE Vision_Analyzer SHALL omitir el análisis y continuar la generación sin datos de visión, registrando el error.
6. THE Vision_Analyzer SHALL completar el análisis de imagen en menos de 10 segundos para no bloquear el pipeline de generación.

---

### Requirement 3: Extracción Automática de Paleta de Colores

**User Story:** Como usuario de Audrey, quiero que el sistema extraiga automáticamente la paleta de colores de la imagen de mi producto, para no tener que elegir colores manualmente.

#### Acceptance Criteria

1. WHEN el Canvas_Editor carga la imagen de un producto seleccionado con `imagenUrl` no nula, THE Color_Extractor SHALL ejecutar ColorThief sobre la imagen para extraer una paleta de hasta 5 colores dominantes.
2. THE Canvas_Editor SHALL pre-rellenar los campos de paleta (`principal`, `secundario`, `contraste`) con los 3 colores más representativos extraídos por el Color_Extractor.
3. WHILE el usuario está en el paso de opciones (step = 'OPCIONES'), THE Canvas_Editor SHALL permitir que el usuario modifique manualmente cualquiera de los 3 colores de la paleta pre-rellenada.
4. THE Canvas_Editor SHALL enviar la paleta resultante (ya sea automática o ajustada manualmente) al API_Gateway como parte del cuerpo de la solicitud de generación.
5. IF la imagen del producto no es accesible o ColorThief no puede extraer colores, THEN THE Canvas_Editor SHALL mantener la paleta por defecto (`#6366f1`, `#818cf8`, `#f59e0b`) y no mostrar error al usuario.
6. THE Color_Extractor SHALL completar la extracción de paleta en menos de 3 segundos en el navegador del usuario.

---

### Requirement 4: Vectorización Semántica de Plantillas

**User Story:** Como administrador de Audrey, quiero que las plantillas se seleccionen semánticamente según el contexto del producto, para que el RAG elija la plantilla más adecuada en lugar de filtrar solo por tipo_uso y formato.

#### Acceptance Criteria

1. THE Design_Engine SHALL mantener una tabla `ai_engine.plantillas_vectorizadas` con columnas: `id` (UUID), `plantilla_id` (referencia a `design.plantillas`), `adn_visual` (JSONB con estilo, mood, industria, paleta_sugerida), `embedding` (VECTOR(1536)), `created_at` (TIMESTAMP).
2. WHEN una plantilla es creada o actualizada en `design.plantillas`, THE Design_Engine SHALL generar un embedding del ADN_Visual usando `text-embedding-3-small` y almacenarlo en `ai_engine.plantillas_vectorizadas`.
3. WHEN el Flyer_Agent necesita seleccionar una plantilla, THE Flyer_Agent SHALL construir una query semántica combinando TipoUso, Formato y contexto del producto, y recuperar la Plantilla_Vectorizada con mayor similitud coseno.
4. THE Design_Engine SHALL aplicar un Similarity_Threshold mínimo de 0.3 para la recuperación de plantillas vectorizadas; si ninguna supera el umbral, THE Flyer_Agent SHALL usar el filtro por tipo_uso/formato como fallback.
5. THE Design_Engine SHALL crear un índice HNSW sobre la columna `embedding` de `ai_engine.plantillas_vectorizadas` para búsqueda eficiente.
6. FOR ALL plantillas activas en `design.plantillas`, THE Design_Engine SHALL mantener un embedding vigente en `ai_engine.plantillas_vectorizadas` (propiedad de consistencia: toda plantilla activa tiene su vector).

---

### Requirement 5: Gestión Enriquecida de Recursos Visuales

**User Story:** Como administrador de Audrey, quiero que los fondos y overlays tengan metadata rica y se recuperen con filtros combinados, para que el RAG devuelva recursos visualmente coherentes con el producto y el contexto.

#### Acceptance Criteria

1. THE Design_Engine SHALL extender la tabla `ai_engine.recursos_visuales` con los campos de metadata JSONB: `estilo` (ej. "minimalista", "vibrante"), `mood` (ej. "energético", "elegante"), `industria` (ej. "alimentos", "tecnología"), `colores_predominantes` (array de hex).
2. WHEN el Flyer_Agent ejecuta el paso RAG de recuperación de recursos, THE Flyer_Agent SHALL construir una query enriquecida que incluya TipoUso, contexto del producto y resultados del Vision_Analyzer (si disponibles).
3. THE Flyer_Agent SHALL aplicar un Similarity_Threshold configurable (valor por defecto 0.5, mínimo 0.0, máximo 1.0) para filtrar Recursos_Visuales recuperados por RAG.
4. THE Flyer_Agent SHALL combinar filtros de metadata GIN (tipo de recurso: fondo/overlay, industria) con búsqueda por embedding coseno en una sola consulta a PgVector.
5. IF el RAG no encuentra recursos con similitud superior al Similarity_Threshold, THEN THE Flyer_Agent SHALL reducir el umbral en 0.1 hasta un mínimo de 0.0 y reintentar, registrando el umbral efectivo usado.
6. THE Design_Engine SHALL exponer el Similarity_Threshold como parámetro configurable en `application.yaml` bajo la clave `design-engine.rag.similarity-threshold`.

---

### Requirement 6: Historial de Flyers Generados

**User Story:** Como usuario de Audrey, quiero acceder a mis flyers generados anteriormente, para poder recuperarlos, regenerarlos o editarlos sin tener que volver a configurar todo desde cero.

#### Acceptance Criteria

1. THE API_Gateway SHALL persistir cada flyer generado exitosamente en la tabla `design.flyers_generados` con los campos: `id` (UUID), `empresa_id` (UUID), `usuario_id` (UUID), `producto_ids` (JSONB array), `tipo_uso` (TipoUso), `formato` (Formato), `palette` (JSONB), `ai_flyer_dto` (JSONB con el layout completo), `plantilla_id` (UUID nullable), `created_at` (TIMESTAMP), `updated_at` (TIMESTAMP).
2. WHEN un usuario solicita su historial, THE API_Gateway SHALL devolver los Flyers_Generados de la empresa del usuario ordenados por `created_at` descendente, con paginación de 20 registros por página.
3. WHEN un usuario solicita regenerar un Flyer_Generado, THE API_Gateway SHALL reenviar la solicitud original al Design_Engine y actualizar el registro existente con el nuevo `ai_flyer_dto` y `updated_at`.
4. THE API_Gateway SHALL exponer un endpoint `GET /api/v1/design/flyers` para listar el historial y `GET /api/v1/design/flyers/{id}` para recuperar un flyer específico.
5. THE API_Gateway SHALL exponer un endpoint `POST /api/v1/design/flyers/{id}/regenerar` para regenerar un flyer del historial.
6. THE API_Gateway SHALL exponer un endpoint `GET /api/v1/design/analytics/productos-mas-promovidos` que devuelva los 10 productos con mayor frecuencia en `design.flyers_generados.producto_ids` para la empresa del usuario autenticado.
7. IF un usuario intenta acceder a un Flyer_Generado de otra empresa, THEN THE API_Gateway SHALL devolver HTTP 403 Forbidden.

---

### Requirement 7: Generación de Múltiples Variaciones en Paralelo

**User Story:** Como usuario de Audrey, quiero recibir 3 variaciones del flyer con diferentes estilos, para elegir la que mejor represente mi producto sin tener que regenerar manualmente.

#### Acceptance Criteria

1. WHEN el Design_Engine recibe una solicitud de generación, THE Flyer_Agent SHALL generar 3 Variaciones del flyer en paralelo usando `CompletableFuture`, cada una con una Plantilla_Vectorizada diferente.
2. THE Design_Engine SHALL devolver las 3 Variaciones en el cuerpo de la respuesta como un array JSON `variaciones`, donde cada elemento es un AiFlyerDTO con un campo adicional `variacionId` (entero 1–3) y `plantillaNombre` (String).
3. THE API_Gateway SHALL actualizar el contrato del endpoint `POST /api/v1/design/flyer` para devolver el nuevo formato con el array `variaciones`.
4. THE Canvas_Editor SHALL mostrar las 3 Variaciones como miniaturas seleccionables; al seleccionar una, THE Canvas_Editor SHALL renderizar esa variación en el canvas principal.
5. IF una de las 3 generaciones paralelas falla, THEN THE Design_Engine SHALL incluir en la respuesta las variaciones exitosas (mínimo 1) y omitir las fallidas, sin propagar el error al cliente.
6. THE Design_Engine SHALL completar la generación de las 3 variaciones en paralelo en menos de 30 segundos bajo condiciones normales de red.
7. FOR ALL conjuntos de 3 variaciones generadas para la misma solicitud, THE Design_Engine SHALL garantizar que cada variación use una plantilla distinta (propiedad de diversidad: sin plantillas duplicadas en el mismo conjunto).

---

### Requirement 8: Soporte Multi-Producto (hasta 3 productos)

**User Story:** Como usuario de Audrey, quiero incluir hasta 3 productos en un mismo flyer, para crear composiciones comparativas o promociones de combo.

#### Acceptance Criteria

1. THE Canvas_Editor SHALL permitir seleccionar entre 1 y 3 productos para un flyer, actualizando la constante `MAX` de 1 a 3.
2. THE API_Gateway SHALL aceptar en el cuerpo de la solicitud `POST /api/v1/design/flyer` un array `productos` de 1 a 3 elementos de tipo `ProductInfo` (nombre, precio, urlImagen).
3. THE Design_Engine SHALL extender el enum `LayerType` con los valores `PRODUCT_2` y `PRODUCT_3` para soportar capas de segundo y tercer producto.
4. WHEN el Flyer_Agent recibe una solicitud con 2 o 3 productos, THE Flyer_Agent SHALL incluir en el prompt al LLM la cantidad de productos y sus datos, para que el LLM adapte la composición de las Zonas_Semanticas.
5. WHEN el Flyer_Agent recibe una solicitud con 1 producto, THE Flyer_Agent SHALL comportarse de forma idéntica al comportamiento actual (compatibilidad hacia atrás).
6. IF una plantilla no tiene zonas para PRODUCT_2 o PRODUCT_3 y la solicitud incluye más de 1 producto, THEN THE Flyer_Agent SHALL notificar al LLM la restricción y el LLM SHALL adaptar la zona_producto existente para mostrar múltiples productos.
7. THE Canvas_Editor SHALL renderizar correctamente capas de tipo PRODUCT_2 y PRODUCT_3 en Fabric.js con las mismas reglas de escalado y máscara circular que PRODUCT.

---

### Requirement 9: Editor Enriquecido en Fabric.js

**User Story:** Como usuario de Audrey, quiero editar el flyer generado con controles de texto, deshacer/rehacer y zoom, para personalizar el resultado final sin salir de la plataforma.

#### Acceptance Criteria

1. THE Canvas_Editor SHALL mostrar un panel lateral con controles de tipografía cuando el usuario selecciona una capa de texto en modo edición: selector de fontFamily (mínimo 5 opciones), input numérico de fontSize (rango 8–120 px), selector de fontWeight (normal/bold), selector de color (colorHex), y selector de textAlign (left/center/right).
2. WHEN el usuario modifica un control tipográfico en el panel lateral, THE Canvas_Editor SHALL aplicar el cambio a la capa de texto seleccionada en el canvas en tiempo real (sin recargar el flyer).
3. THE Canvas_Editor SHALL implementar deshacer (Ctrl+Z / Cmd+Z) y rehacer (Ctrl+Y / Cmd+Shift+Z) con un historial de hasta 50 estados del canvas.
4. WHEN el usuario presiona Ctrl+Z, THE Canvas_Editor SHALL revertir el último cambio aplicado al canvas; WHEN el usuario presiona Ctrl+Y, THE Canvas_Editor SHALL reaplicar el último cambio deshecho.
5. THE Canvas_Editor SHALL implementar zoom con los controles: botón "+" (incremento 10%), botón "-" (decremento 10%), y botón "Reset" (100%), con rango de zoom entre 25% y 300%.
6. THE Canvas_Editor SHALL permitir reemplazar la imagen de fondo (capa BACKGROUND) mediante un selector de archivo local o una URL, actualizando la capa en el canvas sin regenerar el flyer completo.
7. WHILE el usuario está en modo edición, THE Canvas_Editor SHALL mostrar el nivel de zoom actual como porcentaje en la interfaz.

---

### Requirement 10: Descarga en Múltiples Formatos y Resoluciones

**User Story:** Como usuario de Audrey, quiero descargar el flyer en PNG o JPG con resolución configurable, para usarlo directamente en redes sociales o materiales impresos.

#### Acceptance Criteria

1. THE Canvas_Editor SHALL ofrecer al usuario la selección de formato de descarga: PNG o JPG.
2. THE Canvas_Editor SHALL ofrecer al usuario la selección de resolución de descarga: 1x (tamaño base del canvas), 2x (doble resolución) y 3x (triple resolución).
3. WHEN el usuario confirma la descarga, THE Canvas_Editor SHALL exportar el canvas de Fabric.js usando `toDataURL` con el formato y multiplicador seleccionados, y descargar el archivo con nombre `flyer-{timestamp}.{formato}`.
4. WHEN el formato seleccionado es JPG, THE Canvas_Editor SHALL usar calidad de compresión 0.92 en la exportación.
5. IF el canvas está contaminado (canvasTainted = true) por imágenes con restricción CORS, THEN THE Canvas_Editor SHALL mostrar una advertencia al usuario antes de la descarga indicando que algunas imágenes pueden no aparecer en el archivo exportado.
6. THE Canvas_Editor SHALL deshabilitar el botón de descarga mientras el flyer está siendo generado (generando = true).

---

### Requirement 11: Compatibilidad y Contratos de API

**User Story:** Como desarrollador de Audrey, quiero que los cambios en los contratos de API sean retrocompatibles o versionados, para no romper integraciones existentes durante la migración.

#### Acceptance Criteria

1. THE API_Gateway SHALL mantener el endpoint `POST /api/v1/design/flyer` existente y extenderlo para aceptar tanto el formato legacy (campo `productInfo` singular) como el nuevo formato (array `productos`), resolviendo la ambigüedad en favor del array si ambos están presentes.
2. WHEN el API_Gateway recibe una solicitud en formato legacy con `productInfo` singular, THE API_Gateway SHALL convertirlo internamente a un array de un elemento antes de enviarlo al Design_Engine.
3. THE Design_Engine SHALL exponer el endpoint `POST /api/v1/design/generate` como nuevo contrato canónico que acepta el array `productos` y devuelve el array `variaciones`.
4. THE API_Gateway SHALL propagar los headers de autenticación y el `empresa_id` del usuario autenticado en todas las solicitudes al Design_Engine.
5. IF el Design_Engine no está disponible, THEN THE API_Gateway SHALL devolver HTTP 503 Service Unavailable con un mensaje descriptivo en menos de 5 segundos (timeout de circuit breaker).

