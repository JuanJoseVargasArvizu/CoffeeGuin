package com.diep.coffeeguin_backend.model;

import java.util.ArrayList;
import java.util.List;

public class ProductoStockInsuficienteDTO {

	private Long productoId;
	private String productoNombre;
	private List<IngredienteStockInsuficienteDTO> ingredientesFaltantes = new ArrayList<>();

	public ProductoStockInsuficienteDTO() {
	}

	public ProductoStockInsuficienteDTO(Long productoId, String productoNombre,
			List<IngredienteStockInsuficienteDTO> ingredientesFaltantes) {
		this.productoId = productoId;
		this.productoNombre = productoNombre;
		this.ingredientesFaltantes = ingredientesFaltantes;
	}

	public Long getProductoId() {
		return productoId;
	}

	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}

	public String getProductoNombre() {
		return productoNombre;
	}

	public void setProductoNombre(String productoNombre) {
		this.productoNombre = productoNombre;
	}

	public List<IngredienteStockInsuficienteDTO> getIngredientesFaltantes() {
		return ingredientesFaltantes;
	}

	public void setIngredientesFaltantes(List<IngredienteStockInsuficienteDTO> ingredientesFaltantes) {
		this.ingredientesFaltantes = ingredientesFaltantes;
	}
}