# Implementation Plan: Smart Flyer Engine

## Overview

Upgrade the flyer generation system across three components — **newEmbabelDesing** (GOAP design engine), **audreySS** (API gateway), and **audreySW** (Angular frontend) — to support semantic zones, Vision API, multi-product layouts, parallel variations, historial persistence, and an enriched Fabric.js editor.

Tasks are ordered so every dependency (DB migrations, domain models, ports) is implemented before the code that consumes it.

---

## Tasks


<!-- ═══════════════════════════════════════════════════════════════════════ -->
<!-- COMPONENT A — newEmbabelDesing                                         -->
<!-- ═══════════════════════════════════════════════════════════════════════ -->

## A. newEmbabelDesing — Design Engine

- [x] A1. Apply Liquibase DB migrations
  - [x] A1.1 Create migration 10: `ai_engine.plantillas_vectorizadas` table
    - Add file `src/main/resources/db/changelog/migrations/V10__plantillas_vectorizadas.sql`
    - Table columns: `id UUID PK`, `plantilla_id UUID FK → design.plantillas(id) ON DELETE CASCADE`, `adn_visual JSONB NOT NULL`, `embedding VECTOR(1536) NOT NULL`, `created_at TIMESTAMP DEFAULT now()`, `UNIQUE(plantilla_id)`
    - Create HNSW index: `CREATE INDEX idx_plantillas_vec_embedding ON ai_engine.plantillas_vectorizadas USING hnsw (embedding vector_cosine_ops)`
    - Register in `db.changelog-master.yaml`
    - _Requirements: 4.1, 4.5_

  - [x] A1.2 Create migration 11: extend `ai_engine.recursos_visuales`
    - Add file `src/main/resources/db/changelog/migrations/V11__recursos_visuales_metadata.sql`
    - `ALTER TABLE ai_engine.recursos_visuales ADD COLUMN IF NOT EXISTS estilo TEXT, ADD COLUMN IF NOT EXISTS mood TEXT, ADD COLUMN IF NOT EXISTS industria TEXT, ADD COLUMN IF NOT EXISTS colores_predominantes JSONB`
    - `CREATE INDEX IF NOT EXISTS idx_recursos_gin_metadata ON ai_engine.recursos_visuales USING gin (metadata)`
    - Register in `db.changelog-master.yaml`
    - _Requirements: 5.1_

- [x] A2. Add new domain models
  - [x] A2.1 Create `ZonaSemantica` record
    - Fields: `nombre`, `tipo` (LayerType), `x`, `y`, `width`, `height` (all double, immutable [0.0–1.0]), `zIndex`, `applyCircularMask`, `colorHint`, `textHint`, `fontFamilyDefault`, `fontSizeDefault`, `fontWeightDefault`, `textAlignDefault`
    - Place in `engine/domain/model/ZonaSemantica.java`
    - _Requirements: 1.1, 1.6_

  - [x] A2.2 Extend `LayerType` enum and `Layer` record
    - Add `PRODUCT_2`, `PRODUCT_3`, `TEXT_CTA` to `LayerType`
    - Add nullable typography fields to `Layer` record: `fontFamily`, `fontSize` (Integer), `fontWeight`, `textAlign`
    - _Requirements: 8.3, 1.3_

  - [x] A2.3 Create `VisionAnalysis`, `FlyerContextV2`, `AdnVisual` records
    - `VisionAnalysis`: `List<String> coloresDominantes`, `String tipoProducto`, `boolean fondoTransparenteOBlanco`
    - `FlyerContextV2`: `List<ProductInfo> productos` (1–3), `Palette palette`, `String formato`, `String tipoUso`, `VisionAnalysis visionAnalysis` (nullable), `UUID plantillaId` (nullable)
    - `AdnVisual`: `String estilo`, `String mood`, `String industria`, `List<String> paletaSugerida`
    - Place in `engine/domain/model/`
    - _Requirements: 2.2, 8.2, 4.1_

  - [x] A2.4 Create `Variacion` and `FlyerVariacionesDTO` records
    - `Variacion`: `int variacionId`, `String plantillaNombre`, `List<Layer> layers`, `String formato`, `String descripcion`
    - `FlyerVariacionesDTO`: `List<Variacion> variaciones`
    - Place in `engine/domain/model/`
    - _Requirements: 7.2_

  - [x] A2.5 Create `GenerateRequestDTO` and `PlantillaVectorizadaResult` records
    - `GenerateRequestDTO`: `@NotNull @Size(min=1,max=3) List<ProductInfoDTO> productos`, `@NotNull PaletteDTO palette`, `String formato`, `String tipoUso`, `UUID plantillaId`
    - `PlantillaVectorizadaResult`: `UUID plantillaId`, `String nombre`, `double similarity`, `AdnVisual adnVisual`
    - Place in `engine/domain/model/` and `engine/infrastructure/web/dto/` respectively
    - _Requirements: 11.3, 4.1_

