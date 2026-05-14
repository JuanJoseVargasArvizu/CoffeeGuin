package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.repository.CategoriaRepository;
import com.diep.coffeeguin_backend.model.Categoria;
import com.diep.coffeeguin_backend.model.Menu;
import com.diep.coffeeguin_backend.model.Producto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

	private final CategoriaRepository categoriaRepository;
	private final ProductoService productoService;

	public MenuService(CategoriaRepository categoriaRepository, ProductoService productoService) {
		this.categoriaRepository = categoriaRepository;
		this.productoService = productoService;
	}

	public Menu consultarMenu() {
		return new Menu(consultarCategorias());
	}

	public List<Producto> consultarPorCategoria(Categoria c) {
		return productoService.listarDisponiblesPorCategoria(c);
	}

	public Producto buscarProductoEnMenu(int id) {
		Producto producto = productoService.buscarPorId(id);
		if (producto != null && !(producto instanceof com.diep.coffeeguin_backend.model.Ingrediente)) {
			if (!tieneIngredientesDisponibles(producto)) {
				return null;
			}
		}
		return producto;
	}

	private boolean tieneIngredientesDisponibles(Producto producto) {
		if (producto.getIngredientes() == null || producto.getIngredientes().isEmpty()) {
			return false;
		}

		for (com.diep.coffeeguin_backend.model.Ingrediente ingrediente : producto.getIngredientes()) {
			if (ingrediente.getStockActual() <= 0) {
				return false;
			}
		}

		return true;
	}

	public List<Categoria> consultarCategorias() {
		return categoriaRepository.findAll();
	}
}
