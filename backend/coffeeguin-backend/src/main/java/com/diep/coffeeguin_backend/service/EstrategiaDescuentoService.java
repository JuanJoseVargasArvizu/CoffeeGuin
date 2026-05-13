package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.dao.EstrategiaDescuentoDAO;
import com.diep.coffeeguin_backend.model.DescuentoFijo;
import com.diep.coffeeguin_backend.model.DescuentoPorcentaje;
import com.diep.coffeeguin_backend.model.EstrategiaDescuento;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EstrategiaDescuentoService {

	private final EstrategiaDescuentoDAO estrategiaDAO;

	public EstrategiaDescuentoService(EstrategiaDescuentoDAO estrategiaDAO) {
		this.estrategiaDAO = estrategiaDAO;
	}

	/**
	 * Registra una nueva estrategia de descuento por porcentaje
	 */
	public void registrarDescuentoPorcentaje(String nombre, String descripcion, Double porcentaje) {
		validarPorcentaje(porcentaje);
		validarNombre(nombre);
		
		DescuentoPorcentaje estrategia = new DescuentoPorcentaje(nombre, descripcion, porcentaje);
		estrategiaDAO.agregar(estrategia);
	}

	/**
	 * Registra una nueva estrategia de descuento por monto fijo
	 */
	public void registrarDescuentoFijo(String nombre, String descripcion, Double montoFijo) {
		validarMontoFijo(montoFijo);
		validarNombre(nombre);
		
		DescuentoFijo estrategia = new DescuentoFijo(nombre, descripcion, montoFijo);
		estrategiaDAO.agregar(estrategia);
	}

	/**
	 * Consulta todas las estrategias de descuento
	 */
	public List<EstrategiaDescuento> consultarTodas() {
		return estrategiaDAO.listarTodas();
	}

	/**
	 * Consulta solo las estrategias de descuento activas
	 */
	public List<EstrategiaDescuento> consultarActivas() {
		return estrategiaDAO.listarActivas();
	}

	/**
	 * Busca una estrategia de descuento por ID
	 */
	public EstrategiaDescuento consultarPorId(int id) {
		EstrategiaDescuento estrategia = estrategiaDAO.buscarPorId(id);
		if (estrategia == null) {
			throw new NoSuchElementException("No existe una estrategia de descuento con id " + id);
		}
		return estrategia;
	}

	/**
	 * Lista todas las estrategias de descuento por porcentaje
	 */
	public List<DescuentoPorcentaje> consultarPorcentajes() {
		return estrategiaDAO.listarPorcentajes();
	}

	/**
	 * Lista todas las estrategias de descuento por monto fijo
	 */
	public List<DescuentoFijo> consultarFijos() {
		return estrategiaDAO.listarFijos();
	}

	/**
	 * Actualiza una estrategia de descuento existente
	 */
	public void actualizarEstrategia(EstrategiaDescuento estrategia) {
		validarEstrategiaParaActualizar(estrategia);
		
		// Verificar que exista
		consultarPorId(estrategia.getId());
		
		estrategiaDAO.actualizar(estrategia);
	}

	/**
	 * Actualiza solo el nombre de una estrategia
	 */
	public void actualizarNombre(int id, String nuevoNombre) {
		validarNombre(nuevoNombre);
		EstrategiaDescuento estrategia = consultarPorId(id);
		estrategia.setNombre(nuevoNombre);
		estrategiaDAO.actualizar(estrategia);
	}

	/**
	 * Actualiza solo la descripción de una estrategia
	 */
	public void actualizarDescripcion(int id, String nuevaDescripcion) {
		EstrategiaDescuento estrategia = consultarPorId(id);
		estrategia.setDescripcion(nuevaDescripcion);
		estrategiaDAO.actualizar(estrategia);
	}

	/**
	 * Actualiza el porcentaje de una estrategia de descuento por porcentaje
	 */
	public void actualizarPorcentaje(int id, Double nuevoPorcentaje) {
		validarPorcentaje(nuevoPorcentaje);
		EstrategiaDescuento estrategia = consultarPorId(id);
		
		if (!(estrategia instanceof DescuentoPorcentaje)) {
			throw new IllegalArgumentException("La estrategia con id " + id + " no es un descuento por porcentaje");
		}
		
		DescuentoPorcentaje descuento = (DescuentoPorcentaje) estrategia;
		descuento.setPorcentaje(nuevoPorcentaje);
		estrategiaDAO.actualizar(descuento);
	}

	/**
	 * Actualiza el monto fijo de una estrategia de descuento por monto fijo
	 */
	public void actualizarMontoFijo(int id, Double nuevoMonto) {
		validarMontoFijo(nuevoMonto);
		EstrategiaDescuento estrategia = consultarPorId(id);
		
		if (!(estrategia instanceof DescuentoFijo)) {
			throw new IllegalArgumentException("La estrategia con id " + id + " no es un descuento fijo");
		}
		
		DescuentoFijo descuento = (DescuentoFijo) estrategia;
		descuento.setMontoFijo(nuevoMonto);
		estrategiaDAO.actualizar(descuento);
	}

	/**
	 * Activa una estrategia de descuento
	 */
	public void activarEstrategia(int id) {
		EstrategiaDescuento estrategia = consultarPorId(id);
		estrategia.setActiva(true);
		estrategiaDAO.actualizar(estrategia);
	}

	/**
	 * Desactiva una estrategia de descuento
	 */
	public void desactivarEstrategia(int id) {
		EstrategiaDescuento estrategia = consultarPorId(id);
		estrategia.setActiva(false);
		estrategiaDAO.actualizar(estrategia);
	}

	/**
	 * Elimina una estrategia de descuento
	 */
	public void eliminarEstrategia(int id) {
		EstrategiaDescuento estrategia = consultarPorId(id);
		estrategiaDAO.eliminar(estrategia);
	}

	private void validarNombre(String nombre) {
		if (nombre == null || nombre.trim().isEmpty()) {
			throw new IllegalArgumentException("El nombre de la estrategia es requerido");
		}
	}

	private void validarPorcentaje(Double porcentaje) {
		if (porcentaje == null || porcentaje < 0 || porcentaje > 100) {
			throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
		}
	}

	private void validarMontoFijo(Double monto) {
		if (monto == null || monto < 0) {
			throw new IllegalArgumentException("El monto fijo debe ser mayor o igual a 0");
		}
	}

	private void validarEstrategiaParaActualizar(EstrategiaDescuento estrategia) {
		if (estrategia == null || estrategia.getId() == null) {
			throw new IllegalArgumentException("La estrategia debe tener un ID para actualizarse");
		}
		validarNombre(estrategia.getNombre());
	}
}
