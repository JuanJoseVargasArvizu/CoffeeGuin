package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.model.Cliente;
import com.diep.coffeeguin_backend.model.Mesa;
import com.diep.coffeeguin_backend.model.Producto;
import com.diep.coffeeguin_backend.model.Venta;
import com.diep.coffeeguin_backend.repository.ProductoRepository;
import com.diep.coffeeguin_backend.repository.VentaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class VentaService {

	private final VentaRepository ventaRepository;
	private final ProductoRepository productoRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
		this.ventaRepository = ventaRepository;
		this.productoRepository = productoRepository;
	}

	@Transactional(readOnly = true)
	public List<Venta> listarTodas() {
		return ventaRepository.findAll();
	}

	@Transactional(readOnly = true)
	public List<Venta> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
		if (inicio != null && fin != null) {
			return ventaRepository.findByFechaBetween(inicio, fin);
		}
		if (inicio != null) {
			return ventaRepository.findByFechaGreaterThanEqual(inicio);
		}
		if (fin != null) {
			return ventaRepository.findByFechaLessThanEqual(fin);
		}
		return ventaRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Venta buscarPorId(Integer id) {
		return ventaRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("No existe una venta con id " + id));
	}

	@Transactional
	public Venta registrarVenta(Venta nuevaVenta) {
		if (nuevaVenta == null) {
			throw new IllegalArgumentException("La venta no puede ser nula");
		}

		if (nuevaVenta.getFecha() == null) {
			nuevaVenta.setFecha(LocalDateTime.now());
		}

		resolverCliente(nuevaVenta);
		resolverMesa(nuevaVenta);
		resolverProductos(nuevaVenta);

		double subtotalCalculado = nuevaVenta.getProductos().stream()
				.mapToDouble(Producto::getPrecio)
				.sum();
		nuevaVenta.setSubtotal(subtotalCalculado);

		if (nuevaVenta.getCliente() != null) {
			nuevaVenta.finalizarVenta();
		} else {
			nuevaVenta.setTotalFinal(nuevaVenta.getSubtotal());
		}

		Venta ventaGuardada = ventaRepository.save(nuevaVenta);
		actualizarEstadoMesaSiCorresponde(ventaGuardada.getMesa());
		return ventaGuardada;
	}

	private void resolverCliente(Venta venta) {
		Cliente cliente = venta.getCliente();
		if (cliente == null || cliente.getId() == null) {
			return;
		}
		venta.setCliente(entityManager.getReference(Cliente.class, cliente.getId()));
	}

	private void resolverMesa(Venta venta) {
		Mesa mesa = venta.getMesa();
		if (mesa == null || mesa.getId() == null) {
			return;
		}
		venta.setMesa(entityManager.getReference(Mesa.class, mesa.getId()));
	}

	private void resolverProductos(Venta venta) {
		if (venta.getProductos() == null || venta.getProductos().isEmpty()) {
			venta.setProductos(new ArrayList<>());
			return;
		}

		List<Producto> productosGestionados = new ArrayList<>();
		for (Producto producto : venta.getProductos()) {
			if (producto == null || producto.getId() == null) {
				throw new IllegalArgumentException("Cada producto de la venta debe tener id");
			}
			Producto productoExistente = productoRepository.findById(producto.getId())
					.orElseThrow(() -> new NoSuchElementException(
							"No existe un producto con id " + producto.getId()));
			productosGestionados.add(productoExistente);
		}
		venta.setProductos(productosGestionados);
	}

	private void actualizarEstadoMesaSiCorresponde(Mesa mesa) {
		if (mesa == null || mesa.getId() == null) {
			return;
		}
		Mesa mesaPersistida = entityManager.find(Mesa.class, mesa.getId());
		if (mesaPersistida != null) {
			mesaPersistida.setEstado("Ocupada");
			entityManager.merge(mesaPersistida);
		}
	}
}