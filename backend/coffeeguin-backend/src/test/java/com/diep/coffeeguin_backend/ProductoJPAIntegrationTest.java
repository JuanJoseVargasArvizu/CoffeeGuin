package com.diep.coffeeguin_backend;

import com.diep.coffeeguin_backend.model.*;
import com.diep.coffeeguin_backend.repository.CategoriaRepository;
import com.diep.coffeeguin_backend.repository.IngredienteRepository;
import com.diep.coffeeguin_backend.repository.ProductoRepository;
import com.diep.coffeeguin_backend.service.IngredienteService;
import com.diep.coffeeguin_backend.service.ProductoService;
import com.diep.coffeeguin_backend.service.RecetaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para la migración a JPA de productos e ingredientes.
 * Valida que los servicios funcionen correctamente con Spring Data JPA.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductoJPAIntegrationTest {

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private IngredienteRepository ingredienteRepository;

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private ProductoService productoService;

	@Autowired
	private IngredienteService ingredienteService;

	@Autowired
	private RecetaService recetaService;

	private Categoria categoriaBebidasImport;

	@BeforeEach
	void setup() {
		// Limpiar y crear categoría
		categoriaRepository.deleteAll();
		categoriaBebidasImport = new Categoria();
		categoriaBebidasImport.setNombre("Bebidas");
		categoriaBebidasImport = categoriaRepository.save(categoriaBebidasImport);
	}

	@Test
	void testCrearIngrediente() {
		// Arrange
		Ingrediente cafe = new Ingrediente();
		cafe.setNombre("Café");
		cafe.setPrecio(0.50);
		cafe.setStockActual(100);
		cafe.setUmbralAlerta(20);
		cafe.setCategoria(categoriaBebidasImport);

		// Act
		Ingrediente saved = ingredienteRepository.save(cafe);

		// Assert
		assertNotNull(saved.getId());
		assertEquals("Café", saved.getNombre());
		assertEquals(100, saved.getStockActual());
		assertTrue(saved instanceof Ingrediente);
	}

	@Test
	void testCrearBebidaConReceta() {
		// Arrange: Crear ingredientes
		Ingrediente cafe = new Ingrediente();
		cafe.setNombre("Café");
		cafe.setStockActual(100);
		cafe.setUmbralAlerta(20);
		cafe.setCategoria(categoriaBebidasImport);
		cafe = ingredienteRepository.save(cafe);

		Ingrediente leche = new Ingrediente();
		leche.setNombre("Leche");
		leche.setStockActual(50);
		leche.setUmbralAlerta(10);
		leche.setCategoria(categoriaBebidasImport);
		leche = ingredienteRepository.save(leche);

		// Crear bebida
		Bebida cafeConLeche = new Bebida();
		cafeConLeche.setNombre("Café con Leche");
		cafeConLeche.setPrecio(3.50);
		cafeConLeche.setCategoria(categoriaBebidasImport);
		cafeConLeche.setIngredientes(List.of(cafe, leche));

		// Act
		Bebida saved = (Bebida) productoRepository.save(cafeConLeche);

		// Assert
		assertNotNull(saved.getId());
		assertEquals("Café con Leche", saved.getNombre());
		assertEquals(2, saved.getIngredientes().size());
		assertTrue(saved instanceof Bebida);
	}

	@Test
	void testObtenerProductoConReceta() {
		// Arrange
		Ingrediente cafe = new Ingrediente();
		cafe.setNombre("Café");
		cafe.setStockActual(100);
		cafe.setUmbralAlerta(20);
		cafe.setCategoria(categoriaBebidasImport);
		cafe = ingredienteRepository.save(cafe);

		Bebida espresso = new Bebida();
		espresso.setNombre("Espresso");
		espresso.setCategoria(categoriaBebidasImport);
		espresso.setIngredientes(List.of(cafe));
		espresso = (Bebida) productoRepository.save(espresso);

		// Act
		Producto recuperado = productoRepository.findByIdWithReceta(espresso.getId()).orElse(null);

		// Assert
		assertNotNull(recuperado);
		assertEquals(espresso.getId(), recuperado.getId());
		assertEquals(1, recuperado.getIngredientes().size());
		assertEquals("Café", recuperado.getIngredientes().get(0).getNombre());
	}

	@Test
	void testListarProductosDisponibles() {
		// Arrange: Crear ingredientes con diferente stock
		Ingrediente conStock = new Ingrediente();
		conStock.setNombre("Con Stock");
		conStock.setStockActual(50);
		conStock.setUmbralAlerta(10);
		conStock.setCategoria(categoriaBebidasImport);
		conStock = ingredienteRepository.save(conStock);

		Ingrediente sinStock = new Ingrediente();
		sinStock.setNombre("Sin Stock");
		sinStock.setStockActual(0);
		sinStock.setUmbralAlerta(10);
		sinStock.setCategoria(categoriaBebidasImport);
		sinStock = ingredienteRepository.save(sinStock);

		// Crear bebida disponible
		Bebida bebidaDisp = new Bebida();
		bebidaDisp.setNombre("Bebida Disponible");
		bebidaDisp.setCategoria(categoriaBebidasImport);
		bebidaDisp.setIngredientes(List.of(conStock));
		productoRepository.save(bebidaDisp);

		// Crear ingrediente sin stock (no disponible)
		Ingrediente noDisp = new Ingrediente();
		noDisp.setNombre("Ingrediente No Disponible");
		noDisp.setStockActual(0);
		noDisp.setUmbralAlerta(10);
		noDisp.setCategoria(categoriaBebidasImport);
		productoRepository.save(noDisp);

		// Act
		List<Producto> disponibles = productoRepository.findAllDisponiblesWithReceta();

		// Assert: La bebida debe aparecer (tipo != ingrediente), 
		// el ingrediente con stock debe aparecer (stock > 0),
		// el ingrediente sin stock no debe aparecer
		assertEquals(2, disponibles.size());
		assertTrue(disponibles.stream().anyMatch(p -> p.getNombre().equals("Bebida Disponible")));
		assertTrue(disponibles.stream().anyMatch(p -> p.getNombre().equals("Con Stock")));
		assertFalse(disponibles.stream().anyMatch(p -> p.getNombre().equals("Ingrediente No Disponible")));
	}

	@Test
	void testConsumorStock() {
		// Arrange
		Ingrediente cafe = new Ingrediente();
		cafe.setNombre("Café");
		cafe.setStockActual(100);
		cafe.setUmbralAlerta(20);
		cafe.setCategoria(categoriaBebidasImport);
		cafe = ingredienteRepository.save(cafe);

		// Act
		Ingrediente actualizado = ingredienteService.consumirStock(cafe.getId(), 30);

		// Assert
		assertEquals(70, actualizado.getStockActual());
	}

	@Test
	void testConsumirStockAlertaNotificacion() {
		// Arrange
		Ingrediente cafe = new Ingrediente();
		cafe.setNombre("Café");
		cafe.setStockActual(25);
		cafe.setUmbralAlerta(20);
		cafe.setCategoria(categoriaBebidasImport);
		cafe = ingredienteRepository.save(cafe);

		// Act
		Ingrediente actualizado = ingredienteService.consumirStock(cafe.getId(), 10);

		// Assert: Stock cae a 15, por debajo del umbral (20)
		assertEquals(15, actualizado.getStockActual());
		assertTrue(actualizado.getStockActual() <= actualizado.getUmbralAlerta());
	}

	@Test
	void testRecetaService() {
		// Arrange
		Ingrediente cafe = new Ingrediente();
		cafe.setNombre("Café");
		cafe.setStockActual(100);
		cafe.setUmbralAlerta(20);
		cafe.setCategoria(categoriaBebidasImport);
		cafe = ingredienteRepository.save(cafe);

		Bebida espresso = new Bebida();
		espresso.setNombre("Espresso");
		espresso.setCategoria(categoriaBebidasImport);
		espresso.setIngredientes(List.of(cafe));
		espresso = (Bebida) productoRepository.save(espresso);

		// Act
		List<Ingrediente> ingredientes = recetaService.obtenerIngredientesDeProducto(espresso.getId());
		boolean esPreparable = recetaService.esPreparableAhora(espresso.getId());

		// Assert
		assertEquals(1, ingredientes.size());
		assertEquals("Café", ingredientes.get(0).getNombre());
		assertTrue(esPreparable);
	}

	@Test
	void testPolimorfismoJSON() {
		// Arrange
		Ingrediente ing = new Ingrediente();
		ing.setNombre("Azúcar");
		ing.setTipo("ingrediente"); // Type info para JSON
		ing.setStockActual(200);
		ing.setUmbralAlerta(50);
		ing.setCategoria(categoriaBebidasImport);

		Bebida beb = new Bebida();
		beb.setNombre("Café");
		beb.setTipo("bebida"); // Type info para JSON
		beb.setCategoria(categoriaBebidasImport);

		// Act
		Ingrediente savedIng = ingredienteRepository.save(ing);
		Bebida savedBeb = (Bebida) productoRepository.save(beb);

		// Assert
		assertEquals("ingrediente", savedIng.getTipo());
		assertEquals("bebida", savedBeb.getTipo());
		assertTrue(savedIng instanceof Ingrediente);
		assertTrue(savedBeb instanceof Bebida);
	}
}
