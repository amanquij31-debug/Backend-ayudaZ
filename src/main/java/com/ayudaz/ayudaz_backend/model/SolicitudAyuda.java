package com.ayudaz.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_ayuda")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudAyuda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long idSolicitud;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnoreProperties({
            "password",
            "roles",
            "hibernateLazyInitializer",
            "handler"
    })
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private CategoriaAyuda categoria;

    @ManyToOne
    @JoinColumn(name = "id_urgencia")
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private Urgencia urgencia;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    @JsonIgnoreProperties({
            "hibernateLazyInitializer",
            "handler"
    })
    private Estado estado;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 255)
    private String ubicacion;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @PrePersist
    public void prePersist() {

        if (this.fechaSolicitud == null) {
            this.fechaSolicitud = LocalDateTime.now();
        }

    }
}