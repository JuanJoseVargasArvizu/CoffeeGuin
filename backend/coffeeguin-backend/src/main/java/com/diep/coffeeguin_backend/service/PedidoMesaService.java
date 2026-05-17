package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.model.Ingrediente;
import com.diep.coffeeguin_backend.model.Mesa;
import com.diep.coffeeguin_backend.model.PedidoMesaRequest;
import com.diep.coffeeguin_backend.model.PedidoMesaStockInsuficienteResponse;
import com.diep.coffeeguin_backend.model.Producto;
import com.diep.coffeeguin_backend.model.ProductoMesa;
import com.diep.coffeeguin_backend.model.PedidoMesaPendientesResponse;
import com.diep.coffeeguin_backend.model.ProductoStockInsuficienteDTO;
import com.diep.coffeeguin_backend.model.ProductoPendienteMesaResponse;
import com.diep.coffeeguin_backend.model.ProductoPedidoMesaRequest;
import com.diep.coffeeguin_backend.model.ProductoReceta;
import com.diep.coffeeguin_backend.model.IngredienteStockInsuficienteDTO;
import com.diep.coffeeguin_backend.repository.MesaRepository;
import com.diep.coffeeguin_backend.repository.ProductoMesaRepository;
import com.diep.coffeeguin_backend.repository.ProductoRepository;
import com.diep.coffeeguin_backend.exception.StockIngredientesInsuficienteException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class PedidoMesaService {

	private final MesaRepository mesaRepository;
	private final ProductoRepository productoRepository;
	private final ProductoMesaRepository productoMesaRepository;
	private final IngredienteService ingredienteService;

	public PedidoMesaService(MesaRepository mesaRepository,
			ProductoRepository productoRepository,
			ProductoMesaRepository productoMesaRepository,
			IngredienteService ingredienteService) {
		this.mesaRepository = mesaRepository;
		this.productoRepository = productoRepository;
		this.productoMesaRepository = productoMesaRepository;
		this.ingredienteService = ingredienteService;
	}

	@Transactional
	public List<ProductoMesa> registrarPedidoMesa(PedidoMesaRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("El pedido de mesa no puede ser nulo");
		}
		if (request.getMesaId() == null) {
			throw new IllegalArgumentException("La mesaId es obligatoria");
		}
		if (request.getProductos() == null || request.getProductos().isEmpty()) {
			throw new IllegalArgumentException("Debe enviarse al menos un producto");
		}

		Mesa mesa = mesaRepository.findById(request.getMesaId())
				.orElseThrow(() -> new NoSuchElementException("No existe una mesa con id " + request.getMesaId()));
		if (mesa.getEstado() == null || !"Ocupada".equalsIgnoreCase(mesa.getEstado())) {
			throw new IllegalArgumentException("Solo se pueden asignar productos a mesas con estado Ocupada");
		}
		String estadoPago = request.getEstadoPago();
		if (estadoPago == null || estadoPago.isBlank()) {
			estadoPago = "pendiente";
		}

		Map<Long, Integer> cantidadesPorProducto = new LinkedHashMap<>();
		for (ProductoPedidoMesaRequest item : request.getProductos()) {
			if (item == null || item.getId() == null) {
				throw new IllegalArgumentException("Cada producto debe incluir id");
			}
			if (item.getCantidad() == null || item.getCantidad() <= 0) {
				throw new IllegalArgumentException("Cada producto debe incluir una cantidad mayor a cero");
			}
			Producto producto = productoRepository.findById(item.getId())
					.orElseThrow(() -> new NoSuchElementException(
							"No existe un producto con id " + item.getId()));
			String tipo = producto.getTipo();
			if (tipo == null || (!"bebida".equalsIgnoreCase(tipo) && !"alimento".equalsIgnoreCase(tipo))) {
				throw new IllegalArgumentException("Solo se pueden asignar productos de tipo bebida o alimento");
			}
			cantidadesPorProducto.merge(producto.getId(), item.getCantidad(), Integer::sum);
		}

		Map<Long, Producto> productosPorId = new LinkedHashMap<>();
		Map<Long, Integer> cantidadesPorIngrediente = new LinkedHashMap<>();
		Map<Long, Integer> stockRestantePorIngrediente = new LinkedHashMap<>();
		List<ProductoStockInsuficienteDTO> productosInsuficientes = new ArrayList<>();
		for (Map.Entry<Long, Integer> entry : cantidadesPorProducto.entrySet()) {
			Long productoId = entry.getKey();
			Integer cantidad = entry.getValue();
			Producto producto = productoRepository.findByIdWithReceta(productoId)
					.orElseThrow(() -> new NoSuchElementException("No existe un producto con id " + productoId));
			String tipo = producto.getTipo();
			if (tipo == null || (!"bebida".equalsIgnoreCase(tipo) && !"alimento".equalsIgnoreCase(tipo))) {
				throw new IllegalArgumentException("Solo se pueden asignar productos de tipo bebida o alimento");
			}
			productosPorId.put(productoId, producto);

			List<IngredienteStockInsuficienteDTO> ingredientesInsuficientes = new ArrayList<>();

			for (ProductoReceta lineaReceta : producto.getLineasReceta()) {
				if (lineaReceta == null || lineaReceta.getIngrediente() == null || lineaReceta.getIngrediente().getId() == null) {
					continue;
				}
				Ingrediente ingrediente = lineaReceta.getIngrediente();
				int requerido = (int) Math.ceil(lineaReceta.getCantidad() * cantidad);
				cantidadesPorIngrediente.merge(ingrediente.getId(), requerido, Integer::sum);

				int stockDisponible = stockRestantePorIngrediente.containsKey(ingrediente.getId())
						? stockRestantePorIngrediente.get(ingrediente.getId())
						: ingrediente.getStockActual();
				if (stockDisponible < requerido) {
					ingredientesInsuficientes.add(new IngredienteStockInsuficienteDTO(
						ingrediente.getId(),
						ingrediente.getNombre(),
						requerido,
						stockDisponible,
						requerido - stockDisponible
					));
				}
				stockRestantePorIngrediente.put(ingrediente.getId(), Math.max(0, stockDisponible - requerido));
			}

			if (!ingredientesInsuficientes.isEmpty()) {
				productosInsuficientes.add(new ProductoStockInsuficienteDTO(
					producto.getId(),
					producto.getNombre(),
					ingredientesInsuficientes
				));
			}
		}

		if (!productosInsuficientes.isEmpty()) {
			PedidoMesaStockInsuficienteResponse response = new PedidoMesaStockInsuficienteResponse(
				"No hay stock suficiente para registrar el pedido",
				productosInsuficientes
			);
			throw new StockIngredientesInsuficienteException(response);
		}

		for (Map.Entry<Long, Integer> entry : cantidadesPorIngrediente.entrySet()) {
			ingredienteService.consumirStock(entry.getKey(), entry.getValue());
		}

		List<ProductoMesa> registros = new ArrayList<>();
		for (Map.Entry<Long, Integer> entry : cantidadesPorProducto.entrySet()) {
			Long productoId = entry.getKey();
			Integer cantidad = entry.getValue();
			Producto producto = productosPorId.get(productoId);

			ProductoMesa registroExistente = productoMesaRepository
					.findByMesa_IdAndProducto_IdAndEstadoPago(mesa.getId(), productoId, estadoPago)
					.orElse(null);

			ProductoMesa registro = registroExistente != null ? registroExistente : new ProductoMesa();
			registro.setMesa(mesa);
			registro.setProducto(producto);
			registro.setCantidad(registroExistente != null ? registroExistente.getCantidad() + cantidad : cantidad);
			registro.setEstadoPago(estadoPago);
			registros.add(registro);
		}

		return productoMesaRepository.saveAll(registros);
	}
	@Transactional(readOnly = true)
	public PedidoMesaPendientesResponse obtenerPendientesMesa(Long mesaId) {
		List<ProductoMesa> pendientes = productoMesaRepository
				.findPendientesConProductoByMesaIdAndEstadoPago(mesaId, "pendiente");

		List<ProductoPendienteMesaResponse> productos = pendientes.stream()
				.map(pedido -> {
					double precioUnitario = pedido.getProducto().getPrecio();
					double subtotal = precioUnitario * pedido.getCantidad();
					return new ProductoPendienteMesaResponse(
							pedido.getProducto().getId(),
							pedido.getProducto().getNombre(),
							pedido.getCantidad(),
							precioUnitario,
							subtotal
					);
				})
				.collect(Collectors.toList());

		double totalEstimado = productos.stream()
				.mapToDouble(ProductoPendienteMesaResponse::getSubtotalEstimado)
				.sum();

		return new PedidoMesaPendientesResponse(mesaId, productos, totalEstimado);
	}
}