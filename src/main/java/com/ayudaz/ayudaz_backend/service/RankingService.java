package com.ayudaz.ayudaz_backend.service;

import com.ayudaz.ayudaz_backend.model.RankingMensual;
import com.ayudaz.ayudaz_backend.model.Usuario;
import com.ayudaz.ayudaz_backend.repository.RankingMensualRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RankingService {

    @Autowired
    private RankingMensualRepository rankingRepository;

    @Autowired
    private UsuarioService usuarioService;

    public List<RankingMensual> getRankingMensual() {
        LocalDate now = LocalDate.now();
        return rankingRepository.findTop10ByMesAndAnioOrderByAyudasCompletadasDesc(
                now.getMonthValue(), now.getYear()
        );
    }

    @Transactional
    public void sumarPuntos(Long voluntarioId, int cantidad) {
        LocalDate now = LocalDate.now();
        int mes  = now.getMonthValue();
        int anio = now.getYear();

        RankingMensual ranking = rankingRepository
                .findByVoluntarioIdAndMesAndAnio(voluntarioId, mes, anio)
                .orElseGet(() -> {
                    Usuario voluntario = usuarioService.findById(voluntarioId)
                            .orElseThrow(() -> new RuntimeException("Voluntario no encontrado"));
                    RankingMensual nuevo = new RankingMensual();
                    nuevo.setVoluntario(voluntario);
                    nuevo.setMes(mes);
                    nuevo.setAnio(anio);
                    nuevo.setAyudasCompletadas(0);  // campo real del modelo
                    nuevo.setPuntos(0);              // campo real del modelo
                    return nuevo;
                });

        // +1 ayuda completada, +1 punto (ajusta los puntos si quieres otra lógica)
        ranking.setAyudasCompletadas(ranking.getAyudasCompletadas() + cantidad);
        ranking.setPuntos(ranking.getPuntos() + cantidad);
        rankingRepository.save(ranking);
    }
}