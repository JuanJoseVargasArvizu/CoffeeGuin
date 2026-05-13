# Documentación de Endpoints - Gestión de Clientes CoffeeGuin

## Base URL
```
http://localhost:8080
```

---

## 1. LISTAR CLIENTES

### GET /clientes
Obtiene todos los clientes registrados en el sistema.

**Request:**
```
GET /clientes
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "telefono": "5551234567",
    "direccion": "Calle Principal 123",
    "preferencias": "Sin azúcar en el café",
    "alergias": "Maní, nueces",
    "bebidaFavorita": "Café Americano",
    "platoFavorito": "Croissant",
    "estrategia": {
      "id": 1,
      "tipo": "PORCENTAJE",
      "valor": 10.0,
      "activa": true
    },
    "fechaRegistro": "2026-05-12T10:30:00",
    "fechaActualizacion": "2026-05-12T14:45:00",
    "activo": true
  },
  {
    "id": 2,
    "nombre": "María García",
    "email": "maria@example.com",
    "telefono": "5559876543",
    "direccion": "Avenida Central 456",
    "preferencias": "Leche desnatada",
    "alergias": "Lactosa",
    "bebidaFavorita": "Café con leche",
    "platoFavorito": "Panini",
    "estrategia": null,
    "fechaRegistro": "2026-05-10T09:00:00",
    "fechaActualizacion": "2026-05-10T09:00:00",
    "activo": true
  }
]
```

---

## 2. LISTAR CLIENTES ACTIVOS

### GET /clientes/activos
Obtiene solo los clientes que están activos.

**Request:**
```
GET /clientes/activos
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    ...
  }
]
```

---

## 3. OBTENER CLIENTE POR ID

### GET /clientes/{id}
Obtiene los detalles de un cliente específico.

**Request:**
```
GET /clientes/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "telefono": "5551234567",
  "direccion": "Calle Principal 123",
  "preferencias": "Sin azúcar en el café",
  "alergias": "Maní, nueces",
  "bebidaFavorita": "Café Americano",
  "platoFavorito": "Croissant",
  "estrategia": {
    "id": 1,
    "tipo": "PORCENTAJE",
    "valor": 10.0,
    "activa": true
  },
  "fechaRegistro": "2026-05-12T10:30:00",
  "fechaActualizacion": "2026-05-12T14:45:00",
  "activo": true
}
```

**Response (404 Not Found):**
```json
{
  "error": "No existe un cliente con id 999"
}
```

---

## 4. BUSCAR CLIENTE POR EMAIL

### GET /clientes/email/{email}
Busca un cliente por su dirección de email.

**Request:**
```
GET /clientes/email/juan@example.com
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  ...
}
```

**Response (404 Not Found):**
Si el email no existe en el sistema.

---

## 5. BUSCAR CLIENTE POR TELÉFONO

### GET /clientes/telefono/{telefono}
Busca un cliente por su número de teléfono.

**Request:**
```
GET /clientes/telefono/5551234567
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "telefono": "5551234567",
  ...
}
```

---

## 6. LISTAR CLIENTES POR ESTRATEGIA DE DESCUENTO

### GET /clientes/estrategia/{estrategiaId}
Obtiene todos los clientes que tienen asignada una estrategia de descuento específica.

**Request:**
```
GET /clientes/estrategia/1
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "estrategia": {
      "id": 1,
      "tipo": "PORCENTAJE",
      "valor": 10.0,
      "activa": true
    },
    ...
  },
  {
    "id": 5,
    "nombre": "Carlos López",
    "email": "carlos@example.com",
    "estrategia": {
      "id": 1,
      "tipo": "PORCENTAJE",
      "valor": 10.0,
      "activa": true
    },
    ...
  }
]
```

---

## 7. CREAR NUEVO CLIENTE

### POST /clientes
Registra un nuevo cliente habitual en el sistema.

