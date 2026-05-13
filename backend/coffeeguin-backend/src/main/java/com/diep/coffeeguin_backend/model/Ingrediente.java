package com.diep.coffeeguin_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*; 

import java.util.ArrayList;
import java.util.List;

@Entity 
@Table(name = "ingredientes") 
public class Ingrediente extends Producto {

    @Column(nullable = false) 
    private int stockActual;
    
    @Column(nullable = false) 
    private int umbralAlerta;
    
    @JsonIgnore
    @Transient 
    private List<ObservadorInventario> observadores;

    public Ingrediente() {
        super("ingrediente");
        this.observadores = new ArrayList<>();
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getUmbralAlerta() {
        return umbralAlerta;
    }

    public void setUmbralAlerta(int umbralAlerta) {
        this.umbralAlerta = umbralAlerta;
    }

    public List<ObservadorInventario> getObservadores() {
        return observadores;
    }

    public void agregarObservador(ObservadorInventario obs) {
        if (obs != null && !observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    public void consumirStock(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad a consumir no puede ser negativa");
        }

        stockActual = Math.max(0, stockActual - cantidad);
        if (stockActual <= umbralAlerta) {
            notificarObservadores();
        }
    }

    private void notificarObservadores() {
        for (ObservadorInventario observador : new ArrayList<>(observadores)) {
            observador.actualizarInventario(this);
        }
    }
}