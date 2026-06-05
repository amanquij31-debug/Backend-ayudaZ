package com.ayudaz.ayudaz_backend.controller;

import com.ayudaz.ayudaz_backend.model.RankingMensual;
import com.ayudaz.ayudaz_backend.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ranking")
@CrossOrigin(origins = "*")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    @GetMapping("/mensual")
    public ResponseEntity<?> getRankingMensual() {
        List<RankingMensual> ranking = rankingService.getRankingMensual();

        // Mapear a DTO simple para el frontend
        List<Map<String, Object>> resultado = ranking.stream()
                .map(r -> Map.<String, Object>of(
                        "id",       r.getId(),
                        "voluntario", Map.of(
                                "id",     r.getVoluntario().getId(),
                                "nombre", r.getVoluntario().getNombre() != null
                                        ? r.getVoluntario().getNombre() : "Voluntario"
                        ),
                        "ayudasCompletadas", r.getAyudasCompletadas() != null ? r.getAyudasCompletadas() : 0,
                        "puntos", r.getPuntos() != null ? r.getPuntos() : 0,
                        "mes",   r.getMes(),
                        "anio",  r.getAnio()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }
}