# Endpoints — Cliente, Menu, Producto, Categoria, EstrategiaDescuento

Documento resumen de los endpoints y esquemas JSON de respuesta/ request para que el frontend pueda integrarse sin depender de las entidades del backend.

Formato de tipos: string, integer, number (float), boolean, object, array, datetime (ISO-8601).

---

## Cliente (/clientes)

Esquema `ClienteSummary` (lista / respuestas breves):

```json
{
  "id": integer,
  "nombre": string,
  "email": string | null,
  "telefono": string | null,
  "activo": boolean
}
```

Esquema `ClienteDetail` (detalle completo):

```json
{
  "id": integer,
  "nombre": string,
  "email": string | null,
  "telefono": string | null,
  "direccion": string | null,
  "preferencias": string | null,
  "alergias": string | null,
  "bebidaFavorita": string | null,
  "platoFavorito": string | null,
  "estrategia": { "id": integer, "nombre": string } | null,
  "fechaRegistro": datetime | null,
  "fechaActualizacion": datetime | null,
  "activo": boolean
}
```

- GET `/clientes`
  - Response `200 OK`: `ClienteSummary[]` (array)

- GET `/clientes/activos`
  - Response `200 OK`: `ClienteSummary[]`

- GET `/clientes/{id}`
  - Response `200 OK`: `ClienteDetail` | `404`

- GET `/clientes/email/{email}`
  - Response `200 OK`: `ClienteDetail` | `404`

- GET `/clientes/telefono/{telefono}`
  - Response `200 OK`: `ClienteDetail` | `404`

- GET `/clientes/estrategia/{estrategiaId}`
  - Response `200 OK`: `ClienteSummary[]`

- POST `/clientes`
  - Request: `ClienteCreate` (sin `id`)

```json
{
  "nombre": string,
  "email": string | null,
  "telefono": string | null,
  "direccion": string | null
}
```

  - Response `201 Created`: `ClienteDetail` | `400`

- PUT `/clientes/{id}`
  - Request: `ClienteUpdate` (todos los campos permitidos; `id` forzado por URL)
  - Response `200 OK`: `ClienteDetail` | `400` | `404`

- PATCH `/clientes/{id}/contacto`
  - Request: `{ "email": string?, "telefono": string?, "direccion": string? }`
  - Response `200 OK`: `ClienteDetail` | `400` | `404`

- PATCH `/clientes/{id}/preferencias`
  - Request: `{ "preferencias": string?, "alergias": string?, "bebidaFavorita": string?, "platoFavorito": string? }`
  - Response `200 OK`: `ClienteDetail` | `400` | `404`

- POST `/clientes/{id}/descuentos`
  - Request: `{ "estrategiaId": integer }`
  - Response `200 OK`: `ClienteDetail` | `400` | `404`

- DELETE `/clientes/{id}/descuentos`
  - Response `200 OK`: `ClienteDetail` | `404`

- GET `/clientes/{id}/precio-descuento?total=number`
  - Response `200 OK`:

```json
{ "totalOriginal": number, "totalConDescuento": number, "montoDescuento": number }
```

- PATCH `/clientes/{id}/desactivar`
  - Response `200 OK`: `ClienteDetail` | `404`

- PATCH `/clientes/{id}/reactivar`
  - Response `200 OK`: `ClienteDetail` | `404`

- DELETE `/clientes/{id}`
  - Response `204 No Content` | `404`

---

## Menu (/menu)

Esquema `Categoria`:

```json
{ "id": integer, "nombre": string }
```

Esquema `ProductoCard` (para listas):

```json
{ "id": integer, "nombre": string, "precio": number, "descripcion": string | null, "tipo": string }
```

Esquema `CategoriaProductsAvailability`:

```json
{
  "categoria": { "id": integer, "nombre": string },
  "disponibles": ProductoCard[],
  "noDisponibles": ProductoCard[]
}
```

- GET `/menu`
  - Response `200 OK`: `CategoriaProductsAvailability[]`

- GET `/menu/categorias`
  - Response `200 OK`: `Categoria[]`

- GET `/menu/categorias/{id}/productos`
  - Response `200 OK`: `CategoriaProductsAvailability`

- GET `/menu/productos/{id}`
  - Response `200 OK`: `ProductoDetail` (ver sección Producto) | `404`

---

## Producto (/productos)

Esquema `IngredienteSimple`:

```json
{ "id": integer, "nombre": string, "stockActual": number }
```

Esquema `ProductoDetail`:

```json
{
  "id": integer,
  "nombre": string,
  "precio": number,
  "descripcion": string | null,
  "tipo": string,
  "categoria": { "id": integer, "nombre": string } | null,
  "ingredientes": IngredienteSimple[]
}
```