- [x] A3. Add `PlantillaVectorizadaPort` domain port and JPA adapter
  - [x] A3.1 Create `PlantillaVectorizadaPort` interface
    - Methods: `void upsertEmbedding(UUID plantillaId, AdnVisual adnVisual, float[] embedding)` and `List<PlantillaVectorizadaResult> buscarSimilares(float[] queryEmbedding, int topK, double similarityThreshold)`
    - Place in `engine/domain/port/PlantillaVectorizadaPort.java`
    - _Requirements: 4.2, 4.3_

  - [x] A3.2 Implement `PlantillaVectorizadaAdapter`
    - JPA entity `PlantillaVectorizadaEntity` mapped to `ai_engine.plantillas_vectorizadas`
    - Spring Data repository `PlantillaVectorizadaRepository` with native query for cosine similarity search using `<=>` operator and threshold filter
    - Adapter class implementing `PlantillaVectorizadaPort`, registered as `@Component`
    - _Requirements: 4.1, 4.3, 4.5_

- [x] A4. Implement new GOAP actions
  - [x] A4.1 Implement `VisionAnalyzerAction`
    - Input type: `FlyerContextV2` → Output type: `VisionEnrichedContext` (new wrapper record)
    - Call `chatClient` with `gpt-4o` vision using first product's `urlImagen`; parse response into `VisionAnalysis`
    - On any exception or null URL: return `VisionEnrichedContext(context, null)` and log WARN
    - Annotate with `@Action`
    - _Requirements: 2.1, 2.2, 2.5_

  - [ ]* A4.2 Write unit test for `VisionAnalyzerAction` fallback
    - Mock `ChatClient` to throw `RuntimeException`
    - Assert returned `VisionEnrichedContext.visionAnalysis()` is null
    - Assert no exception propagates
    - _Requirements: 2.5_

  - [x] A4.3 Implement `SeleccionarPlantillasAction`
    - Input type: `VisionEnrichedContext` → Output type: `PlantillasSeleccionadas` (new wrapper record holding `List<PlantillaVectorizadaResult>`)
    - Build semantic query from `tipoUso`, `formato`, product context, and vision results
    - Call `PlantillaVectorizadaPort.buscarSimilares` with `topK=3`, threshold from config (`design-engine.rag.plantillas-threshold`, default 0.3)
    - If fewer than 3 results above threshold, fall back to `PlantillasRegistry.getByTipoUso` + format filter to fill up to 3 distinct entries
    - Annotate with `@Action`
    - _Requirements: 4.3, 4.4, 7.1_

  - [x] A4.4 Implement `RecuperarRecursosAction`
    - Input type: `PlantillasSeleccionadas` → Output type: `RecursosRecuperados` (update existing record to carry `FlyerContextV2` instead of `FlyerContext`)
    - Build enriched RAG query including vision analysis results when available
    - Apply GIN metadata filter (`industria`, resource type) combined with cosine embedding search
    - Adaptive threshold: start at `design-engine.rag.similarity-threshold` (0.5), reduce by 0.1 until 0.0 if no results; log effective threshold
    - Annotate with `@Action`
    - _Requirements: 5.2, 5.3, 5.4, 5.5_

  - [x] A4.5 Implement `GenerarVariacionesAction`
    - Input type: `RecursosRecuperados` → Output type: `FlyerVariacionesDTO` (goal)
    - Launch 3 `CompletableFuture<Variacion>` via `executor`, each calling `generarVariacion(i, recursos, plantillas.get(i-1))`
    - Collect with 25s timeout per future; filter nulls; if list empty throw `FlyerGenerationException`
    - Annotate with `@AchievesGoal` + `@Action`
    - _Requirements: 7.1, 7.5, 7.6_

  - [ ]* A4.6 Write unit test for `GenerarVariacionesAction` diversity invariant
    - Provide 3 distinct `PlantillaVectorizadaResult` mocks
    - Assert returned `FlyerVariacionesDTO.variaciones()` has 3 elements with distinct `plantillaNombre` values
    - _Requirements: 7.7_

