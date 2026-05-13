package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.dao.CategoriaDAO;
import com.diep.coffeeguin_backend.dao.ProductoDAO;
import com.diep.coffeeguin_backend.model.Categoria;
import com.diep.coffeeguin_backend.model.Menu;
import com.diep.coffeeguin_backend.model.Producto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

	private final CategoriaDAO categoriaDAO;
	private final ProductoDAO productoDAO;

	public MenuService(CategoriaDAO categoriaDAO, ProductoDAO productoDAO) {
		this.categoriaDAO = categoriaDAO;
		this.productoDAO = productoDAO;
	}

	public Menu consultarMenu() {
		return new Menu(consultarCategorias());
	}

	public List<Producto> consultarPorCategoria(Categoria c) {
		return productoDAO.listarPorCategoriaDisponibles(c);
	}

	public Producto buscarProductoEnMenu(int id) {
		Producto producto = productoDAO.buscarPorId(id);
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
		return categoriaDAO.listarTodos();
	}
}
