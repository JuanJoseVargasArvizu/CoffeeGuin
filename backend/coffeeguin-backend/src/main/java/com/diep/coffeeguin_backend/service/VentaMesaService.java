package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.model.Cliente;
import com.diep.coffeeguin_backend.model.Mesa;
import com.diep.coffeeguin_backend.model.Producto;
import com.diep.coffeeguin_backend.model.ProductoMesa;
import com.diep.coffeeguin_backend.model.Venta;
import com.diep.coffeeguin_backend.model.VentaDetalle;
import com.diep.coffeeguin_backend.model.VentaMesaRequest;
import com.diep.coffeeguin_backend.model.VentaMesaResponse;
import com.diep.coffeeguin_backend.repository.ClienteRepository;
import com.diep.coffeeguin_backend.repository.MesaRepository;
import com.diep.coffeeguin_backend.repository.ProductoMesaRepository;
import com.diep.coffeeguin_backend.repository.ProductoRepository;
import com.diep.coffeeguin_backend.repository.VentaDetalleRepository;
import com.diep.coffeeguin_backend.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class VentaMesaService {

	private final VentaRepository ventaRepository;
	private final VentaDetalleRepository ventaDetalleRepository;
	private final MesaRepository mesaRepository;
	private final ClienteRepository clienteRepository;
	private final ProductoMesaRepository productoMesaRepository;
	private final ProductoRepository productoRepository;

	public VentaMesaService(VentaRepository ventaRepository,
			VentaDetalleRepository ventaDetalleRepository,
			MesaRepository mesaRepository,
			ClienteRepository clienteRepository,
			ProductoMesaRepository productoMesaRepository,
			ProductoRepository productoRepository) {
		this.ventaRepository = ventaRepository;
		this.ventaDetalleRepository = ventaDetalleRepository;
		this.mesaRepository = mesaRepository;
		this.clienteRepository = clienteRepository;
		this.productoMesaRepository = productoMesaRepository;
		this.productoRepository = productoRepository;
	}

	@Transactional
	public VentaMesaResponse registrarVentaMesa(VentaMesaRequest request) {
		if (request == null || request.getMesaId() == null) {
			throw new IllegalArgumentException("La mesaId es obligatoria");
		}

		List<ProductoMesa> pendientes = productoMesaRepository.findByMesaIdAndEstadoPago(request.getMesaId().longValue(), "pendiente");
		if (pendientes.isEmpty()) {
			throw new IllegalStateException("No hay productos pendientes para cobrar en esta mesa");
		}

		Mesa mesa = mesaRepository.findById(request.getMesaId())
				.orElseThrow(() -> new NoSuchElementException("No existe una mesa con id " + request.getMesaId()));

		Cliente cliente = null;
		if (request.getClienteId() != null) {
			cliente = clienteRepository.findById(request.getClienteId())
					.orElseThrow(() -> new NoSuchElementException("No existe un cliente con id " + request.getClienteId()));
		}

		Map<Long, Producto> productosUnicos = new LinkedHashMap<>();
		List<VentaDetalle> detalles = new ArrayList<>();
		double totalOriginal = 0.0;

		for (ProductoMesa pedido : pendientes) {
			Long productoId = pedido.getProducto().getId();
			Producto producto = productoRepository.findByIdWithReceta(productoId)
					.orElseThrow(() -> new NoSuchElementException("No existe un producto con id " + productoId));
			String tipo = producto.getTipo();
			if (tipo == null || (!"bebida".equalsIgnoreCase(tipo) && !"alimento".equalsIgnoreCase(tipo))) {
				throw new IllegalArgumentException("Solo se pueden vender productos de tipo bebida o alimento");
			}

			productosUnicos.putIfAbsent(producto.getId(), producto);
			double subtotalLinea = producto.getPrecio() * pedido.getCantidad();
			totalOriginal += subtotalLinea;

			VentaDetalle detalle = new VentaDetalle();
			detalle.setProducto(producto);
			detalle.setCantidad(pedido.getCantidad());
			detalle.setPrecioUnitario(producto.getPrecio());
			detalle.setSubtotalLinea(subtotalLinea);
			detalles.add(detalle);
		}

		double totalConDescuento = totalOriginal;
		if (cliente != null && cliente.getEstrategia() != null && cliente.getEstrategia().getActiva()) {
			totalConDescuento = cliente.aplicarDescuento(totalOriginal);
		}
		double montoDescuento = totalOriginal - totalConDescuento;

		Venta venta = new Venta();
		venta.setFecha(LocalDateTime.now());
		venta.setMesa(mesa);
		venta.setCliente(cliente);
		venta.setProductos(new ArrayList<>(productosUnicos.values()));
		venta.setSubtotal(totalOriginal);
		venta.setTotalFinal(totalConDescuento);
		Venta ventaGuardada = ventaRepository.save(venta);

		for (VentaDetalle detalle : detalles) {
			detalle.setVenta(ventaGuardada);
		}
		ventaDetalleRepository.saveAll(detalles);

		for (ProductoMesa pedido : pendientes) {
			pedido.setVenta(ventaGuardada);
			pedido.setEstadoPago("pagado");
		}
		productoMesaRepository.saveAll(pendientes);

		Mesa mesaPersistida = mesaRepository.findById(mesa.getId())
				.orElseThrow(() -> new NoSuchElementException("No existe una mesa con id " + mesa.getId()));
		mesaPersistida.setEstado("Libre");
		mesaRepository.save(mesaPersistida);

		return new VentaMesaResponse(ventaGuardada.getIdVenta(), totalOriginal, totalConDescuento, montoDescuento);
	}
}