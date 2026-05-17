package com.diep.coffeeguin_backend.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class VentaMesaRequest {

	@JsonAlias({"idmesa", "mesaId"})
	private Integer mesaId;

	@JsonAlias({"idcliente", "clienteId"})
	private Integer clienteId;

	public VentaMesaRequest() {
	}

	public Integer getMesaId() {
		return mesaId;
	}

	public void setMesaId(Integer mesaId) {
		this.mesaId = mesaId;
	}

	public Integer getClienteId() {
		return clienteId;
	}

	public void setClienteId(Integer clienteId) {
		this.clienteId = clienteId;
	}
}