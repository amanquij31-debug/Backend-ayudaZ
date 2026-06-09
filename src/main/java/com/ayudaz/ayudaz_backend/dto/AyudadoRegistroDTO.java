package com.ayudaz.ayudaz_backend.dto;

import java.time.LocalDate;

public class AyudadoRegistroDTO {

    private String email;
    private String nombre;
    private String dni;                        // ← nuevo — 8 dígitos, validado en frontend
    private String nombreConyuge;
    private LocalDate fechaNacimientoConyuge;
    private String lugarNacimientoConyuge;
    private Integer cantidadIntegrantes;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombreConyuge() { return nombreConyuge; }
    public void setNombreConyuge(String nombreConyuge) { this.nombreConyuge = nombreConyuge; }

    public LocalDate getFechaNacimientoConyuge() { return fechaNacimientoConyuge; }
    public void setFechaNacimientoConyuge(LocalDate f) { this.fechaNacimientoConyuge = f; }

    public String getLugarNacimientoConyuge() { return lugarNacimientoConyuge; }
    public void setLugarNacimientoConyuge(String l) { this.lugarNacimientoConyuge = l; }

    public Integer getCantidadIntegrantes() { return cantidadIntegrantes; }
    public void setCantidadIntegrantes(Integer c) { this.cantidadIntegrantes = c; }
}