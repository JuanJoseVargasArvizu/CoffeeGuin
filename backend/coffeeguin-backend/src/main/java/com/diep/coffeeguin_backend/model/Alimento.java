package com.diep.coffeeguin_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "alimento")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
public class Alimento extends Producto {

	public Alimento() {
		super("alimento");
	}
}
