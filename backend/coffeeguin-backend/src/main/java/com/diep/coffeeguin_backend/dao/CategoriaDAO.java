package com.diep.coffeeguin_backend.dao;

import com.diep.coffeeguin_backend.model.Categoria;

import java.util.List;

public interface CategoriaDAO {

	void agregar(Categoria categoria);

	void eliminar(Categoria categoria);

	Categoria buscarPorId(int id);

	List<Categoria> listarTodos();

	void actualizar(Categoria categoria);
}