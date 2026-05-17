package com.diep.coffeeguin_backend.dto;

public class VentaDetalleDTO {

	private String nombreProducto;
	private Integer cantidad;
	private Double precioUnitario;

	public VentaDetalleDTO() {
	}

	public VentaDetalleDTO(String nombreProducto, Integer cantidad, Double precioUnitario) {
		this.nombreProducto = nombreProducto;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
	}

	public String getNombreProducto() {
		return nombreProducto;
	}

	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public Double getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(Double precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
}