**Request:**
```
POST /clientes
Content-Type: application/json

{
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "telefono": "5551234567",
  "direccion": "Calle Principal 123",
  "preferencias": "Sin azúcar en el café",
  "alergias": "Maní, nueces",
  "bebidaFavorita": "Café Americano",
  "platoFavorito": "Croissant"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "telefono": "5551234567",
  "direccion": "Calle Principal 123",
  "preferencias": "Sin azúcar en el café",
  "alergias": "Maní, nueces",
  "bebidaFavorita": "Café Americano",
  "platoFavorito": "Croissant",
  "estrategia": null,
  "fechaRegistro": "2026-05-12T10:30:00",
  "fechaActualizacion": "2026-05-12T10:30:00",
  "activo": true
}
```

**Response (400 Bad Request):**
- Email ya existe en el sistema
- Falta el nombre o email requerido

---

## 8. ACTUALIZAR CLIENTE COMPLETO

### PUT /clientes/{id}
Actualiza todos los datos de un cliente existente.

**Request:**
```
PUT /clientes/1
Content-Type: application/json

{
  "id": 1,
  "nombre": "Juan Pérez Actualizado",
  "email": "juan.nuevo@example.com",
  "telefono": "5551234567",
  "direccion": "Calle Nueva 456",
  "preferencias": "Sin azúcar, extra caramelizado",
  "alergias": "Maní",
  "bebidaFavorita": "Latte",
  "platoFavorito": "Tiramisú"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Juan Pérez Actualizado",
  "email": "juan.nuevo@example.com",
  ...
  "fechaActualizacion": "2026-05-12T15:00:00",
  "activo": true
}
```

**Response (404 Not Found):**
Si el cliente no existe.

---

## 9. ACTUALIZAR INFORMACIÓN DE CONTACTO

### PATCH /clientes/{id}/contacto
Actualiza solo la información de contacto (email, teléfono, dirección) de un cliente.

**Request:**
```
PATCH /clientes/1/contacto
Content-Type: application/json

{
  "email": "juan.nuevo@example.com",
  "telefono": "5559876543",
  "direccion": "Nueva dirección 789"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "email": "juan.nuevo@example.com",
  "telefono": "5559876543",
  "direccion": "Nueva dirección 789",
  ...
  "fechaActualizacion": "2026-05-12T15:30:00"
}
```

**Nota:** Los campos son opcionales. Solo se actualizan los que se envíen.

---

## 10. ACTUALIZAR PREFERENCIAS

### PATCH /clientes/{id}/preferencias
Actualiza las preferencias, alergias y bebida/plato favorito del cliente.

**Request:**
```
PATCH /clientes/1/preferencias
Content-Type: application/json

{
  "preferencias": "Sin azúcar, extra caramelizado",
  "alergias": "Maní, frutos secos",
  "bebidaFavorita": "Latte",
  "platoFavorito": "Tiramisú"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "preferencias": "Sin azúcar, extra caramelizado",
  "alergias": "Maní, frutos secos",
  "bebidaFavorita": "Latte",
  "platoFavorito": "Tiramisú",
  ...
  "fechaActualizacion": "2026-05-12T15:45:00"
}
```

---

## 11. ASIGNAR ESTRATEGIA DE DESCUENTO

### POST /clientes/{id}/descuentos
Asigna una estrategia de descuento (o programa de fidelización) a un cliente registrado.

**Request:**
```
POST /clientes/1/descuentos
Content-Type: application/json

{
  "id": 1,
  "tipo": "PORCENTAJE",
  "valor": 10.0,
  "activa": true
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "estrategia": {
    "id": 1,
    "tipo": "PORCENTAJE",
    "valor": 10.0,
    "activa": true
  },
  ...
  "fechaActualizacion": "2026-05-12T16:00:00"
}
```

**Response (400 Bad Request):**
- Estrategia inválida o sin ID

**Response (404 Not Found):**
Si el cliente no existe.

---

## 12. ELIMINAR ESTRATEGIA DE DESCUENTO

### DELETE /clientes/{id}/descuentos
Elimina (desvincula) la estrategia de descuento de un cliente.

**Request:**
```
DELETE /clientes/1/descuentos
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "estrategia": null,
  ...
  "fechaActualizacion": "2026-05-12T16:15:00"
}
```

---

## 13. CALCULAR PRECIO CON DESCUENTO

### GET /clientes/{id}/precio-descuento
Calcula el precio final considerando el descuento asignado al cliente.

**Request:**
```
GET /clientes/1/precio-descuento?total=100.00
```

