package com.ayudaz.ayudaz_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ranking_mensual")
public class RankingMensual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "voluntario_id")
    private Usuario voluntario;   // ← objeto Usuario, no Long

    private Integer mes;
    private Integer anio;
    private Integer ayudasCompletadas = 0;
    private Integer puntos = 0;

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getVoluntario() { return voluntario; }
    public void setVoluntario(Usuario voluntario) { this.voluntario = voluntario; }
    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
    public Integer getAyudasCompletadas() { return ayudasCompletadas; }
    public void setAyudasCompletadas(Integer ayudasCompletadas) { this.ayudasCompletadas = ayudasCompletadas; }
    public Integer getPuntos() { return puntos; }
    public void setPuntos(Integer puntos) { this.puntos = puntos; }
}