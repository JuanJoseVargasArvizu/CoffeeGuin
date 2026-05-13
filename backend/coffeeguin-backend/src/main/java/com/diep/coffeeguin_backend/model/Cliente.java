package com.diep.coffeeguin_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    // Información de contacto
    @Column(length = 100)
    private String email;

    @Column(length = 15)
    private String telefono;

    @Column(length = 255)
    private String direccion;

    @Column(name = "fecha_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaRegistro;

    // Preferencias del cliente
    @Column(columnDefinition = "TEXT")
    private String preferencias;

    @Column(length = 255)
    private String alergias;

    @Column(name = "bebida_favorita")
    private String bebidaFavorita;

    @Column(name = "plato_favorito")
    private String platoFavorito;

    // Estrategia de descuento (persistente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estrategia_descuento_id")
    private EstrategiaDescuento estrategia;

    // Auditoría
    @Column(name = "fecha_actualizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaActualizacion;

    @Column(nullable = false)
    private Boolean activo = true;

    // Constructores
    public Cliente() {
    }

    public Cliente(String nombre, String email, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.fechaRegistro = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.activo = true;
    }

    // Strategy pattern
    public void setEstrategiaDescuento(EstrategiaDescuento e) {
        this.estrategia = e;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public double aplicarDescuento(double total) {
        if (this.estrategia != null && this.estrategia.getActiva()) {
            return this.estrategia.calcularTotalConDescuento(total);
        }
        return total;
    }

    // Getters y setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(String preferencias) {
        this.preferencias = preferencias;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public String getBebidaFavorita() {
        return bebidaFavorita;
    }

    public void setBebidaFavorita(String bebidaFavorita) {
        this.bebidaFavorita = bebidaFavorita;
    }

    public String getPlatoFavorito() {
        return platoFavorito;
    }

    public void setPlatoFavorito(String platoFavorito) {
        this.platoFavorito = platoFavorito;
    }

    public EstrategiaDescuento getEstrategia() {
        return estrategia;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
