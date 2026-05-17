package com.diep.coffeeguin_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "productos_mesa")
public class ProductoMesa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_pedido_producto")
	private Integer idPedidoProducto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idmesa", nullable = false)
	@JsonIgnore
	private Mesa mesa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idproducto", nullable = false)
	@JsonIgnore
	private Producto producto;

	@Column(nullable = false)
	private Integer cantidad;

	@Column(name = "estado_pago", nullable = false, length = 20)
	private String estadoPago;

	@Column(name = "creado_at", nullable = false, updatable = false)
	private LocalDateTime creadoAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "venta_id")
	@JsonIgnore
	private Venta venta;

	public ProductoMesa() {
	}

	@PrePersist
	public void prePersist() {
		if (creadoAt == null) {
			creadoAt = LocalDateTime.now();
		}
	}

	public Integer getIdPedidoProducto() {
		return idPedidoProducto;
	}

	public void setIdPedidoProducto(Integer idPedidoProducto) {
		this.idPedidoProducto = idPedidoProducto;
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

	public LocalDateTime getCreadoAt() {
		return creadoAt;
	}

	public void setCreadoAt(LocalDateTime creadoAt) {
		this.creadoAt = creadoAt;
	}

	public Venta getVenta() {
		return venta;
	}

	public void setVenta(Venta venta) {
		this.venta = venta;
	}
}