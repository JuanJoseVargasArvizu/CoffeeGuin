package com.diep.coffeeguin_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos_mesa")
public class ProductoMesa {

	@EmbeddedId
	private ProductoMesaId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("mesaId")
	@JoinColumn(name = "idmesa", nullable = false)
	@JsonIgnore
	private Mesa mesa;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("productoId")
	@JoinColumn(name = "idproducto", nullable = false)
	@JsonIgnore
	private Producto producto;

	@Column(nullable = false)
	private Integer cantidad;

	@Column(name = "estado_pago", nullable = false, length = 20)
	private String estadoPago;

	public ProductoMesa() {
	}

	public ProductoMesaId getId() {
		return id;
	}

	public void setId(ProductoMesaId id) {
		this.id = id;
	}

	public Mesa getMesa() {
		return mesa;
	}

	public void setMesa(Mesa mesa) {
		this.mesa = mesa;
	}

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public String getEstadoPago() {
		return estadoPago;
	}

	public void setEstadoPago(String estadoPago) {
		this.estadoPago = estadoPago;
	}
}