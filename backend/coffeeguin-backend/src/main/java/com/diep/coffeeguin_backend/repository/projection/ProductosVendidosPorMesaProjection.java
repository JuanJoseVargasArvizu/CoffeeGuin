package com.diep.coffeeguin_backend.repository.projection;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface ProductosVendidosPorMesaProjection {
	String getNumeroMesa();

	String getNombreProducto();

	BigInteger getCantidadVendida();

	BigDecimal getIngresos();
}