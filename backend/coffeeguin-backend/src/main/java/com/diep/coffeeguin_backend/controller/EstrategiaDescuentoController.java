package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.model.DescuentoFijo;
import com.diep.coffeeguin_backend.model.DescuentoPorcentaje;
import com.diep.coffeeguin_backend.model.EstrategiaDescuento;
import com.diep.coffeeguin_backend.service.EstrategiaDescuentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/estrategias-descuento")
public class EstrategiaDescuentoController {

	private final EstrategiaDescuentoService estrategiaService;

	public EstrategiaDescuentoController(EstrategiaDescuentoService estrategiaService) {
		this.estrategiaService = estrategiaService;
	}

	/**
	 * GET /estrategias-descuento - Obtiene todas las estrategias de descuento
	 * @return Lista de todas las estrategias
	 */
	@GetMapping
	public ResponseEntity<List<EstrategiaDescuento>> listarEstrategias() {
		return ResponseEntity.ok(estrategiaService.consultarTodas());
	}

	/**
	 * GET /estrategias-descuento/activas - Obtiene solo las estrategias activas
	 * @return Lista de estrategias activas
	 */
	@GetMapping("/activas")
	public ResponseEntity<List<EstrategiaDescuento>> listarActivas() {
		return ResponseEntity.ok(estrategiaService.consultarActivas());
	}

	/**
	 * GET /estrategias-descuento/porcentajes - Obtiene todas las estrategias de descuento por porcentaje
	 * @return Lista de descuentos por porcentaje
	 */
	@GetMapping("/porcentajes")
	public ResponseEntity<List<DescuentoPorcentaje>> listarPorcentajes() {
		return ResponseEntity.ok(estrategiaService.consultarPorcentajes());
	}

	/**
	 * GET /estrategias-descuento/fijos - Obtiene todas las estrategias de descuento fijo
	 * @return Lista de descuentos fijos
	 */
	@GetMapping("/fijos")
	public ResponseEntity<List<DescuentoFijo>> listarFijos() {
		return ResponseEntity.ok(estrategiaService.consultarFijos());
	}

