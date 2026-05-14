# Guía Rápida: Próximos Pasos (Fase 4-5)

## Para Fase 4: Compatibilizar Venta con JPA

### Estado Actual de Venta
- `VentaDAOImpl` usa JDBC manual + `PreparedStatement`
- Tabla `venta` y `venta_producto` existen
- Relación N:M con `Producto` vía tabla intermedia

### Tareas

**4.1 Revisar `VentaDAOImpl`**
```bash
# Ver qué métodos usa:
grep -n "class VentaDAOImpl" src/main/java/com/diep/coffeeguin_backend/dao/VentaDAOImpl.java
```

**4.2 Opciones de Implementación**

**Opción A: Mantener JDBC (Mínimo compatible)**
- Dejar VentaDAOImpl tal cual
- Validar que `venta_producto.producto_id` apunta a mismos IDs que `ProductoRepository`
- Asegurar que cuando inserta venta, usa IDs correctos

**Opción B: Migrar a JPA (Recomendado)**
```java
@Entity
@Table(name = "venta")
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    
    @ManyToOne
    @JoinColumn(name = "mesa_id")
    private Mesa mesa;
    
    @ManyToMany
    @JoinTable(name = "venta_producto",
        joinColumns = @JoinColumn(name = "venta_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id"))
    private List<Producto> productos = new ArrayList<>();
}
```

### Validación
- Los IDs de productos en `venta_producto` deben ser iguales obtenidos vía `ProductoRepository`
- Las fechas y tipos de datos deben coincidir

## Para Fase 5: Migrar Resto de DAOs

### Patrón General para Migrar DAO JDBC → JPA

**Paso 1: Crear Entidad con anotaciones**
```java
@Entity
@Table(name = "cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String nombre;
    
    // Reemplazar @Temporal con java.time:
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    // Relaciones
    @ManyToOne
    @JoinColumn(name = "estrategia_descuento_id")
    private EstrategiaDescuento estrategia;
}
```

**Paso 2: Crear Repository**
```java
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findByActivo(boolean activo);
    List<Cliente> findByNombreContainsIgnoreCase(String nombre);
}
```

**Paso 3: Crear Service (opcional pero recomendado)**
```java
@Service
public class ClienteService {
    public Cliente registrar(Cliente cliente) { ... }
    public void actualizar(Cliente cliente) { ... }
    public List<Cliente> listarActivos() { ... }
}
```

**Paso 4: Eliminar DAO**
- Remover `ClienteDAO.java`
- Remover `ClienteDAOImpl.java`
- Actualizar inyecciones en controladores (use service)

### Orden Recomendado
1. **Cliente** (más simples, sin dependencies complejas)
2. **EstrategiaDescuento**
3. **Asiento**
4. **Mesa** (ya existe como entidad JPA, revisar)
5. **Venta** (más complejo, después de Cliente)
6. **Reporte** (depende de Venta)

### Validación para Cada DAO Migrado

```bash
# 1. Compilar
./mvnw clean compile

# 2. Ejecutar tests
./mvnw test

# 3. Iniciar app y verificar esquema
./mvnw spring-boot:run
# Revisar logs: Hibernate debe crear/validar tablas correctamente
```

## Archivos Clave para Referencia

- [PLAN_LIMPIEZA_JPA_PRODUCTO_INGREDIENTE.md](../PLAN_LIMPIEZA_JPA_PRODUCTO_INGREDIENTE.md) - Plan original
- [ESQUEMA_REFERENCIA.md](../ESQUEMA_REFERENCIA.md) - Referencia de tablas y relaciones
- [MIGRACION_JPA_REPORTE.md](../MIGRACION_JPA_REPORTE.md) - Reporte de Fase 0-2

## Checklist Final

- [ ] Fase 4 completada (Venta compatible con JPA)
- [ ] Fase 5 completada (todos los DAOs migrados a JPA)
- [ ] Todos los tests pasan
- [ ] Aplicación arranca sin errores
- [ ] Verificar endpoints del checklist (sección 6 del plan)
- [ ] Remover todos los DAOs JDBC manual
- [ ] Remover DDL manual de constructores
- [ ] Cambiar `ddl-auto=create` a `validate` (producción)
- [ ] Agregar Flyway o Liquibase para migraciones versionadas
- [ ] Actualizar documentación

## Contacto / Preguntas

Si hay dudas sobre la implementación:
1. Revisar tests de ProductoJPAIntegrationTest como ejemplo
2. Revisar ProductoService, IngredienteService, RecetaService como templates
3. Revisar ProductoRepository para examples de @Query y FETCH JOIN

---

**Estado**: Fase 0-3 completada, Fase 4-5 pendiente  
**Última actualización**: 13 mayo 2026