**Response (200 OK):**
```json
{
  "totalOriginal": 100.00,
  "totalConDescuento": 90.00,
  "montoDescuento": 10.00
}
```

**Explicación:**
- Si el cliente tiene descuento del 10% en $100:
  - Total Original: $100.00
  - Monto Descuento: $10.00
  - Total Con Descuento: $90.00

---

## 14. DESACTIVAR CLIENTE

### PATCH /clientes/{id}/desactivar
Desactiva un cliente (soft delete). El cliente no aparecerá en listados de activos pero sus datos se conservan.

**Request:**
```
PATCH /clientes/1/desactivar
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  ...
  "activo": false,
  "fechaActualizacion": "2026-05-12T16:30:00"
}
```

---

## 15. REACTIVAR CLIENTE

### PATCH /clientes/{id}/reactivar
Reactiva un cliente que fue desactivado previamente.

**Request:**
```
PATCH /clientes/1/reactivar
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  ...
  "activo": true,
  "fechaActualizacion": "2026-05-12T16:45:00"
}
```

---

## 16. ELIMINAR CLIENTE

### DELETE /clientes/{id}
Elimina permanentemente un cliente del sistema (hard delete).

**Request:**
```
DELETE /clientes/1
```

**Response (204 No Content):**
Sin cuerpo en la respuesta.

**Response (404 Not Found):**
Si el cliente no existe.

---

## Resumen de Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/clientes` | Listar todos los clientes |
| GET | `/clientes/activos` | Listar clientes activos |
| GET | `/clientes/{id}` | Obtener cliente por ID |
| GET | `/clientes/email/{email}` | Buscar cliente por email |
| GET | `/clientes/telefono/{telefono}` | Buscar cliente por teléfono |
| GET | `/clientes/estrategia/{estrategiaId}` | Listar clientes con estrategia |
| POST | `/clientes` | Crear nuevo cliente |
| PUT | `/clientes/{id}` | Actualizar cliente completo |
| PATCH | `/clientes/{id}/contacto` | Actualizar contacto |
| PATCH | `/clientes/{id}/preferencias` | Actualizar preferencias |
| POST | `/clientes/{id}/descuentos` | Asignar descuento |
| DELETE | `/clientes/{id}/descuentos` | Eliminar descuento |
| GET | `/clientes/{id}/precio-descuento` | Calcular precio con descuento |
| PATCH | `/clientes/{id}/desactivar` | Desactivar cliente |
| PATCH | `/clientes/{id}/reactivar` | Reactivar cliente |
| DELETE | `/clientes/{id}` | Eliminar cliente |

---

## Códigos de Estado HTTP

- **200 OK** - Operación exitosa
- **201 Created** - Recurso creado exitosamente
- **204 No Content** - Operación exitosa sin contenido (DELETE)
- **400 Bad Request** - Datos inválidos o falta información requerida
- **404 Not Found** - Recurso no encontrado

---

## Información del Cliente

Cada cliente almacena:

### Información de Contacto
- `nombre` - Nombre del cliente (requerido)
- `email` - Email del cliente (requerido, único)
- `telefono` - Número de teléfono
- `direccion` - Dirección de domicilio

### Preferencias
- `preferencias` - Preferencias de bebida/comida
- `alergias` - Alergias y restricciones alimentarias
- `bebidaFavorita` - Bebida favorita del cliente
- `platoFavorito` - Plato favorito del cliente

### Fidelización
- `estrategia` - Estrategia de descuento asignada al cliente

### Auditoría
- `fechaRegistro` - Fecha de registro del cliente
- `fechaActualizacion` - Fecha de última actualización
- `activo` - Estado del cliente (true/false)

---

## Notas Importantes

1. **Email Único**: Cada cliente debe tener un email único en el sistema.
2. **Soft Delete**: Al desactivar un cliente, sus datos se conservan pero no aparece en listados de activos.
3. **Descuentos**: La estrategia de descuento puede ser PORCENTAJE o FIJO.
4. **Auditoría Automática**: Los timestamps se actualizan automáticamente en cada operación.
5. **Validaciones**: El nombre y email son obligatorios para crear/actualizar un cliente.
