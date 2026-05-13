package com.diep.coffeeguin_backend.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo", visible = true)
@JsonSubTypes({
	@JsonSubTypes.Type(value = Ingrediente.class, name = "ingrediente"),
	@JsonSubTypes.Type(value = Bebida.class, name = "bebida"),
	@JsonSubTypes.Type(value = Alimento.class, name = "alimento")
})

@Entity 
@Table(name = "productos")
@Inheritance(strategy = InheritanceType.JOINED)

public abstract class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre;
	private double precio;
	@ManyToMany
    @JoinTable(
        name = "producto_receta",
        joinColumns = @JoinColumn(name = "producto_id"),
        inverseJoinColumns = @JoinColumn(name = "ingrediente_id")
    )
	private List<Ingrediente> receta;
	private String tipo;
	@ManyToOne
    @JoinColumn(name = "categoria_id")
	private Categoria categoria;

	protected Producto() {
		this.receta = new ArrayList<>();
	}

	protected Producto(String tipo) {
		this();
		this.tipo = tipo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public List<Ingrediente> getReceta() {
		return receta;
	}

	public void setReceta(List<Ingrediente> receta) {
		this.receta = receta != null ? receta : new ArrayList<>();
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
}