package com.ayudaz.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "donaciones")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Donacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_donacion")
    private Long idDonacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_tipo_donacion")
    private TipoDonacion tipoDonacion;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "id_solicitud")
    private SolicitudAyuda solicitud;

    private BigDecimal monto;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_donacion")
    private LocalDateTime fechaDonacion = LocalDateTime.now();
}