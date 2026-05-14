package com.ayudaz.backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usuario_roles")

@Getter
@Setter

public class UsuarioRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")

    @JsonBackReference
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;
}