- [x] A5. Update `FlyerDesignAgent` to wire new GOAP chain
  - Replace `FlyerContext` input with `FlyerContextV2` as the external entry point
  - Remove old `recuperarRecursos` and `generarLayout` actions (now replaced by A4.x actions)
  - Update `recuperarRecursosDirecto` and `generarLayoutDirecto` public API methods to accept `FlyerContextV2`
  - Replace `gpt-4o-mini` with `gpt-4o` in `ChatClient` usage
  - Update `buildSystemPrompt` to describe semantic zones and typography decisions (fontFamily, fontSize, fontWeight, textAlign, colorHex per TEXT_* layer)
  - Update `buildUserPrompt` to serialize `ZonaSemantica` fields (including typography defaults) and multi-product list
  - Update fallback builder to handle `FlyerContextV2.productos` list
  - _Requirements: 1.2, 1.3, 1.4, 1.5, 8.4, 8.5_

- [x] A6. Update `PlantillasRegistry` fallback plantillas to use `ZonaSemantica`
  - Replace `CapaPlantilla` with `ZonaSemantica` in `buildImpulsar9_16`, `buildImpulsar1_1`, `buildImpulsar16_9`
  - Add `fontFamilyDefault`, `fontSizeDefault`, `fontWeightDefault`, `textAlignDefault` values to each zone
  - Update `PlantillaFlyer` record to hold `List<ZonaSemantica>` instead of `List<CapaPlantilla>`
  - _Requirements: 1.1, 1.5_

- [x] A7. Update `FlyerController` with new canonical endpoint
  - Add `POST /api/v1/design/generate` accepting `GenerateRequestDTO`, returning `FlyerVariacionesDTO`
  - Wire to `FlyerDesignAgent` via updated public API
  - Keep existing `POST /api/v1/design/flyer` endpoint for backward compatibility (wraps single product into `FlyerContextV2`)
  - _Requirements: 11.3_

- [x] A8. Update `application.yaml` in newEmbabelDesing
  - Set `spring.ai.openai.chat.model: gpt-4o`
  - Set `embabel.models.default-llm: gpt-4o`
  - Add `design-engine.rag.similarity-threshold: 0.5`
  - Add `design-engine.rag.plantillas-threshold: 0.3`
  - Add `design-engine.generation.parallel-timeout-seconds: 25`
  - Add `design-engine.generation.max-variaciones: 3`
  - _Requirements: 1.4, 5.6_

- [x] A9. Checkpoint — newEmbabelDesing
  - Ensure all tests pass, ask the user if questions arise.

- [ ]* A10. Write property test for coordinate invariant
  - **Property: Coordinate Invariant** — for all `Layer` objects in any generated `FlyerVariacionesDTO`, `x`, `y`, `width`, `height` ∈ [0.0, 1.0]
  - Use a property-based test library (e.g., jqwik) to generate arbitrary `FlyerContextV2` inputs and assert the invariant on the output
  - **Validates: Requirements 1.7**


<!-- ═══════════════════════════════════════════════════════════════════════ -->
<!-- COMPONENT B — audreySS                                                 -->
<!-- ═══════════════════════════════════════════════════════════════════════ -->

## B. audreySS — API Gateway

- [x] B1. Apply Liquibase DB migration 26
  - Add file `src/main/resources/db/changelog/migrations/V26__flyers_generados.sql`
  - Create `design.flyers_generados` table with columns: `id UUID PK`, `empresa_id UUID NOT NULL`, `usuario_id UUID NOT NULL`, `producto_ids JSONB NOT NULL`, `tipo_uso TEXT NOT NULL`, `formato TEXT NOT NULL`, `palette JSONB NOT NULL`, `ai_flyer_dto JSONB NOT NULL`, `plantilla_id UUID`, `created_at TIMESTAMP DEFAULT now()`, `updated_at TIMESTAMP DEFAULT now()`
  - Create index: `CREATE INDEX idx_flyers_empresa_created ON design.flyers_generados (empresa_id, created_at DESC)`
  - Register in `db.changelog-master.yaml`
  - _Requirements: 6.1_

