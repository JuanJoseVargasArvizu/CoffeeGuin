# Plan de limpieza: Spring Data JPA, separación producto/ingrediente y verificación de endpoints

Este documento es la **hoja de ruta ejecutable** para la refactorización acordada. Está escrito para que un agente de implementación (o un desarrollador) lo siga **en orden**, marque criterios de aceptación y no deje pasos críticos al azar.

**Alcance declarado por el product owner**

- Quedarse con **Spring JPA** (en la práctica: **Spring Data JPA** + entidades Hibernate donde aplique) y **dejar de mantener DAO JDBC manual** para el dominio que hoy duplica persistencia.
- **Separar claramente** el modelo físico/lógico de **productos vendibles** vs **ingredientes (inventario / receta)**.
- **Verificar** que el comportamiento de **todos los endpoints** existentes se mantiene (o documentar cambios intencionales).
- **No** incluye en este documento fragmentos de código de producción; solo decisiones, orden y verificación.

---

## 1. Inventario del estado actual (referencia)

### 1.1 Dependencias y configuración

- `pom.xml`: `spring-boot-starter-data-jpa` presente.
- `application.properties`: `spring.jpa.hibernate.ddl-auto=update`, PostgreSQL, `show-sql=true`.
- **No** existen interfaces `JpaRepository` / `CrudRepository` en el árbol actual.

### 1.2 Patrones de persistencia coexistiendo

| Área | Estilo | Archivos representativos |
|------|--------|---------------------------|
| Producto, categoría, cliente, estrategia, venta | JDBC vía `DBConnection`, SQL manual, en varios casos `CREATE TABLE IF NOT EXISTS` en constructores | `*DAOImpl` con `java.sql.*` |
| Mesa, asiento, reporte | JPA manual con `EntityManager` | `MesaDAOImpl`, `AsientoDAOImpl`, `ReporteDAOImpl` |
| Modelo | `@Entity` en varias clases | `Producto`, `Ingrediente`, `Categoria`, `Cliente`, etc. |

Riesgo: **dos dueños del esquema** (Hibernate `ddl-auto` + DDL en DAOs) y **mapeo duplicado** (anotaciones + `ResultSet`).

### 1.3 Producto e ingrediente (punto dolor)

- `Producto` es `@Entity` con herencia `JOINED`.
- `Ingrediente` extiende `Producto` y tiene `@Table(name = "ingrediente")`, con `stockActual` y `umbralAlerta`.
- `Bebida` y `Alimento` **no** están anotados como `@Entity` (solo extienden `Producto`): Hibernate **no** puede persistirlos como subtipos estándar hasta alinear el modelo.
- `Producto.ingredientes` está marcado `@Transient`: la relación receta la implementa **solo** el JDBC en `ProductoDAOImpl` (`producto_receta`).
- Existe entidad JPA `ProductoReceta` / `ProductoRecetaId`, pero el flujo principal de negocio sigue el DAO JDBC.
- En JDBC, la tabla `producto` incluye `stock_actual` y `umbral_alerta` en la misma fila que el resto; la tabla `ingrediente` guarda `(id, stock_actual, umbral_alerta)` enlazada al id del producto. Eso es **redundancia** y fuente de inconsistencia si un día solo se actualiza una vía.

**Objetivo de “separar tablas” en este plan:** una sola fuente de verdad para stock de ingredientes (tabla `ingrediente` o equivalente), sin columnas de inventario duplicadas en `producto` para filas de tipo ingrediente; y relación **bebida/alimento → ingredientes** modelada de forma que JPA la gestione (p. ej. `ProductoReceta` o `@ManyToMany` con entidad intermedia), eliminando la sincronización manual en JDBC.

---

## 2. Objetivos finales (definición de “hecho”)

