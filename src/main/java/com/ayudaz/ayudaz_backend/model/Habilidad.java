package com.ayudaz.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "habilidades")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Habilidad {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habilidad")
    private Long idHabilidad;

    @Column(nullable = false, unique = true)
    private String nombre;
}