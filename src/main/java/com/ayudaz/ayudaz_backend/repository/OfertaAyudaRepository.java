package com.ayudaz.ayudaz_backend.repository;

import com.ayudaz.ayudaz_backend.model.EstadoOferta;
import com.ayudaz.ayudaz_backend.model.OfertaAyuda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfertaAyudaRepository extends JpaRepository<OfertaAyuda, Long> {

    boolean existsBySolicitudIdAndVoluntarioId(
            Long solicitudId,
            Long voluntarioId
    );

    List<OfertaAyuda> findBySolicitudId(Long solicitudId);

    Optional<OfertaAyuda> findBySolicitudIdAndEstado(
            Long solicitudId,
            EstadoOferta estado
    );
}