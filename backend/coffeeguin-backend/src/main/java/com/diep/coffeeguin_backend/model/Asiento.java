package com.diep.coffeeguin_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "asientos")
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private int numero;

    @Column(nullable = false)
    private Boolean ocupado;

    // Constructor vacío
    public Asiento() {
    }

    // Constructor
    public Asiento(int numero, Boolean ocupado) {
        this.numero = numero;
        this.ocupado = ocupado;
    }

    // gettesr y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Boolean getOcupado() {
        return ocupado;
    }

    public void setOcupado(Boolean ocupado) {
        this.ocupado = ocupado;
    }
}
