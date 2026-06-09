package com.ayudaz.ayudaz_backend.repository;

import com.ayudaz.ayudaz_backend.model.EstadoSolicitud;
import com.ayudaz.ayudaz_backend.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    // Solicitudes activas (para voluntarios)
    List<Solicitud> findByEstado(EstadoSolicitud estado);

    // Contar activas de un ayudado (límite de 3)
    long countByAyudadoIdAndEstado(Long ayudadoId, EstadoSolicitud estado);

    // NUEVO: todas las solicitudes de un ayudado, ordenadas por fecha desc
    // Devuelve activas + en_proceso + cerradas + canceladas
    List<Solicitud> findByAyudadoIdOrderByFechaCreacionDesc(Long ayudadoId);
}