- [x] B2. Add new and updated DTOs
  - [x] B2.1 Update `FlyerRequestDTO` for multi-product + legacy compatibility
    - Add `List<ProductInfoDTO> productos` field (nullable)
    - Keep existing `ProductInfoDTO productInfo` field (nullable, legacy)
    - Add `productosNormalizados()` method: returns `productos` if non-empty, else wraps `productInfo` in `List.of(...)`, else throws `IllegalArgumentException`
    - _Requirements: 11.1, 11.2_

  - [ ]* B2.2 Write unit tests for `FlyerRequestDTO.productosNormalizados()`
    - Test: new format with `productos` list → returns list as-is
    - Test: legacy format with `productInfo` only → returns single-element list
    - Test: both null → throws `IllegalArgumentException`
    - _Requirements: 11.2_

  - [x] B2.3 Create `FlyerVariacionesResponseDTO`, `VariacionDTO`, and `FlyerGeneradoDTO` records
    - `FlyerVariacionesResponseDTO`: `List<VariacionDTO> variaciones`
    - `VariacionDTO`: `int variacionId`, `String plantillaNombre`, `List<CapaDTO> layers`, `String formato`, `String descripcion`
    - `FlyerGeneradoDTO`: `UUID id`, `List<String> productoIds`, `String tipoUso`, `String formato`, `PaletteDTO palette`, `FlyerVariacionesResponseDTO aiResult`, `UUID plantillaId`, `Instant createdAt`, `Instant updatedAt`
    - Place in `design/application/dtos/`
    - _Requirements: 7.2, 6.1_

- [x] B3. Create `FlyerGeneradoRepositoryPort` and JPA implementation
  - [x] B3.1 Create `FlyerGeneradoRepositoryPort` interface
    - Methods: `FlyerGeneradoEntity save(FlyerGeneradoEntity entity)`, `Optional<FlyerGeneradoEntity> findByIdAndEmpresaId(UUID id, UUID empresaId)`, `Page<FlyerGeneradoEntity> findByEmpresaId(UUID empresaId, Pageable pageable)`, `List<String> findProductoIds(UUID empresaId)`
    - Place in `design/domain/ports/`
    - _Requirements: 6.1, 6.7_

  - [x] B3.2 Create `FlyerGeneradoEntity` JPA entity
    - Map to `design.flyers_generados`; use `@Column(columnDefinition = "jsonb")` for JSONB fields
    - Include `@PreUpdate` to set `updatedAt`
    - Place in `design/infrastructure/persistence/entity/`
    - _Requirements: 6.1_

  - [x] B3.3 Create `FlyerGeneradoRepository` Spring Data JPA repository and adapter
    - Extend `JpaRepository<FlyerGeneradoEntity, UUID>`
    - Add `findByEmpresaId(UUID empresaId, Pageable pageable)` query method
    - Add native query for `findProductoIds` using `jsonb_array_elements_text`
    - Implement `FlyerGeneradoRepositoryPort` adapter wrapping the Spring Data repo
    - _Requirements: 6.2, 6.6_

- [x] B4. Update `DesignEnginePort` and `DesignEngineAdapter`
  - [x] B4.1 Add `generateFlyer` overload to `DesignEnginePort`
    - New method signature: `FlyerVariacionesResponseDTO generateFlyer(List<ProductInfoDTO> productos, PaletteDTO palette, String formato, String tipoUso, UUID plantillaId)`
    - Keep existing `AiFlyerDTO generateFlyer(FlyerRequestDTO request)` for backward compatibility
    - _Requirements: 11.3_

  - [x] B4.2 Implement new `generateFlyer` in `DesignEngineAdapter` with circuit breaker
    - Call `POST {engineUrl}/api/v1/design/generate` with `GenerateRequestDTO` body; deserialize to `FlyerVariacionesResponseDTO`
    - Annotate with `@CircuitBreaker(name = "designEngine", fallbackMethod = "fallbackGenerate")`
    - `fallbackGenerate` throws `ServiceUnavailableException` (maps to HTTP 503)
    - Configure Resilience4j in `application.yaml`: `failure-rate-threshold: 50`, `wait-duration-open-ms: 10000`
    - _Requirements: 11.5_

