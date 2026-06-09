package com.ayudaz.ayudaz_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ofertas_ayuda",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"solicitud_id", "voluntario_id"})
        }
)
public class OfertaAyuda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "solicitud_id", nullable = false)
    @JsonIgnoreProperties({"ofertas", "ayudado"})
    private Solicitud solicitud;

    @ManyToOne
    @JoinColumn(name = "voluntario_id", nullable = false)
    @JsonIgnoreProperties({
            "password",
            "solicitudes",
            "hibernateLazyInitializer",
            "handler"
    })
    private Usuario voluntario;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Enumerated(EnumType.STRING)
    private EstadoOferta estado = EstadoOferta.pendiente;

    @Column(name = "fecha_oferta")
    private LocalDateTime fechaOferta = LocalDateTime.now();

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Solicitud getSolicitud() {
        return solicitud;
    }

    public void setSolicitud(Solicitud solicitud) {
        this.solicitud = solicitud;
    }

    public Usuario getVoluntario() {
        return voluntario;
    }

    public void setVoluntario(Usuario voluntario) {
        this.voluntario = voluntario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public EstadoOferta getEstado() {
        return estado;
    }

    public void setEstado(EstadoOferta estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaOferta() {
        return fechaOferta;
    }

    public void setFechaOferta(LocalDateTime fechaOferta) {
        this.fechaOferta = fechaOferta;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }
}