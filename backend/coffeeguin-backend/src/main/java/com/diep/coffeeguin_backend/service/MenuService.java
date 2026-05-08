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
		return productoDAO.listarPorCategoria(c);
	}

	public Producto buscarProductoEnMenu(int id) {
		return productoDAO.buscarPorId(id);
	}

	public List<Categoria> consultarCategorias() {
		return categoriaDAO.listarTodos();
	}
}