1. **Capa de acceso**: Para dominios migrados, las operaciones CRUD y consultas habituales pasan por **repositorios Spring Data JPA** (y servicios transaccionales), no por `ProductoDAOImpl` / SQL manual en constructores.
2. **Esquema**: En **pruebas**, Hibernate genera o actualiza el esquema (`ddl-auto`); sin control fino de migraciones en BD. En **producción** futura: `validate`/`none` y, si aplica, migraciones versionadas (Flyway/Liquibase).
3. **Producto vs ingrediente**: Modelo relacional y de entidades alineados: sin duplicar stock en `producto` para ingredientes; receta gestionada por JPA.
4. **API**: Mismas rutas y semántica de respuesta que hoy, salvo lista acotada de **breaking changes** aceptados y documentados en la sección 8.
5. **Pruebas**: Checklist de endpoints (sección 7) ejecutada; tests automatizados mínimos añadidos donde falten (hoy solo existe `CoffeeguinBackendApplicationTests`).

---

## 3. Decisiones de diseño (resolver antes de codificar)

Marcar una opción y ceñirse a ella en todo el plan.

### 3.1 Herencia de productos

- **Opción A (recomendada para menor cambio de API JSON):** Mantener `Producto` polimórfico con `@JsonTypeInfo` / subtipos `Ingrediente`, `Bebida`, `Alimento`, y mapeo JPA **`JOINED`** completo: añadir `@Entity` + `@Table` a `Bebida` y `Alimento` con clave foránea al padre. (SELECCIONADA)

**Recomendación del plan:** Opción A salvo que el front acuerde romper contratos.

### 3.2 Receta (producto compuesto → ingredientes)

- **Opción A1:** Mantener tabla `producto_receta` y entidad `ProductoReceta` como **dueña** de la relación; repositorio y servicio actualizan receta en el mismo agregado transaccional.
- **Opción A2:** `@ManyToMany` con `@JoinTable` equivalente a `producto_receta` (menos entidades explícitas).

**Recomendación:** A1 si ya existe lógica mental en torno a `ProductoReceta`; coherente con FKs actuales.

### 3.3 Listados “disponibles” (menú)

Hoy el SQL filtra por `tipo` y `stock_actual` en `producto`. Tras separar stock solo a `ingrediente`, las consultas “disponibles” deben usar **join/subconsulta** sobre `ingrediente.stock_actual` o proyección en repositorio (`@Query`).

Documentar la regla de negocio exacta (ej.: “ingrediente visible en menú si stock > 0”; “bebida/alimento si todos los ingredientes de la receta tienen stock > 0”) y replicarla en JPQL.

### 3.4 Alcance de “dejar de usar DAO manual”

- **Mínimo obligatorio para este epic:** `ProductoDAO` (+ impl), receta, y coherencia con `Venta`/`Menu`/`ProductoController` que dependen de productos.
- **Ampliación opcional (fases posteriores):** `CategoriaDAO`, `ClienteDAO`, `EstrategiaDescuentoDAO`, `VentaDAO`, DAOs con `EntityManager` → repositorios. Incluir en roadmap pero no bloquear el cierre del epic producto/ingrediente.

---

## 4. Fases de implementación (orden estricto)

### Fase 0 — Preparación (sin cambiar comportamiento visible)

| Paso | Acción | Criterio de hecho |
|------|--------|-------------------|
| 0.1 | Congelar rama base y listar commits de referencia | Rama clara en Git |
| 0.2 | Exportar esquema actual de PostgreSQL (`pg_dump --schema-only`) o documentar tablas y FKs | Archivo o wiki adjunto al PR |
| 0.3 | Inventariar volúmenes de datos (conteos por tabla) | Números anotados para validar migración |
| 0.4 | Añadir Flyway (o Liquibase) con **script inicial vacío** o baseline que refleje estado actual | App arranca igual |
| 0.5 | Definir convención de paquetes: `...repository`, servicios sin prefijo DAO | Documentado en este MD o README interno |

**Estado Fase 0 (implementado en código):**

