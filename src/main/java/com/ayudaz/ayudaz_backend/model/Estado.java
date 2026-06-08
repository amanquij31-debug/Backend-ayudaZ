package com.ayudaz.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "estados")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Estado {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Long idEstado;

    @Column(name = "tipo_tabla", nullable = false)
    private String tipoTabla; // "solicitud" o "donacion"

    @Column(name = "nombre_estado", nullable = false)
    private String nombreEstado;
}