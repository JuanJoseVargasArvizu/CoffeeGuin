package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.model.Ingrediente;
import com.diep.coffeeguin_backend.repository.IngredienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Servicio para operaciones específicas de inventario de ingredientes.
 * Coordina actualización de stock y notificaciones de umbral bajo.
 */
@Service
public class IngredienteService {

	private final IngredienteRepository ingredienteRepository;

	public IngredienteService(IngredienteRepository ingredienteRepository) {
		this.ingredienteRepository = Objects.requireNonNull(ingredienteRepository, "ingredienteRepository");
	}

	/**
	 * Obtiene un ingrediente por ID.
	 * @param id ID del ingrediente
	 * @return Ingrediente encontrado
	 * @throws NoSuchElementException si no existe
	 */
	@Transactional(readOnly = true)
	public Ingrediente obtenerPorId(Long id) {
		return ingredienteRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("No existe ingrediente con id " + id));
	}

	/**
	 * Lista todos los ingredientes en el sistema.
	 * @return Lista de ingredientes
	 */
	@Transactional(readOnly = true)
	public List<Ingrediente> listarTodos() {
		return ingredienteRepository.findAll();
	}

	/**
	 * Lista ingredientes con stock por debajo del umbral de alerta.
	 * @param umbral Valor del umbral (ej: 5)
	 * @return Ingredientes bajo el umbral
	 */
	@Transactional(readOnly = true)
	public List<Ingrediente> listarPorBajoStock(int umbral) {
		List<Ingrediente> ingredientes = ingredienteRepository.findAll();
		return ingredientes.stream()
				.filter(i -> i.getStockActual() <= umbral)
				.toList();
	}

	/**
	 * Consume una cantidad de stock de un ingrediente (sin validar disponibilidad).
	 * Si el stock cae por debajo del umbral, notifica observadores.
	 *
	 * @param id ID del ingrediente
	 * @param cantidad Cantidad a consumir (debe ser positiva)
	 * @return Ingrediente actualizado
	 * @throws IllegalArgumentException si cantidad es negativa
	 * @throws NoSuchElementException si ingrediente no existe
	 */
	@Transactional
	public Ingrediente consumirStock(Long id, int cantidad) {
		if (cantidad < 0) {
			throw new IllegalArgumentException("La cantidad a consumir no puede ser negativa");
		}
		Ingrediente ing = obtenerPorId(id);
		ing.consumirStock(cantidad);
		return ingredienteRepository.save(ing);
	}

	/**
	 * Aumenta el stock de un ingrediente (ej: reabastecimiento).
	 * @param id ID del ingrediente
	 * @param cantidad Cantidad a añadir (debe ser positiva)
	 * @return Ingrediente actualizado
	 * @throws IllegalArgumentException si cantidad es negativa
	 * @throws NoSuchElementException si ingrediente no existe
	 */
	@Transactional
	public Ingrediente aumentarStock(Long id, int cantidad) {
		if (cantidad < 0) {
			throw new IllegalArgumentException("La cantidad a aumentar no puede ser negativa");
		}
		Ingrediente ing = obtenerPorId(id);
		int nuevoStock = ing.getStockActual() + cantidad;
		ing.setStockActual(nuevoStock);
		return ingredienteRepository.save(ing);
	}

	/**
	 * Establece el stock directamente (operación administrativa).
	 * @param id ID del ingrediente
	 * @param nuevoStock Nuevo valor de stock
	 * @return Ingrediente actualizado
	 * @throws IllegalArgumentException si stock es negativo
	 * @throws NoSuchElementException si ingrediente no existe
	 */
	@Transactional
	public Ingrediente establecerStock(Long id, int nuevoStock) {
		if (nuevoStock < 0) {
			throw new IllegalArgumentException("El stock no puede ser negativo");
		}
		Ingrediente ing = obtenerPorId(id);
		ing.setStockActual(nuevoStock);
		return ingredienteRepository.save(ing);
	}

	/**
	 * Verifica si hay stock disponible de un ingrediente.
	 * @param id ID del ingrediente
	 * @param cantidad Cantidad requerida
	 * @return true si hay stock >= cantidad
	 */
	@Transactional(readOnly = true)
	public boolean hayStockDisponible(Long id, int cantidad) {
		if (cantidad < 0) {
			throw new IllegalArgumentException("La cantidad no puede ser negativa");
		}
		return ingredienteRepository.findById(id)
				.map(ing -> ing.getStockActual() >= cantidad)
				.orElse(false);
	}

	/**
	 * Registra un nuevo ingrediente.
	 * @param ingrediente Ingrediente a registrar (id debe ser null)
	 * @return Ingrediente guardado con id asignado
	 */
	@Transactional
	public Ingrediente registrar(Ingrediente ingrediente) {
		if (ingrediente.getId() != null) {
			throw new IllegalArgumentException("El ingrediente no debe tener id para registrarse");
		}
		return ingredienteRepository.save(ingrediente);
	}

	/**
	 * Actualiza un ingrediente existente.
	 * @param ingrediente Ingrediente a actualizar (debe tener id)
	 * @return Ingrediente actualizado
	 * @throws NoSuchElementException si no existe
	 */
	@Transactional
	public Ingrediente actualizar(Ingrediente ingrediente) {
		if (ingrediente.getId() == null) {
			throw new IllegalArgumentException("El ingrediente debe tener id para actualizarse");
		}
		if (!ingredienteRepository.existsById(ingrediente.getId())) {
			throw new NoSuchElementException("No existe ingrediente con id " + ingrediente.getId());
		}
		return ingredienteRepository.save(ingrediente);
	}

	/**
	 * Elimina un ingrediente.
	 * @param id ID del ingrediente a eliminar
	 * @throws NoSuchElementException si no existe
	 */
	@Transactional
	public void eliminar(Long id) {
		if (!ingredienteRepository.existsById(id)) {
			throw new NoSuchElementException("No existe ingrediente con id " + id);
		}
		ingredienteRepository.deleteById(id);
	}

    @Transactional(readOnly = true)
    public List<Ingrediente> obtenerAlertasDeStock() {
        return ingredienteRepository.buscarIngredientesConBajoStock();
    }
}
