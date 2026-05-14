# Referencia de esquema (deducida del código — Fase 0/1)

## Estado actual (13 mayo 2026)

### Tecnología de persistencia

- **Producto, Ingrediente, Bebida, Alimento, Categoria, ProductoReceta, Mesa, Asiento, Reporte:** 100% **Spring Data JPA + Hibernate** (entidades anotadas, repositorios activos)
- **Cliente, EstrategiaDescuento, Venta:** Aún **JDBC manual** vía DAOs (Fase 5 opcional)

### Configuración Hibernate

- `spring.jpa.hibernate.ddl-auto=create` (pruebas: recrea esquema en cada inicio desde entidades)
- Alternativa dev: cambiar a `update` para conservar datos entre reinicios
- Producción futura: `validate` o `none` + migraciones versionadas (Flyway/Liquibase)

En **pruebas**, el esquema lo genera **Hibernate** desde las entidades. Este documento no sustituye un `pg_dump --schema-only`; sirve como mapa conceptual.

**Fuentes:** 
- Entidades `@Table` (producto, ingrediente, etc.)
- DAOs JDBC con `CREATE TABLE IF NOT EXISTS` (cliente, estrategia, venta, asiento) — redundante pero tolerado

## Tablas y origen aproximado

| Tabla | Origen principal | Notas |
|-------|------------------|--------|
| `categoria` | `CategoriaDAOImpl` | `id` IDENTITY, `nombre` VARCHAR(150) |
| `estrategia_descuento` | `EstrategiaDescuentoDAOImpl` | tipos porcentaje/fijo, `activa`, etc. |
| `cliente` | `ClienteDAOImpl` | columnas listadas en el DAO; FK a estrategia puede existir solo vía Hibernate |
| `producto` | Hibernate JOINED (raíz) | Sin `stock_actual` / `umbral_alerta` en raíz; inventario solo en `ingrediente`. |
| `bebida` / `alimento` | Hibernate (subclase JOINED) | Tabla solo con FK `id` → `producto.id` |
| `ingrediente` | Hibernate (subclase JOINED) | `stock_actual`, `umbral_alerta` |
| `producto_receta` | JPA (`Producto.lineasReceta` → `ProductoReceta`, cascade) | `producto_id`, `ingrediente_id`, PK compuesta, FKs |
| `venta` | `VentaDAOImpl` | `id_venta`, `fecha`, `cliente_id`, `mesa_id`, importes |
| `venta_producto` | `VentaDAOImpl` | N:M venta–producto |
| `mesa` | Entidad JPA `Mesa` | |
| `asiento` | Entidad JPA `Asiento` | |
| `reporte` | Entidad JPA `Reporte` | |

## Relaciones relevantes (producto / menú / venta)

- `producto.categoria_id` → `categoria.id`
- `producto_receta.producto_id` → `producto.id`
- `producto_receta.ingrediente_id` → `ingrediente.id`
- `venta.cliente_id` → `cliente.id` (según DDL venta)
- `venta.mesa_id` → `mesa.id`
- `venta_producto` → `venta`, `producto`

## Inventario de datos (Fase 0.3)

Ejecutar en PostgreSQL cuando haya datos reales (copiar resultados al PR o wiki):

```sql
-- docs/scripts/conteos_tablas.sql (mismo contenido que referencia)
SELECT 'categoria' AS tabla, COUNT(*) FROM categoria
UNION ALL SELECT 'producto', COUNT(*) FROM producto
UNION ALL SELECT 'ingrediente', COUNT(*) FROM ingrediente
UNION ALL SELECT 'producto_receta', COUNT(*) FROM producto_receta
UNION ALL SELECT 'cliente', COUNT(*) FROM cliente
UNION ALL SELECT 'estrategia_descuento', COUNT(*) FROM estrategia_descuento
UNION ALL SELECT 'venta', COUNT(*) FROM venta
UNION ALL SELECT 'venta_producto', COUNT(*) FROM venta_producto
UNION ALL SELECT 'mesa', COUNT(*) FROM mesa
UNION ALL SELECT 'asiento', COUNT(*) FROM asiento
UNION ALL SELECT 'reporte', COUNT(*) FROM reporte;
```

Si alguna tabla aún no existe en un entorno concreto, omitir esa fila o crear datos semilla antes de la Fase 1.
