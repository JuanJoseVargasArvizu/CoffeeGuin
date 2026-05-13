package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.dao.CategoriaDAO;
import com.diep.coffeeguin_backend.dao.ProductoDAO;
import com.diep.coffeeguin_backend.model.Producto;
import com.diep.coffeeguin_backend.model.Ingrediente;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.ArrayList;

@Service
public class ProductoService {

	private final ProductoDAO productoDAO;
	private final CategoriaDAO categoriaDAO;

	public ProductoService(ProductoDAO productoDAO, CategoriaDAO categoriaDAO) {
		this.productoDAO = productoDAO;
		this.categoriaDAO = categoriaDAO;
		Objects.requireNonNull(this.categoriaDAO, "categoriaDAO");
	}

	public void registrarNuevoProducto(Producto p) {
		productoDAO.agregar(p);
	}

	public void eliminarProducto(Producto p) {
		productoDAO.eliminar(p);
	}

	public void eliminarProductoPorId(int id) {
		Producto producto = productoDAO.buscarPorId(id);
		if (producto == null) {
			throw new NoSuchElementException("No existe un producto con id " + id);
		}
		productoDAO.eliminar(producto);
	}

	public void actualizarProducto(Producto p) {
		productoDAO.actualizar(p);
	}

	public List<Producto> consultarTodos() {
		return productoDAO.listarTodos();
	}

	public List<Ingrediente> listarIngredientes() {
		List<Ingrediente> ingredientes = new ArrayList<>();
		for (Producto p : productoDAO.listarTodos()) {
			if (p instanceof Ingrediente ingrediente) {
				ingredientes.add(ingrediente);
			}
		}
		return ingredientes;
	}
}