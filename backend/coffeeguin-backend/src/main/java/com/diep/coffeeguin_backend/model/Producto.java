package com.diep.coffeeguin_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo", visible = true)
@JsonSubTypes({
	@JsonSubTypes.Type(value = Ingrediente.class, name = "ingrediente"),
	@JsonSubTypes.Type(value = Bebida.class, name = "bebida"),
	@JsonSubTypes.Type(value = Alimento.class, name = "alimento")
})

@Entity 
@Table(name = "producto")
@Inheritance(strategy = InheritanceType.JOINED)

public abstract class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre;
	private double precio;
	private String descripcion;
	private String tipo;
	@ManyToOne
    @JoinColumn(name = "categoria_id")
	private Categoria categoria;

	@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ProductoReceta> lineasReceta = new ArrayList<>();

	protected Producto() {
	}

	protected Producto(String tipo) {
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

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@JsonIgnore
	public List<Ingrediente> getIngredientes() {
		return lineasReceta.stream()
				.map(ProductoReceta::getIngrediente)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public void setIngredientes(List<Ingrediente> ingredientes) {
		setIngredientesConCantidad(ingredientes, null);
	}

	public void setIngredientesConCantidad(List<?> ingredientes) {
		lineasReceta.clear();
		if (ingredientes == null) {
			return;
		}
		for (Object obj : ingredientes) {
			if (obj == null) {
				continue;
			}
			if (obj instanceof Ingrediente ing) {
				ProductoReceta linea = new ProductoReceta(this, ing, 0.0);
				lineasReceta.add(linea);
			} else if (obj instanceof IngredienteCantidad ingCant) {
				if (ingCant.getId() == null) {
					continue;
				}
				// El ingrediente será resuelto por el servicio
				// Por ahora creamos un placeholder que será reemplazado
				ProductoReceta linea = new ProductoReceta(this, null, ingCant.getCantidad());
				linea.setId(new ProductoRecetaId(this.id, ingCant.getId()));
				lineasReceta.add(linea);
			}
		}
	}

	private void setIngredientesConCantidad(List<Ingrediente> ingredientes, Void unused) {
		lineasReceta.clear();
		if (ingredientes == null) {
			return;
		}
		for (Ingrediente ing : ingredientes) {
			if (ing == null) {
				continue;
			}
			ProductoReceta linea = new ProductoReceta(this, ing, 0.0);
			lineasReceta.add(linea);
		}
	}


	public List<ProductoReceta> getLineasReceta() {
		return lineasReceta;
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