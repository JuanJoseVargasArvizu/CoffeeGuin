package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.repository.CategoriaRepository;
import com.diep.coffeeguin_backend.model.Categoria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CategoriaService {

	private final CategoriaRepository categoriaRepository;

	public CategoriaService(CategoriaRepository categoriaRepository) {
		this.categoriaRepository = categoriaRepository;
	}

	public List<Categoria> consultarTodos() {
		return categoriaRepository.findAll();
	}

	public List<Categoria> consultarActivos() {
		return categoriaRepository.findAll();
	}

	public void registrarNuevaCategoria(Categoria categoria) {
		categoriaRepository.save(categoria);
	}

	public void eliminarCategoria(Categoria categoria) {
		validarCategoriaConId(categoria, "eliminarse");
		Categoria existente = categoriaRepository.findById(categoria.getId()).orElse(null);
		if (existente == null) {
			throw new NoSuchElementException("No existe una categoria con id " + categoria.getId());
		}
		categoriaRepository.delete(existente);
	}

	public void actualizarCategoria(Categoria categoria) {
		validarCategoriaConId(categoria, "actualizarse");
		Categoria existente = categoriaRepository.findById(categoria.getId()).orElse(null);
		if (existente == null) {
			throw new NoSuchElementException("No existe una categoria con id " + categoria.getId());
		}
		categoriaRepository.save(categoria);
	}

	private void validarCategoriaConId(Categoria categoria, String accion) {
		if (categoria == null || categoria.getId() == null) {
			throw new IllegalArgumentException("La categoria debe tener id para " + accion);
		}
	}
}