- **Estrategia de BD (pruebas):** sin Flyway; esquema generado por **Hibernate** (`spring.jpa.hibernate.ddl-auto=create` en `application.properties`). Cada arranque recrea tablas desde entidades (se pierden datos). Para conservar datos entre reinicios, cambiar a `update`.
- **0.2 (parcial):** `docs/ESQUEMA_REFERENCIA.md` (mapa deducido del código). Opcional: `pg_dump` según `docs/FASE0_NOTAS.md`.
- **0.3 (plantilla):** `docs/scripts/conteos_tablas.sql` solo si en algún momento necesitas auditoría de datos.
- **0.5:** Paquete `com.diep.coffeeguin_backend.repository` con `package-info.java` (convención Javadoc).
- **0.1:** Referencia Git en `docs/FASE0_NOTAS.md`.
- **0.4:** *No aplica* (equipo opta por regeneración con Hibernate en lugar de migraciones versionadas en este entorno).

### Fase 1 — Migración de esquema: producto / ingrediente / receta

**Entorno actual (solo Hibernate, BD desechable):** no hace falta script Flyway de copia de datos. Basta con **alinear entidades** y reiniciar con `ddl-auto=create` (o vaciar la BD y usar `update`). Los pasos SQL explícitos 1.1–1.2 aplican si en el futuro hubiera que conservar datos en producción.

| Paso | Acción | Criterio de hecho |
|------|--------|-------------------|
| 1.1 | Escribir migración SQL idempotente o versionada: eliminar de `producto` las columnas `stock_actual` y `umbral_alerta` **solo después** de copiar valores a `ingrediente` para filas `tipo = 'ingrediente'` | Datos no perdidos; `ingrediente` completo |
| 1.2 | Asegurar PK/FK: `ingrediente.id` = `producto.id` (JOINED) o ajustar a diseño elegido | FKs coherentes con `producto_receta` |
| 1.3 | Alinear entidades: quitar columnas de stock de la entidad raíz `Producto` si ya no existen en BD; mantenerlas solo en `Ingrediente` | Metamodelo = BD |
| 1.4 | Añadir `@Entity` a `Bebida` y `Alimento` con `@PrimaryKeyJoinColumn` según estrategia JOINED | Hibernate crea/valida tablas `bebida`/`alimento` según migración |
| 1.5 | Sustituir `@Transient` en `Producto.ingredientes` por mapeo JPA real (p. ej. `@OneToMany(mappedBy = "producto")` en `ProductoReceta` + métodos de conveniencia en servicio) o `@ManyToMany` acordado | Carga de receta vía JPA en prueba manual |

**Nota:** En entorno de pruebas el esquema lo genera Hibernate (`ddl-auto=create` o `update`); no hay Flyway. Cuando se pase a producción, conviene `validate`/`none` más herramienta de migración si hace falta.

### Fase 2 — Repositorios Spring Data JPA (producto y receta)

| Paso | Acción | Criterio de hecho |
|------|--------|-------------------|
| 2.1 | Crear `ProductoRepository` (y otros necesarios: `ProductoRecetaRepository`, `Ingrediente` puede compartir repositorio con padre según diseño Spring Data) | Interfaces compilando |
| 2.2 | Implementar consultas equivalentes a `listarTodos`, `listarPorCategoria`, `listarTodosDisponibles`, `listarPorCategoriaDisponibles` con `@Query` o nombres derivados | Paridad funcional en tests o Postman |
| 2.3 | Mover lógica de “sincronizar ingredientes de receta” de JDBC a servicio `@Transactional` usando repositorios | Sin SQL crudo en servicio salvo `@Query` justificado |
| 2.4 | `ProductoService` / `MenuService` dejan de depender de `ProductoDAO` | Inyección solo de repositorios/servicios JPA |

### Fase 3 — Retirada de `ProductoDAOImpl` y DDL embebido

