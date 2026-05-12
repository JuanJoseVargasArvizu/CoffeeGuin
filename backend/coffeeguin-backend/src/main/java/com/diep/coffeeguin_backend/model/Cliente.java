package com.diep.coffeeguin_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String contacto;

    @Transient
    private EstrategiaDescuento estrategia;

    // Constructores
    public Cliente() {
    }

    public Cliente(String nombre, String contacto) {
        this.nombre = nombre;
        this.contacto = contacto;
    }

    //strategy
    
    public void setEstrategiaDescuento(EstrategiaDescuento e) {
        this.estrategia = e;
    }

    public double aplicarDescuento(double total) {
        if (this.estrategia != null) {
            return this.estrategia.calcularTotalConDescuento(total);
        }
        return total; 
    }

    // getters y setters 
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }
}
