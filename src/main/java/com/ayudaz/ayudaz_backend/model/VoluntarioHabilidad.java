package com.ayudaz.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "voluntario_habilidades")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class VoluntarioHabilidad {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_voluntario", nullable = false)
    private Voluntario voluntario;

    @ManyToOne
    @JoinColumn(name = "id_habilidad", nullable = false)
    private Habilidad habilidad;
}