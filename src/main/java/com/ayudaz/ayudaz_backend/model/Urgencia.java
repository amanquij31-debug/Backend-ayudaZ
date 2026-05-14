package com.ayudaz.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "urgencias")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Urgencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_urgencia")
    private Long idUrgencia;

    @Column(nullable = false, unique = true)
    private String nombre;
}