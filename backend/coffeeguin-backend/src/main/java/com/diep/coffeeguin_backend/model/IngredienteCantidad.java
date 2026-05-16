package com.diep.coffeeguin_backend.model;

/**
 * DTO para representar un ingrediente con su cantidad en una receta de producto
 */
public class IngredienteCantidad {
	
	private Long id;
	private String nombre;
	private Double cantidad;
	private Double stockActual;
	
	public IngredienteCantidad() {
	}
	
	public IngredienteCantidad(Long id, Double cantidad) {
		this.id = id;
		this.cantidad = cantidad;
	}
	
	public IngredienteCantidad(Long id, String nombre, Double cantidad) {
		this.id = id;
		this.nombre = nombre;
		this.cantidad = cantidad;
	}
	
	public IngredienteCantidad(Long id, String nombre, Double cantidad, Double stockActual) {
		this.id = id;
		this.nombre = nombre;
		this.cantidad = cantidad;
		this.stockActual = stockActual;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public Double getCantidad() {
		return cantidad;
	}
	
	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}
	
	public Double getStockActual() {
		return stockActual;
	}
	
	public void setStockActual(Double stockActual) {
		this.stockActual = stockActual;
	}
}
