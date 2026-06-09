package com.ayudaz.ayudaz_backend.dto;

public class LoginResponseDTO {
    private Long id;
    private String email;
    private String nombre;
    private String tipoUsuario;
    private String estado;

    public LoginResponseDTO(Long id, String email, String nombre, String tipoUsuario, String estado) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.tipoUsuario = tipoUsuario;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}