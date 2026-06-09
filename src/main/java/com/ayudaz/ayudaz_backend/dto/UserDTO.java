package com.ayudaz.ayudaz_backend.dto;

public class UserDTO {
    private Long id;
    private String email;
    private String nombre;
    private String tipoUsuario;   // "voluntario", "ayudado", "admin" o null
    private String estado;        // "activo", "pendiente", "rechazado"
    private boolean necesitaRegistro; // true si el usuario no tiene rol asignado

    // Constructor vacío (necesario para Jackson)
    public UserDTO() {}

    // Constructor con todos los campos
    public UserDTO(Long id, String email, String nombre, String tipoUsuario, String estado, boolean necesitaRegistro) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.tipoUsuario = tipoUsuario;
        this.estado = estado;
        this.necesitaRegistro = necesitaRegistro;
    }

    // Getters y Setters
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

    public boolean isNecesitaRegistro() {
        return necesitaRegistro;
    }

    public void setNecesitaRegistro(boolean necesitaRegistro) {
        this.necesitaRegistro = necesitaRegistro;
    }
}