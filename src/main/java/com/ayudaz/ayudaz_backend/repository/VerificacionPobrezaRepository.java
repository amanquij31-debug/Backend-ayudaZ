package com.ayudaz.ayudaz_backend.repository;

import com.ayudaz.ayudaz_backend.model.VerificacionPobreza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificacionPobrezaRepository
        extends JpaRepository<VerificacionPobreza, Long> {

    Optional<VerificacionPobreza> findByAyudadoId(Long ayudadoId);

    boolean existsByAyudadoId(Long ayudadoId);

        
}