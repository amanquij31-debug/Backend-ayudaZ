package com.ayudaz.backend.dto;

import lombok.Data;

@Data
public class RegistroRequest {

    private String nombres;
    private String correo;
    private String telefono;
    private String direccion;

    // VOLUNTARIO o USUARIO
    private String rol;
}