package com.diep.coffeeguin_backend.exception;

import com.diep.coffeeguin_backend.model.PedidoMesaStockInsuficienteResponse;

public class StockIngredientesInsuficienteException extends RuntimeException {

	private final transient PedidoMesaStockInsuficienteResponse response;

	public StockIngredientesInsuficienteException(PedidoMesaStockInsuficienteResponse response) {
		super(response != null ? response.getMensaje() : "Stock insuficiente");
		this.response = response;
	}

	public PedidoMesaStockInsuficienteResponse getResponse() {
		return response;
	}
}