package com.diep.coffeeguin_backend.repository.projection;

import java.time.LocalDateTime;

public interface VentaResumenProjection {

	Integer getIdVenta();

	LocalDateTime getFecha();

	Double getSubtotal();

	Double getTotalFinal();

	String getNumeroMesa();

	String getNombreCliente();
}