package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.dao.CategoriaDAO;
import com.diep.coffeeguin_backend.model.Categoria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CategoriaService {

	private final CategoriaDAO categoriaDAO;

	public CategoriaService(CategoriaDAO categoriaDAO) {
		this.categoriaDAO = categoriaDAO;
	}

	public List<Categoria> consultarTodos() {
		return categoriaDAO.listarTodos();
	}

	public List<Categoria> consultarActivos() {
		return categoriaDAO.listarTodos();
	}

	public void registrarNuevaCategoria(Categoria categoria) {
		categoriaDAO.agregar(categoria);
	}

	public void eliminarCategoria(Categoria categoria) {
		validarCategoriaConId(categoria, "eliminarse");
		Categoria existente = categoriaDAO.buscarPorId(categoria.getId().intValue());
		if (existente == null) {
			throw new NoSuchElementException("No existe una categoria con id " + categoria.getId());
		}
		categoriaDAO.eliminar(existente);
	}

	public void actualizarCategoria(Categoria categoria) {
		validarCategoriaConId(categoria, "actualizarse");
		Categoria existente = categoriaDAO.buscarPorId(categoria.getId().intValue());
		if (existente == null) {
			throw new NoSuchElementException("No existe una categoria con id " + categoria.getId());
		}
		categoriaDAO.actualizar(categoria);
	}

	private void validarCategoriaConId(Categoria categoria, String accion) {
		if (categoria == null || categoria.getId() == null) {
			throw new IllegalArgumentException("La categoria debe tener id para " + accion);
		}
	}
}