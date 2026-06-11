package com.ayudaz.ayudaz_backend.dto;

// DTO para la solicitud
public class SolicitudRequestDTO {
    private String titulo;
    private String descripcion;
    private String categoria;
    private String ubicacion;
    private Integer urgencia;
    private Long ayudadoId;  // ← campo adicional

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Integer getUrgencia() {
        return urgencia;
    }

    public void setUrgencia(Integer urgencia) {
        this.urgencia = urgencia;
    }

    public Long getAyudadoId() {
        return ayudadoId;
    }

    public void setAyudadoId(Long ayudadoId) {
        this.ayudadoId = ayudadoId;
    }
}