- [x] B5. Implement use cases
  - [x] B5.1 Implement `GenerarFlyerUseCase`
    - Call `designEnginePort.generateFlyer(req.productosNormalizados(), ...)` to get `FlyerVariacionesResponseDTO`
    - Build and persist `FlyerGeneradoEntity` via `FlyerGeneradoRepositoryPort.save`
    - Return `FlyerVariacionesResponseDTO`
    - Extract `empresaId` and `usuarioId` from the authenticated principal passed as parameters
    - _Requirements: 6.1, 7.3_

  - [x] B5.2 Implement `HistorialFlyerUseCase`
    - `listarHistorial(UUID empresaId, int page)`: call `findByEmpresaId` with `PageRequest.of(page, 20, Sort.by("createdAt").descending())`; map entities to `FlyerGeneradoDTO`
    - `obtenerFlyer(UUID flyerId, UUID empresaId)`: call `findByIdAndEmpresaId`; throw `AccessDeniedException` (→ HTTP 403) if not found
    - `regenerarFlyer(UUID flyerId, UUID empresaId, UUID usuarioId)`: load existing flyer, re-call design engine, update `ai_flyer_dto` and `updated_at`, save
    - _Requirements: 6.2, 6.3, 6.7_

  - [ ]* B5.3 Write unit tests for `HistorialFlyerUseCase` empresa_id isolation
    - Mock `FlyerGeneradoRepositoryPort.findByIdAndEmpresaId` to return `Optional.empty()` when `empresaId` doesn't match
    - Assert `obtenerFlyer` throws `AccessDeniedException`
    - _Requirements: 6.7_

  - [x] B5.4 Implement `AnalyticsUseCase`
    - `productosMasPromovidos(UUID empresaId, int limit)`: call `findProductoIds`, aggregate frequency, return top `limit` entries as `List<ProductoPromovidoDTO>`
    - _Requirements: 6.6_

- [x] B6. Update `DesignController` with new endpoints
  - Update `POST /api/v1/design/flyer` to use `GenerarFlyerUseCase` and return `FlyerVariacionesResponseDTO` (replaces direct `DesignEnginePort` call)
  - Add `GET /api/v1/design/flyers` → `HistorialFlyerUseCase.listarHistorial`, `@PreAuthorize("isAuthenticated()")`, accepts `@RequestParam(defaultValue="0") int page`
  - Add `GET /api/v1/design/flyers/{id}` → `HistorialFlyerUseCase.obtenerFlyer`
  - Add `POST /api/v1/design/flyers/{id}/regenerar` → `HistorialFlyerUseCase.regenerarFlyer`
  - Add `GET /api/v1/design/analytics/productos-mas-promovidos` → `AnalyticsUseCase.productosMasPromovidos`
  - _Requirements: 6.4, 6.5, 6.6_

- [ ]* B7. Write `@WebMvcTest` for `DesignController` flyer endpoints
  - Test `GET /api/v1/design/flyers/{id}` with a flyer belonging to a different `empresa_id` → assert HTTP 403
  - Test `POST /api/v1/design/flyer` with legacy `productInfo` body → assert HTTP 200 and `variaciones` array in response
  - _Requirements: 6.7, 11.1_

- [x] B8. Checkpoint — audreySS
  - Ensure all tests pass, ask the user if questions arise.


<!-- ═══════════════════════════════════════════════════════════════════════ -->
<!-- COMPONENT C — audreySW                                                 -->
<!-- ═══════════════════════════════════════════════════════════════════════ -->

## C. audreySW — Angular 20 Frontend

- [x] C1. Install `colorthief` dependency
  - Run `npm install colorthief` in `audreySW/`
  - Add type declaration if needed (`@types/colorthief` or inline `declare module 'colorthief'`)
  - _Requirements: 3.1_

- [x] C2. Update models in `design.service.ts`
  - Extend `LayerType` union type with `'PRODUCT_2' | 'PRODUCT_3' | 'TEXT_CTA'`
  - Add optional typography fields to `LayerDTO`: `fontFamily?: string`, `fontSize?: number`, `fontWeight?: string`, `textAlign?: string`
  - Add `VariacionDTO` interface: `variacionId`, `plantillaNombre`, `layers: LayerDTO[]`, `formato`, `descripcion`
  - Add `FlyerVariacionesDTO` interface: `variaciones: VariacionDTO[]`
  - Update `FlyerRequestDTO` to use `productos: ProductInfoDTO[]` (replace `productInfo` singular)
  - Add `FlyerGeneradoDTO` interface: `id`, `productoIds`, `tipoUso`, `formato`, `palette`, `aiResult: FlyerVariacionesDTO`, `plantillaId?`, `createdAt`, `updatedAt`
  - _Requirements: 8.3, 8.7, 1.3_

