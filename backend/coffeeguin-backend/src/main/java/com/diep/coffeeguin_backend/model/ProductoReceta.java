package com.diep.coffeeguin_backend.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "producto_receta")
public class ProductoReceta {

	@EmbeddedId
	private ProductoRecetaId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("productoId")
	@JoinColumn(name = "producto_id", nullable = false)
	private Producto producto;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("ingredienteId")
	@JoinColumn(name = "ingrediente_id", nullable = false)
	private Ingrediente ingrediente;

	public ProductoReceta() {
	}

	public ProductoReceta(Producto producto, Ingrediente ingrediente) {
		this.producto = producto;
		this.ingrediente = ingrediente;
		// Inicializar el ID embebido con base en las relaciones
		// Esto será actualizado por Hibernate cuando se persista
		if (producto != null && ingrediente != null) {
			this.id = new ProductoRecetaId(producto.getId(), ingrediente.getId());
		}
	}

	public ProductoRecetaId getId() {
		return id;
	}

	public void setId(ProductoRecetaId id) {
		this.id = id;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public Ingrediente getIngrediente() {
		return ingrediente;
	}

	public void setIngrediente(Ingrediente ingrediente) {
		this.ingrediente = ingrediente;
	}
}