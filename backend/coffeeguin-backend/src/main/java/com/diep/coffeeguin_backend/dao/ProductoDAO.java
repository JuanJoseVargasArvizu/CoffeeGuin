package com.diep.coffeeguin_backend.dao;

import com.diep.coffeeguin_backend.model.Categoria;
import com.diep.coffeeguin_backend.model.Producto;

import java.util.List;

public interface ProductoDAO {

	void agregar(Producto p);

	void eliminar(Producto p);

	Producto buscarPorId(int id);
	List<Producto> listarTodos();
	List<Producto> listarPorCategoria(Categoria categoria);
	List<Producto> listarTodosDisponibles();
	List<Producto> listarPorCategoriaDisponibles(Categoria categoria);

	void actualizar(Producto p);
}