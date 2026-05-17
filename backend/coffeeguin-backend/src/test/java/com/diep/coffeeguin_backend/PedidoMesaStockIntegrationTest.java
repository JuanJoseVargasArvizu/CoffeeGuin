package com.diep.coffeeguin_backend;

import com.diep.coffeeguin_backend.model.Bebida;
import com.diep.coffeeguin_backend.model.Categoria;
import com.diep.coffeeguin_backend.model.Ingrediente;
import com.diep.coffeeguin_backend.model.IngredienteCantidad;
import com.diep.coffeeguin_backend.model.Mesa;
import com.diep.coffeeguin_backend.model.PedidoMesaRequest;
import com.diep.coffeeguin_backend.model.PedidoMesaStockInsuficienteResponse;
import com.diep.coffeeguin_backend.model.Producto;
import com.diep.coffeeguin_backend.model.ProductoMesa;
import com.diep.coffeeguin_backend.model.ProductoPedidoMesaRequest;
import com.diep.coffeeguin_backend.exception.StockIngredientesInsuficienteException;
import com.diep.coffeeguin_backend.model.VentaMesaRequest;
import com.diep.coffeeguin_backend.repository.CategoriaRepository;
import com.diep.coffeeguin_backend.repository.IngredienteRepository;
import com.diep.coffeeguin_backend.repository.MesaRepository;
import com.diep.coffeeguin_backend.repository.ProductoMesaRepository;
import com.diep.coffeeguin_backend.repository.ProductoRepository;
import com.diep.coffeeguin_backend.service.PedidoMesaService;
import com.diep.coffeeguin_backend.service.ProductoService;
import com.diep.coffeeguin_backend.service.VentaMesaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PedidoMesaStockIntegrationTest {

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private ProductoMesaRepository productoMesaRepository;

	@Autowired
	private IngredienteRepository ingredienteRepository;

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private MesaRepository mesaRepository;

	@Autowired
	private ProductoService productoService;

	@Autowired
	private PedidoMesaService pedidoMesaService;

	@Autowired
	private VentaMesaService ventaMesaService;

	@BeforeEach
	void setup() {
		productoMesaRepository.deleteAll();
		productoRepository.deleteAll();
		ingredienteRepository.deleteAll();
		mesaRepository.deleteAll();
		categoriaRepository.deleteAll();
	}

	@Test
	void testPedidoMesaDescuentaStockYVentaNoLoRepite() {
		Categoria categoria = new Categoria();
		categoria.setNombre("Bebidas");
		categoria = categoriaRepository.save(categoria);

		Ingrediente cafe = new Ingrediente();
		cafe.setNombre("Cafe");
		cafe.setStockActual(10);
		cafe.setUmbralAlerta(2);
		cafe.setCategoria(categoria);
		cafe = ingredienteRepository.save(cafe);

		Bebida bebida = new Bebida();
		bebida.setNombre("Americano");
		bebida.setPrecio(35.0);
		bebida.setCategoria(categoria);
		bebida.setIngredientesConCantidad(List.of(new IngredienteCantidad(cafe.getId(), 1.0)));
		productoService.registrarNuevoProducto(bebida);

		Producto productoGuardado = productoRepository.findAllWithReceta().stream()
				.filter(p -> "Americano".equals(p.getNombre()))
				.findFirst()
				.orElseThrow();

		Mesa mesa = new Mesa();
		mesa.setNumero(1);
		mesa.setEstado("Ocupada");
		mesa = mesaRepository.save(mesa);

		PedidoMesaRequest primerPedido = new PedidoMesaRequest();
		primerPedido.setMesaId(mesa.getId());
		primerPedido.setEstadoPago("pendiente");
		ProductoPedidoMesaRequest item1 = new ProductoPedidoMesaRequest();
		item1.setId(productoGuardado.getId());
		item1.setCantidad(2);
		primerPedido.setProductos(List.of(item1));

		List<ProductoMesa> primerRegistro = pedidoMesaService.registrarPedidoMesa(primerPedido);
		assertEquals(1, primerRegistro.size());
		assertEquals(8, ingredienteRepository.findById(cafe.getId()).orElseThrow().getStockActual());

		PedidoMesaRequest segundoPedido = new PedidoMesaRequest();
		segundoPedido.setMesaId(mesa.getId());
		segundoPedido.setEstadoPago("pendiente");
		ProductoPedidoMesaRequest item2 = new ProductoPedidoMesaRequest();
		item2.setId(productoGuardado.getId());
		item2.setCantidad(1);
		segundoPedido.setProductos(List.of(item2));

		List<ProductoMesa> segundoRegistro = pedidoMesaService.registrarPedidoMesa(segundoPedido);
		assertEquals(1, segundoRegistro.size());

		ProductoMesa pedidoPendiente = productoMesaRepository
				.findByMesa_IdAndProducto_IdAndEstadoPago(mesa.getId(), productoGuardado.getId(), "pendiente")
				.orElseThrow();
		assertEquals(3, pedidoPendiente.getCantidad());
		assertEquals(7, ingredienteRepository.findById(cafe.getId()).orElseThrow().getStockActual());

		VentaMesaRequest cobro = new VentaMesaRequest();
		cobro.setMesaId(mesa.getId());
		cobro.setClienteId(null);
		ventaMesaService.registrarVentaMesa(cobro);

		assertEquals(7, ingredienteRepository.findById(cafe.getId()).orElseThrow().getStockActual());
		assertNotNull(productoMesaRepository
				.findByMesa_IdAndProducto_IdAndEstadoPago(mesa.getId(), productoGuardado.getId(), "pagado")
				.orElseThrow());
	}

	@Test
	void testPedidoMesaDevuelveDetalleDeStockInsuficiente() {
		Categoria categoria = new Categoria();
		categoria.setNombre("Bebidas");
		categoria = categoriaRepository.save(categoria);

		Ingrediente cafe = new Ingrediente();
		cafe.setNombre("Cafe");
		cafe.setStockActual(1);
		cafe.setUmbralAlerta(2);
		cafe.setCategoria(categoria);
		cafe = ingredienteRepository.save(cafe);

		Ingrediente leche = new Ingrediente();
		leche.setNombre("Leche");
		leche.setStockActual(0);
		leche.setUmbralAlerta(2);
		leche.setCategoria(categoria);
		leche = ingredienteRepository.save(leche);

		Bebida americano = new Bebida();
		americano.setNombre("Americano");
		americano.setPrecio(35.0);
		americano.setCategoria(categoria);
		americano.setIngredientesConCantidad(List.of(new IngredienteCantidad(cafe.getId(), 2.0)));
		productoService.registrarNuevoProducto(americano);

		Bebida latte = new Bebida();
		latte.setNombre("Latte");
		latte.setPrecio(40.0);
		latte.setCategoria(categoria);
		latte.setIngredientesConCantidad(List.of(new IngredienteCantidad(leche.getId(), 1.0)));
		productoService.registrarNuevoProducto(latte);

		Mesa mesa = new Mesa();
		mesa.setNumero(2);
		mesa.setEstado("Ocupada");
		mesa = mesaRepository.save(mesa);

		PedidoMesaRequest pedido = new PedidoMesaRequest();
		pedido.setMesaId(mesa.getId());
		pedido.setEstadoPago("pendiente");

		ProductoPedidoMesaRequest item1 = new ProductoPedidoMesaRequest();
		item1.setId(americano.getId());
		item1.setCantidad(1);

		ProductoPedidoMesaRequest item2 = new ProductoPedidoMesaRequest();
		item2.setId(latte.getId());
		item2.setCantidad(1);

		pedido.setProductos(List.of(item1, item2));

		StockIngredientesInsuficienteException exception = assertThrows(
				StockIngredientesInsuficienteException.class,
				() -> pedidoMesaService.registrarPedidoMesa(pedido)
		);

		PedidoMesaStockInsuficienteResponse response = exception.getResponse();
		assertEquals("No hay stock suficiente para registrar el pedido", response.getMensaje());
		assertEquals(2, response.getProductos().size());
		assertEquals("Americano", response.getProductos().get(0).getProductoNombre());
		assertEquals("Cafe", response.getProductos().get(0).getIngredientesFaltantes().get(0).getIngredienteNombre());
		assertEquals("Latte", response.getProductos().get(1).getProductoNombre());
		assertEquals("Leche", response.getProductos().get(1).getIngredientesFaltantes().get(0).getIngredienteNombre());
	}
}