- [x] C3. Update `DesignService` methods
  - Update `generarFlyer(req: FlyerRequestDTO): Observable<FlyerVariacionesDTO>` (change return type from `AiFlyerDTO`)
  - Add `regenerarFlyer(flyerId: string): Observable<FlyerVariacionesDTO>` → `POST /api/v1/design/flyers/{flyerId}/regenerar`
  - Add `listarHistorial(page: number): Observable<any>` → `GET /api/v1/design/flyers?page={page}`
  - Add `obtenerFlyer(id: string): Observable<FlyerGeneradoDTO>` → `GET /api/v1/design/flyers/{id}`
  - Add `productosMasPromovidos(): Observable<any[]>` → `GET /api/v1/design/analytics/productos-mas-promovidos`
  - _Requirements: 6.4, 6.5, 6.6_

- [x] C4. Update `FlyerDesignerComponent` — multi-product and palette extraction
  - [x] C4.1 Enable multi-product selection
    - Change `readonly MAX = 3`
    - Update `productosSeleccionados` to hold up to 3 `Producto` entries
    - Update `generarFlyer()` to build `productos: ProductInfoDTO[]` array from all selected products
    - Update `seleccionarProducto()` to call `extraerPaleta` when a product with `imagenUrl` is added
    - _Requirements: 8.1, 8.2_

  - [x] C4.2 Implement `extraerPaleta(urlImagen: string): Promise<void>`
    - Import `ColorThief` from `colorthief`
    - Load image with `crossOrigin = 'anonymous'`; call `ct.getPalette(img, 5)`; map first 3 RGB arrays to hex strings; update `this.palette`
    - On any error: silently keep default palette (`#6366f1`, `#818cf8`, `#f59e0b`)
    - _Requirements: 3.1, 3.2, 3.5_

  - [ ]* C4.3 Write unit test for `extraerPaleta` fallback
    - Simulate image load failure (reject promise)
    - Assert `this.palette` retains default values after the call
    - _Requirements: 3.5_

- [x] C5. Update `FlyerDesignerComponent` — variaciones picker
  - Add `variaciones = signal<VariacionDTO[]>([])`
  - Add `variacionSeleccionada = signal<VariacionDTO | null>(null)`
  - Update `generarFlyer()` response handler: set `variaciones` from `FlyerVariacionesDTO.variaciones`; auto-select first variation; call `renderCanvas(variacion)`
  - Add `seleccionarVariacion(v: VariacionDTO)` method: update `variacionSeleccionada` signal and call `renderCanvas(v)`
  - Update `renderCanvas()` to accept `VariacionDTO` (instead of `AiFlyerDTO`)
  - Update `regenerar()` to call `design.regenerarFlyer(flyerId)` when a persisted flyer id is available, else re-call `generarFlyer()`
  - _Requirements: 7.4_

- [x] C6. Update `FlyerDesignerComponent` — undo/redo
  - Add `private history: string[] = []`, `private historyIndex = -1`, `private readonly MAX_HISTORY = 50`
  - Implement `private saveState()`: serialize canvas to JSON, trim history to `historyIndex + 1`, enforce `MAX_HISTORY` (shift oldest if at limit), push new state, increment index
  - Implement `undo()`: decrement `historyIndex` if > 0; call `canvas.loadFromJSON` with `history[historyIndex]`
  - Implement `redo()`: increment `historyIndex` if < `history.length - 1`; call `canvas.loadFromJSON`
  - Add `@HostListener('document:keydown', ['$event']) onKeyDown(e)`: Ctrl/Cmd+Z → `undo()`; Ctrl/Cmd+Y or Ctrl/Cmd+Shift+Z → `redo()`
  - Call `saveState()` after every canvas mutation (after `renderCanvas`, after `applyTypography`, after background replacement)
  - _Requirements: 9.3, 9.4_

  - [ ]* C6.1 Write unit test for undo/redo 50-state limit
    - Call `saveState()` 55 times with distinct JSON strings
    - Assert `history.length === 50` and `historyIndex === 49`
    - _Requirements: 9.3_

