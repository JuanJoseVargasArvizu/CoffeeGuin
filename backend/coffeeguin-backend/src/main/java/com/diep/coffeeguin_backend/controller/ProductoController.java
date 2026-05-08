package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.model.Producto;
import com.diep.coffeeguin_backend.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/productos")
public class ProductoController {

	private final ProductoService productoService;

	public ProductoController(ProductoService productoService) {
		this.productoService = productoService;
	}

	@GetMapping
	public ResponseEntity<List<Producto>> listarProductos() {
		List<Producto> productos = productoService.consultarTodos();
		return ResponseEntity.ok(productos);
	}
	@PostMapping
	public ResponseEntity<Producto> nuevoProducto(@RequestBody Producto producto) {
		try {
			productoService.registrarNuevoProducto(producto);
			return ResponseEntity.status(HttpStatus.CREATED).body(producto);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping
	public ResponseEntity<Producto> eliminarProducto(@RequestBody(required = false) Producto producto) {
		try {
			if (producto == null || producto.getId() == null) {
				return ResponseEntity.badRequest().build();
			}
			productoService.eliminarProducto(producto);
			return ResponseEntity.ok(producto);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarProductoPorId(@PathVariable int id) {
		try {
			productoService.eliminarProductoPorId(id);
			return ResponseEntity.noContent().build();
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping
	public ResponseEntity<Producto> actualizar(@RequestBody Producto producto) {
		try {
			productoService.actualizarProducto(producto);
			return ResponseEntity.ok(producto);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}
}