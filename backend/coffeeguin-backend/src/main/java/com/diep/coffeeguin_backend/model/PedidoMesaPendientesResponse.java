package com.diep.coffeeguin_backend.model;

import java.util.ArrayList;
import java.util.List;

public class PedidoMesaPendientesResponse {

	private Long mesaId;
	private List<ProductoPendienteMesaResponse> productos = new ArrayList<>();
	private double totalEstimado;

	public PedidoMesaPendientesResponse() {
	}

	public PedidoMesaPendientesResponse(Long mesaId, List<ProductoPendienteMesaResponse> productos, double totalEstimado) {
		this.mesaId = mesaId;
		this.productos = productos;
		this.totalEstimado = totalEstimado;
	}

	public Long getMesaId() {
		return mesaId;
	}

	public void setMesaId(Long mesaId) {
		this.mesaId = mesaId;
	}

	public List<ProductoPendienteMesaResponse> getProductos() {
		return productos;
	}

	public void setProductos(List<ProductoPendienteMesaResponse> productos) {
		this.productos = productos;
	}

	public double getTotalEstimado() {
		return totalEstimado;
	}

	public void setTotalEstimado(double totalEstimado) {
		this.totalEstimado = totalEstimado;
	}
}