| Paso | Acción | Criterio de hecho |
|------|--------|-------------------|
| 3.1 | Eliminar `crearTablasSiNoExisten` y cualquier `CREATE TABLE` de productos/receta del DAO | DDL solo en Flyway |
| 3.2 | Borrar o deprecar `ProductoDAO` / `ProductoDAOImpl` | Sin referencias en `ProductoService`, `MenuService`, `VentaController` |
| 3.3 | Ajustar `VentaController` para obtener/actualizar productos vía servicio o repositorio JPA | Misma semántica de actualización de stock en venta |

### Fase 4 — Coherencia con `Venta` y relación venta–producto

| Paso | Acción | Criterio de hecho |
|------|--------|-------------------|
| 4.1 | Revisar `Venta` (`@ManyToMany` / `venta_producto`) vs `VentaDAOImpl` JDBC | Una sola vía de persistencia para ventas **o** plan explícito si venta sigue JDBC en esta iteración |
| 4.2 | Si venta sigue JDBC en esta iteración: garantizar que IDs y FKs siguen siendo los mismos que expone JPA para `Producto` | Venta POST no rompe |
| 4.3 | Si se migra `Venta` a JPA en esta iteración: repositorio `VentaRepository`, eliminar duplicación con entidad | Incluido en “hecho” del epic si está en alcance |

**Recomendación:** Si el tiempo es limitado, Fase 4.1 mínima: **compatibilizar** JDBC de venta con entidades JPA de producto (mismos IDs); migración completa de `VentaDAO` como **fase 5 opcional**.

### Fase 5 (opcional) — Resto de DAOs

Repetir el patrón: Flyway + repositorio + servicio; eliminar `CREATE TABLE` de constructores; sustituir `EntityManager` DAOs por repositorios donde aporte valor.

---

## 5. Verificación técnica durante el desarrollo

- Ejecutar `./mvnw test` (o `mvn test`) tras cada fase.
- Arranque local contra PostgreSQL de prueba con datos semilla mínimos: 1 categoría, 2 ingredientes, 1 bebida con receta, 1 venta de prueba.
- Revisar logs Hibernate: sin `SQLGrammarException` ni alteraciones inesperadas cuando `ddl-auto` sea `validate`.

---

## 6. Checklist de endpoints (comportamiento a preservar)

Ejecutar manualmente (Postman/Insomnia/cURL) o automatizar donde sea rentable. Anotar status: OK / diff aceptado / bug.

**Prefijos:** respetar el context path si existe (`server.servlet.context-path`); hoy los controladores mezclan `/api/...` y rutas sin prefijo.

### 6.1 Ventas

| Método y ruta | Comportamiento esperado |
|---------------|-------------------------|
| `GET /api/ventas` | Lista ventas |
| `POST /api/ventas` | Crea venta; actualiza mesa/productos según lógica actual |

### 6.2 Reportes

| Método y ruta | Comportamiento esperado |
|---------------|-------------------------|
| `GET /api/reportes` | Lista reportes |
| `POST /api/reportes/generar` | Genera reporte (usa ventas) |

### 6.3 Mesas y asientos

| Método y ruta | Comportamiento esperado |
|---------------|-------------------------|
| `GET/POST /api/mesas` | CRUD básico mesas |
| `PUT /api/mesas/{id}/estado` | Actualiza estado |
| `GET/POST /api/asientos`, `GET/PUT .../asientos/{id}...` | CRUD asientos |

### 6.4 Clientes

| Método y ruta | Comportamiento esperado |
|---------------|-------------------------|
| `GET/POST/PUT/PATCH/DELETE` bajo `/clientes` y subrutas | Sin regresión (descuentos, contacto, etc.) |

### 6.5 Estrategias de descuento

| Método y ruta | Comportamiento esperado |
|---------------|-------------------------|
| Rutas bajo `/estrategias-descuento` | Sin regresión |

