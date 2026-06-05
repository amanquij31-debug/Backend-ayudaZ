package com.ayudaz.ayudaz_backend.repository;

import com.ayudaz.ayudaz_backend.model.RankingMensual;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RankingMensualRepository
        extends JpaRepository<RankingMensual, Long> {

    Optional<RankingMensual>
    findByVoluntarioIdAndMesAndAnio(
            Long voluntarioId,
            Integer mes,
            Integer anio
    );

    List<RankingMensual>
    findTop10ByMesAndAnioOrderByAyudasCompletadasDesc(
            Integer mes,
            Integer anio
    );
}