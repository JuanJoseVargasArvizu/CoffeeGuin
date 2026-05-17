package com.diep.coffeeguin_backend.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public class PedidoMesaRequest {

	@JsonAlias({"mesaid", "mesaId"})
	private Integer mesaId;

	private List<ProductoPedidoMesaRequest> productos;

	@JsonAlias({"estado_pago", "estadoPago"})
	private String estadoPago;

	public PedidoMesaRequest() {
	}

	public Integer getMesaId() {
		return mesaId;
	}

	public void setMesaId(Integer mesaId) {
		this.mesaId = mesaId;
	}

	public List<ProductoPedidoMesaRequest> getProductos() {
		return productos;
	}

	public void setProductos(List<ProductoPedidoMesaRequest> productos) {
		this.productos = productos;
	}

	public String getEstadoPago() {
		return estadoPago;
	}

	public void setEstadoPago(String estadoPago) {
		this.estadoPago = estadoPago;
	}
}