### 6.6 Productos (crítico para este epic)

| Método y ruta | Comportamiento esperado |
|---------------|-------------------------|
| `GET /productos` | Lista todos; tipos JSON (`tipo`) preservados |
| `GET /productos/ingredientes` | Solo ingredientes |
| `POST /productos` | Alta polimórfica; persistencia receta/stock correcta |
| `PUT /productos` | Actualización + receta |
| `DELETE /productos`, `DELETE /productos/{id}` | Borrado coherente con FKs |

### 6.7 Menú (depende fuerte de productos/receta/stock)

| Método y ruta | Comportamiento esperado |
|---------------|-------------------------|
| `GET /menu` | Árbol categorías/productos; exclusión de ingredientes “no vendibles” como hoy |
| `GET /menu/categorias/{id}/productos` | Filtrado por categoría + disponibilidad |
| `GET /menu/productos/{id}` | Detalle; validación de ingredientes disponibles |
| `GET /menu/categorias` | Lista categorías |

### 6.8 Categorías

| Método y ruta | Comportamiento esperado |
|---------------|-------------------------|
| `GET/POST/PUT/DELETE` bajo `/categorias` | Sin regresión |

---

## 7. Riesgos y mitigaciones

| Riesgo | Mitigación |
|--------|------------|
| Pérdida de datos al mover columnas stock | Backup + migración SQL probada en copia + verificación de conteos |
| `ddl-auto=update` vs Flyway | Desactivar `update` al estabilizar; una fuente de verdad para DDL |
| `consumirStock` y observadores en `Ingrediente` | Tras migrar a JPA, invocar desde servicio de dominio o `@PreUpdate`; probar flujo venta |
| N+1 en listados | `JOIN FETCH` en `@Query` o `EntityGraph` donde el menú liste recetas |
| Contraseña en `application.properties` | No corregir en este epic salvo pedido; ideal mover a variables de entorno |

---

## 8. Breaking changes permitidos (solo si se acuerdan explícitamente)

Ejemplos que **no** son regresión si el equipo los firma:

- Cambio de forma de anidar `ingredientes` en JSON (si se pasa de lista plana a envoltorio de `ProductoReceta`).
- Cambio de códigos HTTP en errores de validación.
- Renombrar propiedades en respuestas.

Si no hay acuerdo: **mantener JSON actual** (incl. `@JsonTypeInfo` en `Producto`).

---

## 9. Orden de trabajo sugerido para el agente implementador

1. Leer este documento completo y la sección 3 (decisiones); **detenerse** si Opción A vs B no está decidida.
2. Ejecutar Fase 0.
3. Fase 1 con base de datos de prueba hasta migración verificada.
4. Fase 2 y pruebas unitarias/integración sobre repositorios (`@DataJpaTest` donde aplique).
5. Fase 3 eliminando DAO.
6. Fase 4 según alcance acordado (mínimo: compatibilidad venta).
7. Recorrer checklist sección 6 y documentar resultados en el cuerpo del PR.
8. Opcional: Fase 5 por módulos.

---

## 10. Entregables del PR (definición de listo para merge)

- [ ] Código sin `ProductoDAOImpl` (y sin interfaz si ya no se usa).
- [ ] Migración Flyway/Liquibase versionada para producto/ingrediente/receta.
- [ ] `Producto`, `Ingrediente`, `Bebida`, `Alimento` coherentes con BD y JPA.
- [ ] Repositorios Spring Data para el alcance acordado.
- [ ] Checklist sección 6 completada (adjuntar captura o tabla en descripción del PR).
- [ ] `mvn test` verde.
- [ ] Notas de cualquier endpoint con comportamiento intencionalmente distinto (sección 8).

---

*Documento generado como plan de acción; la implementación debe seguir este orden salvo dependencias descubiertas en el código, en cuyo caso actualizar este MD en el mismo PR con el motivo del cambio de orden.*
