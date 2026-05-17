package com.diep.coffeeguin_backend.dto;

import java.time.LocalDateTime;

public class VentaResumenDTO {

	private Integer idVenta;
	private LocalDateTime fecha;
	private Double subtotal;
	private Double totalFinal;
	private String numeroMesa;
	private String nombreCliente;

	public VentaResumenDTO() {
	}

	public VentaResumenDTO(Integer idVenta, LocalDateTime fecha, Double subtotal, Double totalFinal,
			String numeroMesa, String nombreCliente) {
		this.idVenta = idVenta;
		this.fecha = fecha;
		this.subtotal = subtotal;
		this.totalFinal = totalFinal;
		this.numeroMesa = numeroMesa;
		this.nombreCliente = nombreCliente;
	}

	public Integer getIdVenta() {
		return idVenta;
	}

	public void setIdVenta(Integer idVenta) {
		this.idVenta = idVenta;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public Double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}

	public Double getTotalFinal() {
		return totalFinal;
	}

	public void setTotalFinal(Double totalFinal) {
		this.totalFinal = totalFinal;
	}

	public String getNumeroMesa() {
		return numeroMesa;
	}

	public void setNumeroMesa(String numeroMesa) {
		this.numeroMesa = numeroMesa;
	}

	public String getNombreCliente() {
		return nombreCliente;
	}

	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}
}