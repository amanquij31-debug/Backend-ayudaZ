package com.ayudaz.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "tipos_donacion")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TipoDonacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_donacion")
    private Long idTipoDonacion;

    @Column(nullable = false, unique = true)
    private String nombre;
}