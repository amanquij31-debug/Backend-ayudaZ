package com.ayudaz.ayudaz_backend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "verificacion_pobreza")
public class VerificacionPobreza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "ayudado_id")
    private Ayudado ayudado;

    @Column(name = "nivel", nullable = false)
    private String nivel;

    @ManyToOne
    @JoinColumn(name = "id_admin", nullable = false)
    private Usuario admin;

    @Column(name = "fecha_verificacion")
    private LocalDateTime fechaVerificacion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "imagen")
    private byte[] imagen;

    @Column(name = "imagen_nombre")
    private String imagenNombre;

    @Column(name = "imagen_tipo")
    private String imagenTipo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ayudado getAyudado() {
        return ayudado;
    }

    public void setAyudado(Ayudado ayudado) {
        this.ayudado = ayudado;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public LocalDateTime getFechaVerificacion() {
        return fechaVerificacion;
    }

    public void setFechaVerificacion(LocalDateTime fechaVerificacion) {
        this.fechaVerificacion = fechaVerificacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Usuario getAdmin() {
        return admin;
    }

    public void setAdmin(Usuario admin) {
        this.admin = admin;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getImagenNombre() {
        return imagenNombre;
    }

    public void setImagenNombre(String imagenNombre) {
        this.imagenNombre = imagenNombre;
    }

    public String getImagenTipo() {
        return imagenTipo;
    }

    public void setImagenTipo(String imagenTipo) {
        this.imagenTipo = imagenTipo;
    }
}