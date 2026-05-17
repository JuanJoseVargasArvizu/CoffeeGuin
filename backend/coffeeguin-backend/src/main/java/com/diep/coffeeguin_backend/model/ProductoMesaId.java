package com.diep.coffeeguin_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductoMesaId implements Serializable {

	@Column(name = "idmesa")
	private Integer mesaId;

	@Column(name = "idproducto")
	private Long productoId;

	public ProductoMesaId() {
	}

	public ProductoMesaId(Integer mesaId, Long productoId) {
		this.mesaId = mesaId;
		this.productoId = productoId;
	}

	public Integer getMesaId() {
		return mesaId;
	}

	public void setMesaId(Integer mesaId) {
		this.mesaId = mesaId;
	}

	public Long getProductoId() {
		return productoId;
	}

	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ProductoMesaId that = (ProductoMesaId) o;
		return Objects.equals(mesaId, that.mesaId) && Objects.equals(productoId, that.productoId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mesaId, productoId);
	}
}