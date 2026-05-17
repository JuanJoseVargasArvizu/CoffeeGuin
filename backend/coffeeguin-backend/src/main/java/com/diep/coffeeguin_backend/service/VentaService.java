package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.dto.VentaDetalleDTO;
import com.diep.coffeeguin_backend.dto.VentaResumenDTO;
import com.diep.coffeeguin_backend.model.Cliente;
import com.diep.coffeeguin_backend.model.Mesa;
import com.diep.coffeeguin_backend.model.Producto;
import com.diep.coffeeguin_backend.model.Venta;
import com.diep.coffeeguin_backend.repository.ProductoRepository;
import com.diep.coffeeguin_backend.repository.VentaDetalleRepository;
import com.diep.coffeeguin_backend.repository.VentaRepository;
import com.diep.coffeeguin_backend.repository.projection.VentaDetalleProjection;
import com.diep.coffeeguin_backend.repository.projection.VentaResumenProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class VentaService {

	private final VentaRepository ventaRepository;
	private final VentaDetalleRepository ventaDetalleRepository;
	private final ProductoRepository productoRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public VentaService(VentaRepository ventaRepository, VentaDetalleRepository ventaDetalleRepository,
			ProductoRepository productoRepository) {
		this.ventaRepository = ventaRepository;
		this.ventaDetalleRepository = ventaDetalleRepository;
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
	public List<VentaResumenDTO> listarResumenPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
		RangoFechas rango = resolverRangoFechas(fechaInicio, fechaFin);
		return ventaRepository.buscarResumenVentas(rango.inicio(), rango.fin()).stream()
				.map(this::mapearResumenVenta)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<VentaDetalleDTO> listarDetalleVenta(Integer ventaId) {
		if (ventaId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id de la venta es obligatorio");
		}
		if (!ventaRepository.existsById(ventaId)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe una venta con id " + ventaId);
		}

		return ventaDetalleRepository.buscarDetalleDeVenta(ventaId).stream()
				.map(this::mapearDetalleVenta)
				.toList();
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

	private VentaResumenDTO mapearResumenVenta(VentaResumenProjection projection) {
		return new VentaResumenDTO(
				projection.getIdVenta(),
				projection.getFecha(),
				projection.getSubtotal(),
				projection.getTotalFinal(),
				projection.getNumeroMesa(),
				projection.getNombreCliente());
	}

	private VentaDetalleDTO mapearDetalleVenta(VentaDetalleProjection projection) {
		return new VentaDetalleDTO(
				projection.getNombreProducto(),
				projection.getCantidad(),
				projection.getPrecioUnitario());
	}

	private RangoFechas resolverRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
		LocalDateTime ahora = LocalDateTime.now();
		LocalDateTime inicio = fechaInicio != null ? fechaInicio.atStartOfDay() : ahora.minusDays(30);
		LocalDateTime fin = fechaFin != null ? fechaFin.atTime(LocalTime.MAX) : ahora;

		if (inicio.isAfter(fin)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechaInicio no puede ser mayor que fechaFin");
		}

		return new RangoFechas(inicio, fin);
	}

	private record RangoFechas(LocalDateTime inicio, LocalDateTime fin) {
	}
}