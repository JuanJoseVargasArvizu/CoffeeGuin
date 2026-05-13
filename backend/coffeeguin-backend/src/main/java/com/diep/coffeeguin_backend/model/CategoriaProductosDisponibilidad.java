package com.diep.coffeeguin_backend.model;

import java.util.ArrayList;
import java.util.List;

public class CategoriaProductosDisponibilidad {

    private Categoria categoria;
    private List<Producto> disponibles;
    private List<Producto> noDisponibles;

    public CategoriaProductosDisponibilidad() {
        this.disponibles = new ArrayList<>();
        this.noDisponibles = new ArrayList<>();
    }

    public CategoriaProductosDisponibilidad(Categoria categoria) {
        this();
        this.categoria = categoria;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public List<Producto> getDisponibles() {
        return disponibles;
    }

    public void setDisponibles(List<Producto> disponibles) {
        this.disponibles = disponibles;
    }

    public List<Producto> getNoDisponibles() {
        return noDisponibles;
    }

    public void setNoDisponibles(List<Producto> noDisponibles) {
        this.noDisponibles = noDisponibles;
    }
}
