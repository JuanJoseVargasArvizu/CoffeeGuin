package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.model.Categoria;
import com.diep.coffeeguin_backend.model.Menu;
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
	public ResponseEntity<Menu> obtenerMenu() {
		Menu menu = menuService.consultarMenu();
		return ResponseEntity.ok(menu);
	}

	@GetMapping("/categorias/{id}/productos")
	public ResponseEntity<List<Producto>> listarPorCategoria(@PathVariable int id) {
		Categoria categoria = new Categoria();
		categoria.setId((long) id);
		List<Producto> productos = menuService.consultarPorCategoria(categoria);
		return ResponseEntity.ok(productos);
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
