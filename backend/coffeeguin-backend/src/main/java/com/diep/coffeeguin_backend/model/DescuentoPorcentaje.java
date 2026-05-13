package com.diep.coffeeguin_backend.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("PORCENTAJE")
public class DescuentoPorcentaje extends EstrategiaDescuento {
    
    @Column(nullable = false)
    private Double porcentaje;
    
    // Constructores
    public DescuentoPorcentaje() {
    }
    
    public DescuentoPorcentaje(String nombre, Double porcentaje) {
        super(nombre, "Descuento por porcentaje");
        this.porcentaje = porcentaje;
    }
    
    public DescuentoPorcentaje(String nombre, String descripcion, Double porcentaje) {
        super(nombre, descripcion);
        this.porcentaje = porcentaje;
    }
    
    @Override
    public double calcularTotalConDescuento(double subtotal) {
        if (porcentaje <= 0 || porcentaje > 100) {
            return subtotal;
        }
        double descuento = subtotal * (porcentaje / 100);
        return subtotal - descuento;
    }
    
    // Getters y setters
    public Double getPorcentaje() {
        return porcentaje;
    }
    
    public void setPorcentaje(Double porcentaje) {
        if (porcentaje >= 0 && porcentaje <= 100) {
            this.porcentaje = porcentaje;
        }
    }
}
