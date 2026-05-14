package com.ayudaz.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "voluntarios")

@Getter
@Setter

public class Voluntario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id_voluntario")
    private Long idVoluntario;

    @OneToOne
    @JoinColumn(name = "id_usuario")

    @JsonBackReference
    private Usuario usuario;

    @Column(name = "disponibilidad")
    private String disponibilidad;

    @Column(name = "zona_apoyo")
    private String zonaApoyo;
}