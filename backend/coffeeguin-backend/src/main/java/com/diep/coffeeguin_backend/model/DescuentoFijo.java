package com.diep.coffeeguin_backend.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("FIJO")
public class DescuentoFijo extends EstrategiaDescuento {
    
    @Column(nullable = false)
    private Double montoFijo;
    
    // Constructores
    public DescuentoFijo() {
    }
    
    public DescuentoFijo(String nombre, Double montoFijo) {
        super(nombre, "Descuento por monto fijo");
        this.montoFijo = montoFijo;
    }
    
    public DescuentoFijo(String nombre, String descripcion, Double montoFijo) {
        super(nombre, descripcion);
        this.montoFijo = montoFijo;
    }
    
    @Override
    public double calcularTotalConDescuento(double subtotal) {
        if (montoFijo <= 0 || montoFijo > subtotal) {
            return subtotal;
        }
        return subtotal - montoFijo;
    }
    
    // Getters y setters
    public Double getMontoFijo() {
        return montoFijo;
    }
    
    public void setMontoFijo(Double montoFijo) {
        if (montoFijo >= 0) {
            this.montoFijo = montoFijo;
        }
    }
}