- [x] C7. Update `FlyerDesignerComponent` — zoom controls
  - Add `zoomLevel = signal(100)` and `private baseWidth: number`, `private baseHeight: number` (set from canvas size on init)
  - Implement `zoomIn()`: `setZoom(Math.min(300, zoomLevel() + 10))`
  - Implement `zoomOut()`: `setZoom(Math.max(25, zoomLevel() - 10))`
  - Implement `resetZoom()`: `setZoom(100)`
  - Implement `private setZoom(level: number)`: update signal, call `canvas.setZoom(level / 100)`, resize canvas dimensions
  - _Requirements: 9.5, 9.7_

- [x] C8. Update `FlyerDesignerComponent` — typography panel
  - Add `selectedTextLayer = signal<fabric.Textbox | null>(null)`
  - In `renderCanvas()` after canvas init: register `canvas.on('selection:created', ...)` to set `selectedTextLayer` when a `Textbox` is selected; register `canvas.on('selection:cleared', ...)` to clear it
  - Implement `applyTypography(prop: 'fontFamily'|'fontSize'|'fontWeight'|'fill'|'textAlign', value: any)`: call `obj.set(prop, value)`, `canvas.renderAll()`, `saveState()`
  - Update `buildTextObject()` to use `layer.fontFamily ?? 'Inter, sans-serif'`, `layer.fontSize ?? calcFontSize(...)`, `layer.fontWeight ?? '700'`, `layer.textAlign ?? 'center'` from LLM response
  - _Requirements: 9.1, 9.2_

- [x] C9. Update `FlyerDesignerComponent` — background replacement
  - Add file input (`<input type="file" accept="image/*">`) and URL text input bound to a `bgReplaceUrl` signal
  - Implement `reemplazarFondo(source: File | string)`: load image via `FabricImage.fromURL` (or `FileReader` for local file), find existing BACKGROUND layer object on canvas, replace it, call `saveState()`
  - _Requirements: 9.6_

- [x] C10. Update `FlyerDesignerComponent` — multi-format download
  - Add `formatoDescarga = signal<'png'|'jpeg'>('png')`
  - Add `resolucionDescarga = signal<1|2|3>(2)`
  - Update `descargarFlyer()`:
    - Guard: return early if `generando()` is true
    - Show CORS warning toast if `canvasTainted()`
    - Call `canvas.toDataURL({ format: formatoDescarga(), multiplier: resolucionDescarga(), quality: formatoDescarga() === 'jpeg' ? 0.92 : undefined })`
    - Download with filename `flyer-{Date.now()}.{formato}`
  - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5, 10.6_

  - [ ]* C10.1 Write unit test for `descargarFlyer` disabled while generating
    - Set `generando.set(true)`
    - Call `descargarFlyer()`
    - Assert `canvas.toDataURL` was not called
    - _Requirements: 10.6_

- [x] C11. Update `flyer-designer.component.html`
  - Add VariacionesPicker section: render 3 thumbnail `<canvas>` elements (one per variation), highlight selected, bind click to `seleccionarVariacion(v)`
  - Add typography panel: visible when `selectedTextLayer()` is non-null; include fontFamily `<select>` (min 5 options), fontSize `<input type="number">`, fontWeight `<select>`, color `<input type="color">`, textAlign `<select>`; bind each to `applyTypography(...)`
  - Add zoom controls: "+" button → `zoomIn()`, "−" button → `zoomOut()`, "Reset" button → `resetZoom()`, display `{{ zoomLevel() }}%`
  - Add format/resolution download selectors: PNG/JPG radio or select bound to `formatoDescarga`, 1x/2x/3x radio bound to `resolucionDescarga`
  - Add background replace controls: file input and URL input, bind to `reemplazarFondo()`
  - Update product picker to allow up to 3 selections (show add button while `puedeAgregarMas()`)
  - _Requirements: 7.4, 9.1, 9.5, 9.6, 9.7, 10.1, 10.2, 8.1_

- [x] C12. Final checkpoint — audreySW
  - Ensure all tests pass, ask the user if questions arise.

---

## Notes

- Tasks marked with `*` are optional and can be skipped for a faster MVP
- Each task references specific requirements for traceability
- Checkpoints (A9, B8, C12) ensure incremental validation between components
- Implement components in order: A (Design Engine) → B (API Gateway) → C (Frontend), since each layer depends on the contracts defined by the layer below it
- The property test (A10) validates the coordinate invariant defined in the design document
