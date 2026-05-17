package com.diep.coffeeguin_backend.model;

public class IngredienteStockInsuficienteDTO {
	private Long ingredienteId;
	private String ingredienteNombre;
	private Integer cantidadRequerida;
	private Integer stockDisponible;
	private Integer cantidadFaltante;

	public IngredienteStockInsuficienteDTO() {
	}

	public IngredienteStockInsuficienteDTO(Long ingredienteId, String ingredienteNombre, Integer cantidadRequerida,
			Integer stockDisponible, Integer cantidadFaltante) {
		this.ingredienteId = ingredienteId;
		this.ingredienteNombre = ingredienteNombre;
		this.cantidadRequerida = cantidadRequerida;
		this.stockDisponible = stockDisponible;
		this.cantidadFaltante = cantidadFaltante;
	}

	public Long getIngredienteId() {
		return ingredienteId;
	}

	public void setIngredienteId(Long ingredienteId) {
		this.ingredienteId = ingredienteId;
	}

	public String getIngredienteNombre() {
		return ingredienteNombre;
	}

	public void setIngredienteNombre(String ingredienteNombre) {
		this.ingredienteNombre = ingredienteNombre;
	}

	public Integer getCantidadRequerida() {
		return cantidadRequerida;
	}

	public void setCantidadRequerida(Integer cantidadRequerida) {
		this.cantidadRequerida = cantidadRequerida;
	}

	public Integer getStockDisponible() {
		return stockDisponible;
	}

	public void setStockDisponible(Integer stockDisponible) {
		this.stockDisponible = stockDisponible;
	}

	public Integer getCantidadFaltante() {
		return cantidadFaltante;
	}

	public void setCantidadFaltante(Integer cantidadFaltante) {
		this.cantidadFaltante = cantidadFaltante;
	}
}