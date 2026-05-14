package com.ayudaz.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "categorias_ayuda")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CategoriaAyuda {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @Column(name = "nombre_categoria", nullable = false, unique = true)
    private String nombreCategoria;

    private String descripcion;
}
