package com.diep.coffeeguin_backend.model;

public interface VentasPorCategoriaResumen {
	Long getCategoriaId();

	String getCategoriaNombre();

	Long getCantidadTotal();

	Double getTotalVendido();
}