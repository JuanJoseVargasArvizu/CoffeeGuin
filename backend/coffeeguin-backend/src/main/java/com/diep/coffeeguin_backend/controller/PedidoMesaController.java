package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.model.PedidoMesaRequest;
import com.diep.coffeeguin_backend.model.PedidoMesaPendientesResponse;
import com.diep.coffeeguin_backend.model.ProductoMesa;
import com.diep.coffeeguin_backend.service.PedidoMesaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos-mesa")
public class PedidoMesaController {

	private final PedidoMesaService pedidoMesaService;

	public PedidoMesaController(PedidoMesaService pedidoMesaService) {
		this.pedidoMesaService = pedidoMesaService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public List<ProductoMesa> registrarPedidoMesa(@RequestBody PedidoMesaRequest request) {
		return pedidoMesaService.registrarPedidoMesa(request);
	}

	@GetMapping("/mesa/{id}/pendientes")
	public PedidoMesaPendientesResponse obtenerPendientesMesa(@PathVariable("id") Long mesaId) {
		return pedidoMesaService.obtenerPendientesMesa(mesaId);
	}
}