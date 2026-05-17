package com.diep.coffeeguin_backend.model;

public interface VentasPorProductoResumen {
	Long getProductoId();

	String getProductoNombre();

	String getCategoriaNombre();

	Long getCantidadTotal();

	Double getTotalVendido();
}