	/**
	 * GET /estrategias-descuento/{id} - Obtiene una estrategia de descuento específica
	 * @param id ID de la estrategia
	 * @return Datos de la estrategia
	 */
	@GetMapping("/{id}")
	public ResponseEntity<EstrategiaDescuento> obtenerEstrategia(@PathVariable int id) {
		try {
			return ResponseEntity.ok(estrategiaService.consultarPorId(id));
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * POST /estrategias-descuento/porcentaje - Crea una nueva estrategia de descuento por porcentaje
	 *
	 * Body:
	 * {
	 *   "nombre": "Descuento VIP 10%",
	 *   "descripcion": "Descuento para clientes VIP",
	 *   "porcentaje": 10.0
	 * }
	 *
	 * @param request Datos de la estrategia de descuento
	 * @return Estrategia creada
	 */
	@PostMapping("/porcentaje")
	public ResponseEntity<DescuentoPorcentaje> crearDescuentoPorcentaje(@RequestBody CrearDescuentoPorcentajeRequest request) {
		try {
			estrategiaService.registrarDescuentoPorcentaje(request.getNombre(), request.getDescripcion(), request.getPorcentaje());
			// Buscar la estrategia creada (por simplicidad, aquí retornamos los datos del request)
			DescuentoPorcentaje estrategia = new DescuentoPorcentaje(request.getNombre(), request.getDescripcion(), request.getPorcentaje());
			return ResponseEntity.status(HttpStatus.CREATED).body(estrategia);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * POST /estrategias-descuento/fijo - Crea una nueva estrategia de descuento fijo
	 *
	 * Body:
	 * {
	 *   "nombre": "Descuento $5 MXN",
	 *   "descripcion": "Descuento fijo de 5 pesos",
	 *   "montoFijo": 5.0
	 * }
	 *
	 * @param request Datos de la estrategia de descuento
	 * @return Estrategia creada
	 */
	@PostMapping("/fijo")
	public ResponseEntity<DescuentoFijo> crearDescuentoFijo(@RequestBody CrearDescuentoFijoRequest request) {
		try {
			estrategiaService.registrarDescuentoFijo(request.getNombre(), request.getDescripcion(), request.getMontoFijo());
			DescuentoFijo estrategia = new DescuentoFijo(request.getNombre(), request.getDescripcion(), request.getMontoFijo());
			return ResponseEntity.status(HttpStatus.CREATED).body(estrategia);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * PUT /estrategias-descuento/{id} - Actualiza una estrategia de descuento
	 *
	 * Body:
	 * {
	 *   "id": 1,
	 *   "nombre": "Descuento VIP 15%",
	 *   "descripcion": "Descuento actualizado para VIP",
	 *   "porcentaje": 15.0,
	 *   "activa": true
	 * }
	 *
	 * @param id ID de la estrategia
	 * @param estrategia Datos actualizados
	 * @return Estrategia actualizada
	 */
	@PutMapping("/{id}")
	public ResponseEntity<EstrategiaDescuento> actualizarEstrategia(@PathVariable int id, @RequestBody EstrategiaDescuento estrategia) {
		try {
			estrategia.setId(id);
			estrategiaService.actualizarEstrategia(estrategia);
			return ResponseEntity.ok(estrategiaService.consultarPorId(id));
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * PATCH /estrategias-descuento/{id}/nombre - Actualiza solo el nombre
	 *
	 * Body:
	 * {
	 *   "nombre": "Nuevo nombre"
	 * }
	 *
	 * @param id ID de la estrategia
	 * @param updateData Nuevo nombre
	 * @return Estrategia actualizada
	 */
	@PatchMapping("/{id}/nombre")
	public ResponseEntity<EstrategiaDescuento> actualizarNombre(@PathVariable int id, @RequestBody ActualizarNombreRequest updateData) {
		try {
			estrategiaService.actualizarNombre(id, updateData.getNombre());
			return ResponseEntity.ok(estrategiaService.consultarPorId(id));
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * PATCH /estrategias-descuento/{id}/descripcion - Actualiza solo la descripción
	 *
	 * Body:
	 * {
	 *   "descripcion": "Nueva descripción"
	 * }
	 *
	 * @param id ID de la estrategia
	 * @param updateData Nueva descripción
	 * @return Estrategia actualizada
	 */
	@PatchMapping("/{id}/descripcion")
	public ResponseEntity<EstrategiaDescuento> actualizarDescripcion(@PathVariable int id, @RequestBody ActualizarDescripcionRequest updateData) {
		try {
			estrategiaService.actualizarDescripcion(id, updateData.getDescripcion());
			return ResponseEntity.ok(estrategiaService.consultarPorId(id));
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * PATCH /estrategias-descuento/{id}/porcentaje - Actualiza el porcentaje de un descuento
	 *
	 * Body:
	 * {
	 *   "porcentaje": 15.0
	 * }
	 *
	 * @param id ID de la estrategia
	 * @param updateData Nuevo porcentaje
	 * @return Estrategia actualizada
	 */
	@PatchMapping("/{id}/porcentaje")
	public ResponseEntity<EstrategiaDescuento> actualizarPorcentaje(@PathVariable int id, @RequestBody ActualizarPorcentajeRequest updateData) {
		try {
			estrategiaService.actualizarPorcentaje(id, updateData.getPorcentaje());
			return ResponseEntity.ok(estrategiaService.consultarPorId(id));
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * PATCH /estrategias-descuento/{id}/monto - Actualiza el monto fijo de un descuento
	 *
	 * Body:
	 * {
	 *   "montoFijo": 10.0
	 * }
	 *
	 * @param id ID de la estrategia
	 * @param updateData Nuevo monto
	 * @return Estrategia actualizada
	 */
	@PatchMapping("/{id}/monto")
	public ResponseEntity<EstrategiaDescuento> actualizarMontoFijo(@PathVariable int id, @RequestBody ActualizarMontoFijoRequest updateData) {
		try {
			estrategiaService.actualizarMontoFijo(id, updateData.getMontoFijo());
			return ResponseEntity.ok(estrategiaService.consultarPorId(id));
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * PATCH /estrategias-descuento/{id}/activar - Activa una estrategia
	 *
	 * @param id ID de la estrategia
	 * @return Estrategia activada
	 */
	@PatchMapping("/{id}/activar")
	public ResponseEntity<EstrategiaDescuento> activarEstrategia(@PathVariable int id) {
		try {
			estrategiaService.activarEstrategia(id);
			return ResponseEntity.ok(estrategiaService.consultarPorId(id));
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * PATCH /estrategias-descuento/{id}/desactivar - Desactiva una estrategia
	 *
	 * @param id ID de la estrategia
	 * @return Estrategia desactivada
	 */
	@PatchMapping("/{id}/desactivar")
	public ResponseEntity<EstrategiaDescuento> desactivarEstrategia(@PathVariable int id) {
		try {
			estrategiaService.desactivarEstrategia(id);
			return ResponseEntity.ok(estrategiaService.consultarPorId(id));
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * DELETE /estrategias-descuento/{id} - Elimina una estrategia de descuento
	 *
	 * @param id ID de la estrategia
	 * @return Sin contenido
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarEstrategia(@PathVariable int id) {
		try {
			estrategiaService.eliminarEstrategia(id);
			return ResponseEntity.noContent().build();
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	// DTOs para requests
	public static class CrearDescuentoPorcentajeRequest {
		private String nombre;
		private String descripcion;
		private Double porcentaje;

		public String getNombre() { return nombre; }
		public void setNombre(String nombre) { this.nombre = nombre; }

		public String getDescripcion() { return descripcion; }
		public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

		public Double getPorcentaje() { return porcentaje; }
		public void setPorcentaje(Double porcentaje) { this.porcentaje = porcentaje; }
	}

	public static class CrearDescuentoFijoRequest {
		private String nombre;
		private String descripcion;
		private Double montoFijo;

		public String getNombre() { return nombre; }
		public void setNombre(String nombre) { this.nombre = nombre; }

		public String getDescripcion() { return descripcion; }
		public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

		public Double getMontoFijo() { return montoFijo; }
		public void setMontoFijo(Double montoFijo) { this.montoFijo = montoFijo; }
	}

	public static class ActualizarNombreRequest {
		private String nombre;

		public String getNombre() { return nombre; }
		public void setNombre(String nombre) { this.nombre = nombre; }
	}

	public static class ActualizarDescripcionRequest {
		private String descripcion;

		public String getDescripcion() { return descripcion; }
		public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
	}

	public static class ActualizarPorcentajeRequest {
		private Double porcentaje;

		public Double getPorcentaje() { return porcentaje; }
		public void setPorcentaje(Double porcentaje) { this.porcentaje = porcentaje; }
	}

	public static class ActualizarMontoFijoRequest {
		private Double montoFijo;

		public Double getMontoFijo() { return montoFijo; }
		public void setMontoFijo(Double montoFijo) { this.montoFijo = montoFijo; }
	}
}