- GET `/productos`
  - Response `200 OK`: `ProductoDetail[]`

- GET `/productos/ingredientes`
  - Response `200 OK`: `IngredienteSimple[]`

- POST `/productos`
  - Request: `ProductoCreate` (subset of `ProductoDetail` without `id`)
  - Response `201 Created`: `ProductoDetail` | `400`

- DELETE `/productos` (body con id)
  - Request: `{ "id": integer }`
  - Response `200 OK`: `{ "id": integer, "deleted": true }` | `400` | `404`

- DELETE `/productos/{id}`
  - Response `204 No Content` | `404`

- PUT `/productos`
  - Request: `ProductoDetail` (con `id`)
  - Response `200 OK`: `ProductoDetail` | `400` | `404`

---

## Categoria (/categorias)

Esquema `Categoria` (ver arriba).

- GET `/categorias`
  - Response `200 OK`: `Categoria[]`

- POST `/categorias`
  - Request: `{ "nombre": string }`
  - Response `201 Created`: `Categoria` | `400`

- DELETE `/categorias` (body con id)
  - Request: `{ "id": integer }`
  - Response `200 OK`: `{ "id": integer, "deleted": true }` | `400` | `404`

- PUT `/categorias`
  - Request: `Categoria` (con `id`)
  - Response `200 OK`: `Categoria` | `400` | `404`

---

## EstrategiaDescuento (/estrategias-descuento)

Esquema base `EstrategiaDescuentoBase`:

```json
{ "id": integer, "nombre": string, "descripcion": string | null, "activa": boolean, "tipo": string }
```

Esquema `DescuentoPorcentaje` extends base:

```json
{ "id": integer, "nombre": string, "descripcion": string | null, "activa": boolean, "tipo": "PORCENTAJE", "porcentaje": number }
```

Esquema `DescuentoFijo` extends base:

```json
{ "id": integer, "nombre": string, "descripcion": string | null, "activa": boolean, "tipo": "FIJO", "montoFijo": number }
```

- GET `/estrategias-descuento`
  - Response `200 OK`: `EstrategiaDescuentoBase[]` (puede contener objetos con campos extra según `tipo`)

- GET `/estrategias-descuento/activas`
  - Response `200 OK`: `EstrategiaDescuentoBase[]` (filtrado)

- GET `/estrategias-descuento/porcentajes`
  - Response `200 OK`: `DescuentoPorcentaje[]`

- GET `/estrategias-descuento/fijos`
  - Response `200 OK`: `DescuentoFijo[]`

- GET `/estrategias-descuento/{id}`
  - Response `200 OK`: `EstrategiaDescuentoBase` (con campos según `tipo`) | `404`

- POST `/estrategias-descuento/porcentaje`
  - Request: `{ "nombre": string, "descripcion": string?, "porcentaje": number }`
  - Response `201 Created`: `DescuentoPorcentaje` | `400`

- POST `/estrategias-descuento/fijo`
  - Request: `{ "nombre": string, "descripcion": string?, "montoFijo": number }`
  - Response `201 Created`: `DescuentoFijo` | `400`

- PUT `/estrategias-descuento/{id}`
  - Request: `EstrategiaDescuentoBase` (con `id`)
  - Response `200 OK`: `EstrategiaDescuentoBase` | `400` | `404`

- PATCH `/estrategias-descuento/{id}/nombre`
  - Request: `{ "nombre": string }`
  - Response `200 OK`: `EstrategiaDescuentoBase` | `400` | `404`

- PATCH `/estrategias-descuento/{id}/descripcion`
  - Request: `{ "descripcion": string }`
  - Response `200 OK`: `EstrategiaDescuentoBase` | `404`

- PATCH `/estrategias-descuento/{id}/porcentaje`
  - Request: `{ "porcentaje": number }` (solo para `PORCENTAJE`)
  - Response `200 OK`: `DescuentoPorcentaje` | `400` | `404`

- PATCH `/estrategias-descuento/{id}/monto`
  - Request: `{ "montoFijo": number }` (solo para `FIJO`)
  - Response `200 OK`: `DescuentoFijo` | `400` | `404`

- PATCH `/estrategias-descuento/{id}/activar` y `/desactivar`
  - Response `200 OK`: `EstrategiaDescuentoBase` | `404`

- DELETE `/estrategias-descuento/{id}`
  - Response `204 No Content` | `404`

---

Notas finales:
- Las rutas devuelven JSON con los campos descritos; el frontend puede mapear directamente a interfaces/Typescript types usando los esquemas anteriores.
- Si quieres, genero automáticamente tipos TypeScript a partir de estos esquemas.

