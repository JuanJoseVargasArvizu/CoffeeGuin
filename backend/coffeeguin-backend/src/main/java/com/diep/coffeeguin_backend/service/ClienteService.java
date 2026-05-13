package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.dao.ClienteDAO;
import com.diep.coffeeguin_backend.model.Cliente;
import com.diep.coffeeguin_backend.model.EstrategiaDescuento;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClienteService {

	private final ClienteDAO clienteDAO;
	private final EstrategiaDescuentoService estrategiaService;

	public ClienteService(ClienteDAO clienteDAO, EstrategiaDescuentoService estrategiaService) {
		this.clienteDAO = clienteDAO;
		this.estrategiaService = estrategiaService;
	}

	/**
	 * Registra un nuevo cliente habitual
	 */
	public void registrarNuevoCliente(Cliente cliente) {
		validarClienteParaRegistro(cliente);
		if (clienteDAO.buscarPorEmail(cliente.getEmail()) != null) {
			throw new IllegalArgumentException("Ya existe un cliente registrado con ese email");
		}
		cliente.setFechaRegistro(LocalDateTime.now());
		cliente.setFechaActualizacion(LocalDateTime.now());
		cliente.setActivo(true);
		clienteDAO.agregar(cliente);
	}

	/**
	 * Consulta todos los clientes registrados
	 */
	public List<Cliente> consultarTodos() {
		return clienteDAO.listarTodos();
	}

	/**
	 * Consulta solo los clientes activos
	 */
	public List<Cliente> consultarActivos() {
		return clienteDAO.listarActivos();
	}

	/**
	 * Busca un cliente por ID
	 */
	public Cliente consultarPorId(int id) {
		Cliente cliente = clienteDAO.buscarPorId(id);
		if (cliente == null) {
			throw new NoSuchElementException("No existe un cliente con id " + id);
		}
		return cliente;
	}

	/**
	 * Busca un cliente por email
	 */
	public Cliente consultarPorEmail(String email) {
		Cliente cliente = clienteDAO.buscarPorEmail(email);
		if (cliente == null) {
			throw new NoSuchElementException("No existe un cliente con email " + email);
		}
		return cliente;
	}

	/**
	 * Busca un cliente por teléfono
	 */
	public Cliente consultarPorTelefono(String telefono) {
		Cliente cliente = clienteDAO.buscarPorTelefono(telefono);
		if (cliente == null) {
			throw new NoSuchElementException("No existe un cliente con teléfono " + telefono);
		}
		return cliente;
	}

	/**
	 * Actualiza la información de contacto de un cliente
	 */
	public void actualizarInformacionContacto(int clienteId, String email, String telefono, String direccion) {
		Cliente cliente = consultarPorId(clienteId);
		
		// Validar que el nuevo email no esté en uso (si es diferente)
		if (email != null && !email.equals(cliente.getEmail()) && clienteDAO.buscarPorEmail(email) != null) {
			throw new IllegalArgumentException("El email ingresado ya está registrado con otro cliente");
		}
		
		if (email != null) cliente.setEmail(email);
		if (telefono != null) cliente.setTelefono(telefono);
		if (direccion != null) cliente.setDireccion(direccion);
		
		cliente.setFechaActualizacion(LocalDateTime.now());
		clienteDAO.actualizar(cliente);
	}

	/**
	 * Actualiza las preferencias del cliente
	 */
	public void actualizarPreferencias(int clienteId, String preferencias, String alergias, String bebidaFavorita, String platoFavorito) {
		Cliente cliente = consultarPorId(clienteId);
		
		if (preferencias != null) cliente.setPreferencias(preferencias);
		if (alergias != null) cliente.setAlergias(alergias);
		if (bebidaFavorita != null) cliente.setBebidaFavorita(bebidaFavorita);
		if (platoFavorito != null) cliente.setPlatoFavorito(platoFavorito);
		
		cliente.setFechaActualizacion(LocalDateTime.now());
		clienteDAO.actualizar(cliente);
	}

	/**
	 * Asigna una estrategia de descuento a un cliente
	 */
	public void asignarEstrategiaDescuento(int clienteId, int estrategiaId) {
		Cliente cliente = consultarPorId(clienteId);
		EstrategiaDescuento estrategia = estrategiaService.consultarPorId(estrategiaId);
		
		cliente.setEstrategiaDescuento(estrategia);
		clienteDAO.actualizar(cliente);
	}

	/**
	 * Elimina la estrategia de descuento de un cliente
	 */
	public void eliminarEstrategiaDescuento(int clienteId) {
		Cliente cliente = consultarPorId(clienteId);
		cliente.setEstrategiaDescuento(null);
		clienteDAO.actualizar(cliente);
	}

	/**
	 * Calcula el precio final con descuento para un cliente
	 */
	public double calcularPrecioConDescuento(int clienteId, double totalVenta) {
		Cliente cliente = consultarPorId(clienteId);
		return cliente.aplicarDescuento(totalVenta);
	}

	/**
	 * Lista todos los clientes que tienen una estrategia de descuento específica
	 */
	public List<Cliente> consultarClientesPorEstrategia(int estrategiaId) {
		return clienteDAO.listarPorEstrategiaDescuento(estrategiaId);
	}

	/**
	 * Desactiva un cliente (soft delete)
	 */
	public void desactivarCliente(int clienteId) {
		Cliente cliente = consultarPorId(clienteId);
		cliente.setActivo(false);
		cliente.setFechaActualizacion(LocalDateTime.now());
		clienteDAO.actualizar(cliente);
	}

	/**
	 * Reactiva un cliente previamente desactivado
	 */
	public void reactivarCliente(int clienteId) {
		Cliente cliente = consultarPorId(clienteId);
		cliente.setActivo(true);
		cliente.setFechaActualizacion(LocalDateTime.now());
		clienteDAO.actualizar(cliente);
	}

	/**
	 * Elimina permanentemente un cliente
	 */
	public void eliminarCliente(int clienteId) {
		Cliente cliente = consultarPorId(clienteId);
		clienteDAO.eliminar(cliente);
	}

	/**
	 * Actualiza todos los datos de un cliente
	 */
	public void actualizarCliente(Cliente cliente) {
		validarClienteParaActualizacion(cliente);
		Cliente existente = consultarPorId(cliente.getId());
		
		// Validar que el nuevo email no esté en uso (si es diferente)
		if (!cliente.getEmail().equals(existente.getEmail()) && clienteDAO.buscarPorEmail(cliente.getEmail()) != null) {
			throw new IllegalArgumentException("El email ingresado ya está registrado con otro cliente");
		}
		
		cliente.setFechaActualizacion(LocalDateTime.now());
		clienteDAO.actualizar(cliente);
	}

	private void validarClienteParaRegistro(Cliente cliente) {
		if (cliente == null || cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
			throw new IllegalArgumentException("El nombre del cliente es requerido");
		}
		if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
			throw new IllegalArgumentException("El email del cliente es requerido");
		}
	}

	private void validarClienteParaActualizacion(Cliente cliente) {
		if (cliente == null || cliente.getId() == null) {
			throw new IllegalArgumentException("El cliente debe tener un ID para actualizarse");
		}
		if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
			throw new IllegalArgumentException("El nombre del cliente es requerido");
		}
		if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
			throw new IllegalArgumentException("El email del cliente es requerido");
		}
	}
}
