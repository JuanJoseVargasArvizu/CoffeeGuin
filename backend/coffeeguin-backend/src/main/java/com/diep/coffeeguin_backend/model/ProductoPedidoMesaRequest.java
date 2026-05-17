package com.diep.coffeeguin_backend.model;

public class ProductoPedidoMesaRequest {

	private Long id;
	private Integer cantidad;

	public ProductoPedidoMesaRequest() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
}