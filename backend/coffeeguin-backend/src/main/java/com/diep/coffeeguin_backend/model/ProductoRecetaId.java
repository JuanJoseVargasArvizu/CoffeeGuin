package com.diep.coffeeguin_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductoRecetaId implements Serializable {

	@Column(name = "producto_id")
	private Long productoId;

	@Column(name = "ingrediente_id")
	private Long ingredienteId;

	public ProductoRecetaId() {
	}

	public ProductoRecetaId(Long productoId, Long ingredienteId) {
		this.productoId = productoId;
		this.ingredienteId = ingredienteId;
	}

	public Long getProductoId() {
		return productoId;
	}

	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}

	public Long getIngredienteId() {
		return ingredienteId;
	}

	public void setIngredienteId(Long ingredienteId) {
		this.ingredienteId = ingredienteId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ProductoRecetaId that = (ProductoRecetaId) o;
		return Objects.equals(productoId, that.productoId) && Objects.equals(ingredienteId, that.ingredienteId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(productoId, ingredienteId);
	}
}