package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.model.Ingrediente;
import com.diep.coffeeguin_backend.model.Producto;
import com.diep.coffeeguin_backend.model.ProductoReceta;
import com.diep.coffeeguin_backend.repository.IngredienteRepository;
import com.diep.coffeeguin_backend.repository.ProductoRecetaRepository;
import com.diep.coffeeguin_backend.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Servicio para gestionar la relación receta (ProductoReceta):
 * qué ingredientes componen cada bebida/alimento y sus cantidades.
 */
@Service
public class RecetaService {

	private final ProductoRecetaRepository recetaRepository;
	private final ProductoRepository productoRepository;
	private final IngredienteRepository ingredienteRepository;

	public RecetaService(
			ProductoRecetaRepository recetaRepository,
			ProductoRepository productoRepository,
			IngredienteRepository ingredienteRepository) {
		this.recetaRepository = Objects.requireNonNull(recetaRepository, "recetaRepository");
		this.productoRepository = Objects.requireNonNull(productoRepository, "productoRepository");
		this.ingredienteRepository = Objects.requireNonNull(ingredienteRepository, "ingredienteRepository");
	}

	/**
	 * Obtiene todas las líneas de receta para un producto (los ingredientes que lo componen).
	 * @param productoId ID del producto
	 * @return Lista de líneas de receta
	 */
	@Transactional(readOnly = true)
	public List<ProductoReceta> obtenerRecetaDeProducto(Long productoId) {
		if (!productoRepository.existsById(productoId)) {
			throw new NoSuchElementException("No existe producto con id " + productoId);
		}
		return recetaRepository.findByProductoId(productoId);
	}

	/**
	 * Obtiene los ingredientes que componen un producto.
	 * @param productoId ID del producto
	 * @return Lista de ingredientes
	 */
	@Transactional(readOnly = true)
	public List<Ingrediente> obtenerIngredientesDeProducto(Long productoId) {
		return recetaRepository.findByProductoId(productoId).stream()
				.map(ProductoReceta::getIngrediente)
				.toList();
	}

	/**
	 * Obtiene todos los productos que usan un ingrediente específico.
	 * @param ingredienteId ID del ingrediente
	 * @return Lista de productos que lo incluyen
	 */
	@Transactional(readOnly = true)
	public List<Producto> obtenerProductosQueUsan(Long ingredienteId) {
		if (!ingredienteRepository.existsById(ingredienteId)) {
			throw new NoSuchElementException("No existe ingrediente con id " + ingredienteId);
		}
		return recetaRepository.findByIngredienteId(ingredienteId).stream()
				.map(ProductoReceta::getProducto)
				.toList();
	}

	/**
	 * Añade un ingrediente a la receta de un producto.
	 * @param productoId ID del producto (bebida/alimento)
	 * @param ingredienteId ID del ingrediente
	 * @return Línea de receta creada
	 * @throws IllegalArgumentException si ya existe esa relación o si no son del tipo correcto
	 */
	@Transactional
	public ProductoReceta agregarIngredienteAReceta(Long productoId, Long ingredienteId) {
		Producto producto = productoRepository.findById(productoId)
				.orElseThrow(() -> new NoSuchElementException("No existe producto con id " + productoId));

		if (producto instanceof Ingrediente) {
			throw new IllegalArgumentException("Un ingrediente no puede tener receta (no puede ser compuesto)");
		}

		Ingrediente ingrediente = ingredienteRepository.findById(ingredienteId)
				.orElseThrow(() -> new NoSuchElementException("No existe ingrediente con id " + ingredienteId));

		if (recetaRepository.existsByProductoIdAndIngredienteId(productoId, ingredienteId)) {
			throw new IllegalArgumentException(
					"El ingrediente " + ingredienteId + " ya está en la receta de " + productoId);
		}

		ProductoReceta linea = new ProductoReceta(producto, ingrediente);
		return recetaRepository.save(linea);
	}

