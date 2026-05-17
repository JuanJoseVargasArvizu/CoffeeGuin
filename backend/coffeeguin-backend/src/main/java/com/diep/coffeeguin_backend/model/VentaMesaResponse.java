package com.diep.coffeeguin_backend.model;

public class VentaMesaResponse {

	private Integer ventaId;
	private double totalOriginal;
	private double totalConDescuento;
	private double montoDescuento;

	public VentaMesaResponse() {
	}

	public VentaMesaResponse(Integer ventaId, double totalOriginal, double totalConDescuento, double montoDescuento) {
		this.ventaId = ventaId;
		this.totalOriginal = totalOriginal;
		this.totalConDescuento = totalConDescuento;
		this.montoDescuento = montoDescuento;
	}

	public Integer getVentaId() {
		return ventaId;
	}

	public void setVentaId(Integer ventaId) {
		this.ventaId = ventaId;
	}

	public double getTotalOriginal() {
		return totalOriginal;
	}

	public void setTotalOriginal(double totalOriginal) {
		this.totalOriginal = totalOriginal;
	}

	public double getTotalConDescuento() {
		return totalConDescuento;
	}

	public void setTotalConDescuento(double totalConDescuento) {
		this.totalConDescuento = totalConDescuento;
	}

	public double getMontoDescuento() {
		return montoDescuento;
	}

	public void setMontoDescuento(double montoDescuento) {
		this.montoDescuento = montoDescuento;
	}
}