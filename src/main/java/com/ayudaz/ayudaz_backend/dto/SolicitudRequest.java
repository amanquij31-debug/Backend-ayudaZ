package com.ayudaz.backend.dto;

import lombok.Data;

@Data
public class SolicitudRequest {

    private Long idUsuario;
    private Long idCategoria;
    private Long idUrgencia;
    private Long idEstado;

    private String titulo;
    private String descripcion;
    private String ubicacion;
}