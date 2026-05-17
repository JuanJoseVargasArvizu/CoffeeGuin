package com.diep.coffeeguin_backend.repository.projection;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface ReporteFinancieroProjection {

	BigInteger getTotalTransacciones();

	BigDecimal getBalanceNeto();
}