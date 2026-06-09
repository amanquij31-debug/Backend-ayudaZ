package com.ayudaz.ayudaz_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ayudados")
public class Ayudado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "dni", length = 8)
    private String dni;

    @Column(name = "dni_nombre")
    private String dniNombre;

    @Column(name = "nombre_conyuge")
    private String nombreConyuge;

    @Column(name = "fecha_nacimiento_conyuge")
    private LocalDate fechaNacimientoConyuge;

    @Column(name = "lugar_nacimiento_conyuge")
    private String lugarNacimientoConyuge;

    @Column(name = "cantidad_integrantes")
    private Integer cantidadIntegrantes;

    @Column(name = "confirmacion_admin")
    private Boolean confirmacionAdmin = false;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "estado_validacion")
    private String estadoValidacion = "PENDIENTE";

    @Column(name = "situacion_economica", columnDefinition = "TEXT")
    private String situacionEconomica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin_validador")
    private Usuario adminValidador;

    // ==========================
    // GETTERS Y SETTERS
    // ==========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getDniNombre() {
        return dniNombre;
    }

    public void setDniNombre(String dniNombre) {
        this.dniNombre = dniNombre;
    }

    public String getNombreConyuge() {
        return nombreConyuge;
    }

    public void setNombreConyuge(String nombreConyuge) {
        this.nombreConyuge = nombreConyuge;
    }

    public LocalDate getFechaNacimientoConyuge() {
        return fechaNacimientoConyuge;
    }

    public void setFechaNacimientoConyuge(LocalDate fechaNacimientoConyuge) {
        this.fechaNacimientoConyuge = fechaNacimientoConyuge;
    }

    public String getLugarNacimientoConyuge() {
        return lugarNacimientoConyuge;
    }

    public void setLugarNacimientoConyuge(String lugarNacimientoConyuge) {
        this.lugarNacimientoConyuge = lugarNacimientoConyuge;
    }

    public Integer getCantidadIntegrantes() {
        return cantidadIntegrantes;
    }

    public void setCantidadIntegrantes(Integer cantidadIntegrantes) {
        this.cantidadIntegrantes = cantidadIntegrantes;
    }

    public Boolean getConfirmacionAdmin() {
        return confirmacionAdmin;
    }

    public void setConfirmacionAdmin(Boolean confirmacionAdmin) {
        this.confirmacionAdmin = confirmacionAdmin;
    }

    public LocalDateTime getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) {
        this.fechaConfirmacion = fechaConfirmacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstadoValidacion() {
        return estadoValidacion;
    }

    public void setEstadoValidacion(String estadoValidacion) {
        this.estadoValidacion = estadoValidacion;
    }

    public String getSituacionEconomica() {
        return situacionEconomica;
    }

    public void setSituacionEconomica(String situacionEconomica) {
        this.situacionEconomica = situacionEconomica;
    }

    public Usuario getAdminValidador() {
        return adminValidador;
    }

    public void setAdminValidador(Usuario adminValidador) {
        this.adminValidador = adminValidador;
    }
}