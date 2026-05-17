package com.diep.coffeeguin_backend.model;

public class ProductoPendienteMesaResponse {

	private Long productoId;
	private String nombre;
	private Integer cantidad;
	private double precioUnitario;
	private double subtotalEstimado;

	public ProductoPendienteMesaResponse() {
	}

	public ProductoPendienteMesaResponse(Long productoId, String nombre, Integer cantidad, double precioUnitario, double subtotalEstimado) {
		this.productoId = productoId;
		this.nombre = nombre;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
		this.subtotalEstimado = subtotalEstimado;
	}

	public Long getProductoId() {
		return productoId;
	}

	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public double getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(double precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	public double getSubtotalEstimado() {
		return subtotalEstimado;
	}

	public void setSubtotalEstimado(double subtotalEstimado) {
		this.subtotalEstimado = subtotalEstimado;
	}
}