	/**
	 * Elimina un ingrediente de la receta de un producto.
	 * @param productoId ID del producto
	 * @param ingredienteId ID del ingrediente
	 * @throws NoSuchElementException si la relación no existe
	 */
	@Transactional
	public void eliminarIngredienteDeReceta(Long productoId, Long ingredienteId) {
		List<ProductoReceta> receta = recetaRepository.findByProductoId(productoId);
		ProductoReceta linea = receta.stream()
				.filter(r -> r.getIngrediente().getId().equals(ingredienteId))
				.findFirst()
				.orElseThrow(() -> new NoSuchElementException(
						"El ingrediente " + ingredienteId + " no está en la receta de " + productoId));
		recetaRepository.delete(linea);
	}

	/**
	 * Reemplaza completamente la receta de un producto.
	 * @param productoId ID del producto
	 * @param nuevoIngredienteIds Lista de IDs de ingredientes (reemplaza la actual)
	 * @return Líneas de receta resultantes
	 */
	@Transactional
	public List<ProductoReceta> reemplazarReceta(Long productoId, List<Long> nuevoIngredienteIds) {
		Producto producto = productoRepository.findById(productoId)
				.orElseThrow(() -> new NoSuchElementException("No existe producto con id " + productoId));

		if (producto instanceof Ingrediente) {
			throw new IllegalArgumentException("Un ingrediente no puede tener receta");
		}

		// Eliminar líneas antiguas
		List<ProductoReceta> lineasAntiguas = recetaRepository.findByProductoId(productoId);
		recetaRepository.deleteAll(lineasAntiguas);

		// Crear líneas nuevas
		List<ProductoReceta> lineasNuevas = new ArrayList<>();
		if (nuevoIngredienteIds != null) {
			for (Long ingredienteId : nuevoIngredienteIds) {
				Ingrediente ingrediente = ingredienteRepository.findById(ingredienteId)
						.orElseThrow(() -> new NoSuchElementException("No existe ingrediente con id " + ingredienteId));
				ProductoReceta linea = new ProductoReceta(producto, ingrediente);
				lineasNuevas.add(recetaRepository.save(linea));
			}
		}
		return lineasNuevas;
	}

	/**
	 * Obtiene recetas (líneas) donde los ingredientes tienen stock bajo.
	 * Útil para alertas de falta de materia prima.
	 * @param umbralStock Umbral mínimo (ej: 5)
	 * @return Líneas de receta con ingredientes bajo stock
	 */
	@Transactional(readOnly = true)
	public List<ProductoReceta> obtenerRecetasConIngredienteBajoStock(int umbralStock) {
		return recetaRepository.findRecetasConIngredienteBajoStock(umbralStock);
	}

	/**
	 * Verifica si un producto tiene todos sus ingredientes disponibles.
	 * @param productoId ID del producto
	 * @return true si todos los ingredientes tienen stock > 0
	 */
	@Transactional(readOnly = true)
	public boolean esPreparableAhora(Long productoId) {
		List<Ingrediente> ingredientes = obtenerIngredientesDeProducto(productoId);
		if (ingredientes.isEmpty()) {
			// Producto sin ingredientes definidos (ej: bebida simple sin receta)
			return true;
		}
		return ingredientes.stream().allMatch(i -> i.getStockActual() > 0);
	}

	/**
	 * Verifica si un producto puede prepararse con una cierta cantidad.
	 * @param productoId ID del producto
	 * @param cantidad Cantidad de unidades a preparar
	 * @return true si hay stock suficiente de todos los ingredientes
	 */
	@Transactional(readOnly = true)
	public boolean esPreparable(Long productoId, int cantidad) {
		if (cantidad <= 0) {
			throw new IllegalArgumentException("La cantidad debe ser positiva");
		}
		List<Ingrediente> ingredientes = obtenerIngredientesDeProducto(productoId);
		if (ingredientes.isEmpty()) {
			return true;
		}
		// Por simplicidad, asumimos 1 unidad de cada ingrediente por unidad de producto
		// En una versión más sofisticada, ProductoReceta tendría un campo "cantidad"
		return ingredientes.stream().allMatch(i -> i.getStockActual() >= cantidad);
	}
}
