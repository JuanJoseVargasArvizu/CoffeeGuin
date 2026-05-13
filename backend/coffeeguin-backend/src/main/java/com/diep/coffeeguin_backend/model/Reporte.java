package com.diep.coffeeguin_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reporte")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReporte;

    private String titulo;
    
    private LocalDateTime fechaGeneracion;
    
    private double totalCalculado;
    
    @Column(columnDefinition = "TEXT") 
    private String observaciones;

    @ManyToMany
    @JoinTable(
        name = "reporte_venta",
        joinColumns = @JoinColumn(name = "reporte_id"),
        inverseJoinColumns = @JoinColumn(name = "venta_id")
    )
    private List<Venta> ventas = new ArrayList<>();

    // constructor 
    public Reporte() {
        this.fechaGeneracion = LocalDateTime.now();
    }

    // metodos 

    public void imprimir() {
        System.out.println("Imprimiendo reporte: " + this.titulo);
    }

    public void exportarPDF() {
        System.out.println("Exportando a PDF...");
    }

    public Integer getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(Integer idReporte) {
        this.idReporte = idReporte;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public double getTotalCalculado() {
        return totalCalculado;
    }

    public void setTotalCalculado(double totalCalculado) {
        this.totalCalculado = totalCalculado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(List<Venta> ventas) {
        this.ventas = ventas;
    }
}
