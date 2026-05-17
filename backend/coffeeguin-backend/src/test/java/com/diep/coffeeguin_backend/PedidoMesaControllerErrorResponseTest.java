package com.diep.coffeeguin_backend;

import com.diep.coffeeguin_backend.controller.PedidoMesaController;
import com.diep.coffeeguin_backend.exception.StockIngredientesInsuficienteException;
import com.diep.coffeeguin_backend.model.IngredienteStockInsuficienteDTO;
import com.diep.coffeeguin_backend.model.PedidoMesaStockInsuficienteResponse;
import com.diep.coffeeguin_backend.model.ProductoStockInsuficienteDTO;
import com.diep.coffeeguin_backend.service.PedidoMesaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

class PedidoMesaControllerErrorResponseTest {

	private PedidoMesaService pedidoMesaService;

	private MockMvc mockMvc;

	@BeforeEach
	void setup() {
		pedidoMesaService = Mockito.mock(PedidoMesaService.class);
		mockMvc = MockMvcBuilders
				.standaloneSetup(new PedidoMesaController(pedidoMesaService))
				.setControllerAdvice(new TestPedidoMesaExceptionHandler())
				.build();
	}

	@Test
	void whenIngredientsMissingReturnsDetailedErrorResponse() throws Exception {
		PedidoMesaStockInsuficienteResponse response = new PedidoMesaStockInsuficienteResponse(
				"No hay stock suficiente para registrar el pedido",
				List.of(
					new ProductoStockInsuficienteDTO(
						10L,
						"Americano",
						List.of(new IngredienteStockInsuficienteDTO(1L, "Cafe", 2, 1, 1))
					),
					new ProductoStockInsuficienteDTO(
						11L,
						"Latte",
						List.of(new IngredienteStockInsuficienteDTO(2L, "Leche", 1, 0, 1))
					)
				)
		);

		when(pedidoMesaService.registrarPedidoMesa(any())).thenThrow(new StockIngredientesInsuficienteException(response));

		String request = """
			{
			  "mesaId": 1,
			  "estadoPago": "pendiente",
			  "productos": [
			    {"id": 10, "cantidad": 1},
			    {"id": 11, "cantidad": 1}
			  ]
			}
			""";

		mockMvc.perform(post("/api/pedidos-mesa")
				.contentType(MediaType.APPLICATION_JSON)
				.content(request))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.mensaje").value("No hay stock suficiente para registrar el pedido"))
				.andExpect(jsonPath("$.productos[0].productoNombre").value("Americano"))
				.andExpect(jsonPath("$.productos[0].ingredientesFaltantes[0].ingredienteNombre").value("Cafe"))
				.andExpect(jsonPath("$.productos[1].productoNombre").value("Latte"))
				.andExpect(jsonPath("$.productos[1].ingredientesFaltantes[0].ingredienteNombre").value("Leche"));
	}

	@RestControllerAdvice
	static class TestPedidoMesaExceptionHandler {
		@ExceptionHandler(StockIngredientesInsuficienteException.class)
		ResponseEntity<PedidoMesaStockInsuficienteResponse> handle(StockIngredientesInsuficienteException exception) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getResponse());
		}
	}
}