package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.model.Categoria;
import com.diep.coffeeguin_backend.model.Menu;
import com.diep.coffeeguin_backend.model.CategoriaProductosDisponibilidad;
import com.diep.coffeeguin_backend.model.Ingrediente;
import com.diep.coffeeguin_backend.model.Producto;
import com.diep.coffeeguin_backend.service.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menu")
public class MenuController {

	private final MenuService menuService;

	public MenuController(MenuService menuService) {
		this.menuService = menuService;
	}

	@GetMapping
	public ResponseEntity<List<CategoriaProductosDisponibilidad>> obtenerMenu() {
		List<CategoriaProductosDisponibilidad> resultado = new java.util.ArrayList<>();
		List<com.diep.coffeeguin_backend.model.Categoria> categorias = menuService.consultarCategorias();
		for (com.diep.coffeeguin_backend.model.Categoria categoria : categorias) {
			CategoriaProductosDisponibilidad dto = new CategoriaProductosDisponibilidad(categoria);
			List<Producto> productos = menuService.consultarPorCategoria(categoria);
			for (Producto p : productos) {
				if (p instanceof Ingrediente) {
					continue;
				}
				if (esProductoDisponible(p)) {
					dto.getDisponibles().add(p);
				} else {
					dto.getNoDisponibles().add(p);
				}
			}
			resultado.add(dto);
		}
		return ResponseEntity.ok(resultado);
	}

	@GetMapping("/categorias/{id}/productos")
	public ResponseEntity<CategoriaProductosDisponibilidad> listarPorCategoria(@PathVariable int id) {
		Categoria categoria = new Categoria();
		categoria.setId((long) id);
		List<Producto> productos = menuService.consultarPorCategoria(categoria);
		CategoriaProductosDisponibilidad dto = new CategoriaProductosDisponibilidad(categoria);
		for (Producto p : productos) {
			if (p instanceof Ingrediente) {
				continue;
			}
			if (esProductoDisponible(p)) {
				dto.getDisponibles().add(p);
			} else {
				dto.getNoDisponibles().add(p);
			}
		}
		return ResponseEntity.ok(dto);
	}

	private boolean esProductoDisponible(Producto producto) {
		List<Ingrediente> ingredientes = producto.getIngredientes();
		if (ingredientes == null || ingredientes.isEmpty()) {
			return false;
		}
		for (Ingrediente ingrediente : ingredientes) {
			if (ingrediente == null || ingrediente.getStockActual() <= 0) {
				return false;
			}
		}
		return true;
	}

	@GetMapping("/productos/{id}")
	public ResponseEntity<Producto> buscarProducto(@PathVariable int id) {
		Producto producto = menuService.buscarProductoEnMenu(id);
		if (producto == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(producto);
	}

	@GetMapping("/categorias")
	public ResponseEntity<List<Categoria>> listarCategorias() {
		List<Categoria> categorias = menuService.consultarCategorias();
		return ResponseEntity.ok(categorias);
	}
}
