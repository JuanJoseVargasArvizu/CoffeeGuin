package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.exception.StockIngredientesInsuficienteException;
import com.diep.coffeeguin_backend.model.PedidoMesaStockInsuficienteResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = PedidoMesaController.class)
public class PedidoMesaExceptionHandler {
	@ExceptionHandler(StockIngredientesInsuficienteException.class)
	public ResponseEntity<PedidoMesaStockInsuficienteResponse> handleStockInsuficiente(
			StockIngredientesInsuficienteException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getResponse());
	}
}