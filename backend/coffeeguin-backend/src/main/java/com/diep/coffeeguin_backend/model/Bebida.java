package com.diep.coffeeguin_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "bebida")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
public class Bebida extends Producto {

	public Bebida() {
		super("bebida");
	}
}
