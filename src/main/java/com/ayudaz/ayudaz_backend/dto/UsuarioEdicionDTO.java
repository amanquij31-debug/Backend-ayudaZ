package com.ayudaz.ayudaz_backend.dto;

public class UsuarioEdicionDTO {
    private String nombre;
    private String email;
    private String tipoUsuario;   // "voluntario", "ayudado", "admin"
    private String estado;        // "activo", "pendiente", "rechazado"

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}