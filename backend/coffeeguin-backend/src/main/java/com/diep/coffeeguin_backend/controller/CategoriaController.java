package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.model.Categoria;
import com.diep.coffeeguin_backend.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

	private final CategoriaService categoriaService;

	public CategoriaController(CategoriaService categoriaService) {
		this.categoriaService = categoriaService;
	}

	@GetMapping
	public ResponseEntity<List<Categoria>> listarCategorias() {
		return ResponseEntity.ok(categoriaService.consultarTodos());
	}

	@PostMapping
	public ResponseEntity<Categoria> nuevaCategoria(@RequestBody Categoria categoria) {
		try {
			categoriaService.registrarNuevaCategoria(categoria);
			return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping
	public ResponseEntity<Categoria> eliminarCategoria(@RequestBody(required = false) Categoria categoria) {
		try {
			if (categoria == null || categoria.getId() == null) {
				return ResponseEntity.badRequest().build();
			}
			categoriaService.eliminarCategoria(categoria);
			return ResponseEntity.ok(categoria);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping
	public ResponseEntity<Categoria> actualizarCategoria(@RequestBody Categoria categoria) {
		try {
			categoriaService.actualizarCategoria(categoria);
			return ResponseEntity.ok(categoria);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().build();
		} catch (NoSuchElementException exception) {
			return ResponseEntity.notFound().build();
		}
	}
}