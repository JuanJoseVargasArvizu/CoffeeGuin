# Fase 0 — notas (BD de pruebas)

## Estrategia actual

- **Sin Flyway.** El esquema lo crea **Hibernate** a partir de las `@Entity` y el valor de `spring.jpa.hibernate.ddl-auto` en `application.properties`.
- Valor por defecto en el proyecto: **`create`** → en cada arranque se **eliminan y recrean** las tablas (se pierden los datos). Útil mientras iteras en modelo y código.
- Si quieres **conservar datos** entre reinicios del backend, cambia a **`update`** en `application.properties` (sigue sin Flyway).

## Base PostgreSQL vacía o nueva

1. Crea la base (o elimina y vuelve a crear el esquema) si lo prefieres manualmente, por ejemplo:

   ```bash
   dropdb coffeeguindb && createdb coffeeguindb
   ```

2. Arranca Spring: Hibernate genera todas las tablas.

## Rama y commit de referencia (opcional)

```bash
git rev-parse --abbrev-ref HEAD
git rev-parse HEAD
```

| Campo | Valor (última verificación local) |
|-------|-----------------------------------|
| Rama | `main` |
| Commit | `16a425f35387bd649f594872058e9eece82c28d1` |

## Volcado de esquema (opcional)

Solo si necesitas documentación SQL fuera del código:

```bash
pg_dump --schema-only --no-owner --no-privileges -h localhost -U postgres -d coffeeguindb > docs/schema_pg_dump.sql
```

Hasta entonces, `ESQUEMA_REFERENCIA.md` resume tablas y relaciones deducidas del código.
