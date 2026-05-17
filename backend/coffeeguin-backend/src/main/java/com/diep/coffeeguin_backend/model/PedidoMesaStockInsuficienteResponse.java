package com.diep.coffeeguin_backend.model;

import java.util.ArrayList;
import java.util.List;

public class PedidoMesaStockInsuficienteResponse {

	private String mensaje;
	private List<ProductoStockInsuficienteDTO> productos = new ArrayList<>();

	public PedidoMesaStockInsuficienteResponse() {
	}

	public PedidoMesaStockInsuficienteResponse(String mensaje, List<ProductoStockInsuficienteDTO> productos) {
		this.mensaje = mensaje;
		this.productos = productos;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public List<ProductoStockInsuficienteDTO> getProductos() {
		return productos;
	}

	public void setProductos(List<ProductoStockInsuficienteDTO> productos) {
		this.productos = productos;
	}
}