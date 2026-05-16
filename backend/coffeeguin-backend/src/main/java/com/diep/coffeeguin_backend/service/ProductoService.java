package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.model.Categoria;
import com.diep.coffeeguin_backend.model.Ingrediente;
import com.diep.coffeeguin_backend.model.Producto;
import com.diep.coffeeguin_backend.model.ProductoReceta;
import com.diep.coffeeguin_backend.model.ProductoRecetaId;
import com.diep.coffeeguin_backend.model.IngredienteCantidad;
import com.diep.coffeeguin_backend.repository.CategoriaRepository;
import com.diep.coffeeguin_backend.repository.IngredienteRepository;
import com.diep.coffeeguin_backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class ProductoService {

	private final ProductoRepository productoRepository;
	private final IngredienteRepository ingredienteRepository;
	private final CategoriaRepository categoriaRepository;

	public ProductoService(ProductoRepository productoRepository, IngredienteRepository ingredienteRepository,
			CategoriaRepository categoriaRepository) {
		this.productoRepository = productoRepository;
		this.ingredienteRepository = ingredienteRepository;
		this.categoriaRepository = categoriaRepository;
		Objects.requireNonNull(this.categoriaRepository, "categoriaRepository");
	}

	@Transactional
	public void registrarNuevoProducto(Producto p) {
		p.setId(null);
		resolverCategoria(p);
		resolverIngredientesDeRecetaParaNuevo(p);
		productoRepository.save(p);
	}

	@Transactional
	public void eliminarProducto(Producto p) {
		if (p.getId() == null) {
			throw new IllegalArgumentException("El producto debe tener id para eliminarse");
		}
		productoRepository.deleteById(p.getId());
	}

	@Transactional
	public void eliminarProductoPorId(int id) {
		if (!productoRepository.existsById((long) id)) {
			throw new NoSuchElementException("No existe un producto con id " + id);
		}
		productoRepository.deleteById((long) id);
	}

	@Transactional
	public void actualizarProducto(Producto p) {
		if (p.getId() == null) {
			throw new IllegalArgumentException("El producto debe tener id para actualizarse");
		}
		Producto existente = productoRepository.findByIdWithReceta(p.getId())
				.orElseThrow(() -> new NoSuchElementException("No existe un producto con id " + p.getId()));

		existente.setNombre(p.getNombre());
		existente.setPrecio(p.getPrecio());
		existente.setDescripcion(p.getDescripcion());
		if (p.getTipo() != null && !p.getTipo().isBlank()) {
			existente.setTipo(p.getTipo());
		}
		resolverCategoriaEn(existente, p.getCategoria());

		if (existente instanceof Ingrediente exIng && p instanceof Ingrediente px) {
			exIng.setStockActual(px.getStockActual());
			exIng.setUmbralAlerta(px.getUmbralAlerta());
		}

		// Procesar ingredientes con cantidad
		resolverIngredientesDeRecetaConCantidad(existente, p);
		productoRepository.save(existente);
	}

	@Transactional(readOnly = true)
	public List<Producto> consultarTodos() {
		return productoRepository.findAllWithReceta();
	}

	@Transactional(readOnly = true)
	public List<Producto> listarDisponiblesPorCategoria(Categoria categoria) {
		if (categoria == null || categoria.getId() == null) {
			throw new IllegalArgumentException("La categoria debe tener id para consultar sus productos");
		}
		return productoRepository.findByCategoriaDisponiblesWithReceta(categoria.getId());
	}

	@Transactional(readOnly = true)
	public Producto buscarPorId(int id) {
		return productoRepository.findByIdWithReceta((long) id).orElse(null);
	}

	@Transactional(readOnly = true)
	public List<Ingrediente> listarIngredientes() {
		List<Ingrediente> ingredientes = new ArrayList<>();
		for (Producto p : productoRepository.findAllWithReceta()) {
			if (p instanceof Ingrediente ingrediente) {
				ingredientes.add(ingrediente);
			}
		}
		return ingredientes;
	}

	private void resolverCategoria(Producto p) {
		resolverCategoriaEn(p, p.getCategoria());
	}

	private void resolverCategoriaEn(Producto destino, Categoria categoria) {
		if (categoria == null || categoria.getId() == null) {
			destino.setCategoria(null);
			return;
		}
		destino.setCategoria(categoriaRepository.getReferenceById(categoria.getId()));
	}

	private void resolverIngredientesDeReceta(Producto p) {
		if (p.getIngredientes() == null || p.getIngredientes().isEmpty()) {
			return;
		}
		List<Ingrediente> refs = new ArrayList<>();
		for (Ingrediente ing : p.getIngredientes()) {
			if (ing == null || ing.getId() == null) {
				continue;
			}
			refs.add(ingredienteRepository.getReferenceById(ing.getId()));
		}
		p.setIngredientes(refs);
	}

	private void resolverIngredientesDeRecetaParaNuevo(Producto p) {
		if (p.getLineasReceta() == null || p.getLineasReceta().isEmpty()) {
			return;
		}
		
		// Procesar cada línea de receta
		for (ProductoReceta linea : p.getLineasReceta()) {
			if (linea == null || linea.getId() == null || linea.getId().getIngredienteId() == null) {
				continue;
			}
			
			Long ingredienteId = linea.getId().getIngredienteId();
			
			// Obtener la referencia del ingrediente
			Ingrediente ingrediente = ingredienteRepository.getReferenceById(ingredienteId);
			linea.setIngrediente(ingrediente);
			linea.setProducto(p);
		}
	}

	private void resolverIngredientesDeRecetaConCantidad(Producto existente, Producto nuevo) {
		// Limpiar las líneas de receta existentes
		existente.getLineasReceta().clear();
		
		if (nuevo.getLineasReceta() == null || nuevo.getLineasReceta().isEmpty()) {
			return;
		}
		
		// Procesar cada línea de receta del nuevo producto
		for (ProductoReceta linea : nuevo.getLineasReceta()) {
			if (linea == null || linea.getId() == null || linea.getId().getIngredienteId() == null) {
				continue;
			}
			
			Long ingredienteId = linea.getId().getIngredienteId();
			Double cantidad = linea.getCantidad();
			
			// Obtener la referencia del ingrediente
			Ingrediente ingrediente = ingredienteRepository.getReferenceById(ingredienteId);
			
			// Crear una nueva línea de receta con la cantidad
			ProductoReceta nuevaLinea = new ProductoReceta(existente, ingrediente, cantidad);
			existente.getLineasReceta().add(nuevaLinea);
		}
	}
}
