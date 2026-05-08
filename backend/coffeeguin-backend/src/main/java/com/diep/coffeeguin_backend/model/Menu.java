package com.diep.coffeeguin_backend.model;

import java.util.ArrayList;
import java.util.List;

public class Menu {

	private List<Categoria> categorias;

	public Menu() {
		this.categorias = new ArrayList<>();
	}

	public Menu(List<Categoria> categorias) {
		this.categorias = categorias != null ? categorias : new ArrayList<>();
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias != null ? categorias : new ArrayList<>();
	}
}
