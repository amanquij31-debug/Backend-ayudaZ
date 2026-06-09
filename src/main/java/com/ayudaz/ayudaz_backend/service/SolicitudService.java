package com.ayudaz.ayudaz_backend.service;

import com.ayudaz.ayudaz_backend.model.*;
import com.ayudaz.ayudaz_backend.repository.OfertaAyudaRepository;
import com.ayudaz.ayudaz_backend.repository.SolicitudRepository;
import com.ayudaz.ayudaz_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudService {

    @Autowired private UsuarioService usuarioService;
    @Autowired private SolicitudRepository solicitudRepository;
    @Autowired private OfertaAyudaRepository ofertaAyudaRepository;
    @Autowired private RankingService rankingService;
    @Autowired private UsuarioRepository usuarioRepository;

    // ─────────────────────────────────────────────
    // CREAR SOLICITUD
    // ─────────────────────────────────────────────
    @Transactional
    public Solicitud crearSolicitud(Solicitud solicitud, Long ayudadoId) {

        long activas = solicitudRepository.countByAyudadoIdAndEstado(
                ayudadoId,
                EstadoSolicitud.activa
        );

        if (activas >= 3) {
            throw new RuntimeException("Has alcanzado el límite de 3 solicitudes activas.");
        }

        Usuario ayudado = usuarioService.findById(ayudadoId)
                .orElseThrow(() -> new RuntimeException("Usuario ayudado no encontrado"));

        solicitud.setAyudado(ayudado);
        solicitud.setEstado(EstadoSolicitud.activa);
        solicitud.setFechaCreacion(LocalDateTime.now());

        return solicitudRepository.save(solicitud);
    }

    // ─────────────────────────────────────────────
    // LISTAR ACTIVAS
    // ─────────────────────────────────────────────
    public List<Solicitud> getSolicitudesActivas() {
        return solicitudRepository.findByEstado(EstadoSolicitud.activa);
    }

    public List<Solicitud> getAllSolicitudes() {
        return solicitudRepository.findAll();
    }

    public List<Solicitud> getSolicitudesByAyudado(Long ayudadoId) {
        return solicitudRepository.findByAyudadoIdOrderByFechaCreacionDesc(ayudadoId);
    }

    // ─────────────────────────────────────────────
    // OFRECER AYUDA
    // ─────────────────────────────────────────────
    @Transactional
    public OfertaAyuda ofrecerAyuda(Long solicitudId, Long voluntarioId, String mensaje) {

        if (ofertaAyudaRepository.existsBySolicitudIdAndVoluntarioId(solicitudId, voluntarioId)) {
            throw new RuntimeException("Ya ofreciste ayuda en esta solicitud.");
        }

        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (solicitud.getEstado() != EstadoSolicitud.activa) {
            throw new RuntimeException("La solicitud no acepta ofertas.");
        }

        Usuario voluntario = usuarioService.findById(voluntarioId)
                .orElseThrow(() -> new RuntimeException("Voluntario no encontrado"));

        OfertaAyuda oferta = new OfertaAyuda();
        oferta.setSolicitud(solicitud);
        oferta.setVoluntario(voluntario);
        oferta.setMensaje(mensaje);
        oferta.setEstado(EstadoOferta.pendiente);
        oferta.setFechaOferta(LocalDateTime.now());

        return ofertaAyudaRepository.save(oferta);
    }

    // ─────────────────────────────────────────────
    // VER OFERTAS
    // ─────────────────────────────────────────────
    public List<OfertaAyuda> getOfertasBySolicitud(Long solicitudId) {
        return ofertaAyudaRepository.findBySolicitudId(solicitudId);
    }

    // ─────────────────────────────────────────────
    // ACEPTAR OFERTA (CIERRA TODO)
    // ─────────────────────────────────────────────
    @Transactional
    public void aceptarOferta(Long ofertaId, Long ayudadoId) {

        OfertaAyuda oferta = ofertaAyudaRepository.findById(ofertaId)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

        Solicitud solicitud = oferta.getSolicitud();

        if (!solicitud.getAyudado().getId().equals(ayudadoId)) {
            throw new RuntimeException("No autorizado");
        }

        if (oferta.getEstado() != EstadoOferta.pendiente) {
            throw new RuntimeException("Oferta ya procesada");
        }

        if (solicitud.getEstado() != EstadoSolicitud.activa) {
            throw new RuntimeException("Solicitud no válida");
        }

        // cerrar oferta aceptada
        oferta.setEstado(EstadoOferta.completada);
        oferta.setFechaRespuesta(LocalDateTime.now());
        ofertaAyudaRepository.save(oferta);

        // cerrar solicitud
        solicitud.setEstado(EstadoSolicitud.cerrada);
        solicitud.setFechaCierre(LocalDateTime.now());
        solicitudRepository.save(solicitud);

        // rechazar otras ofertas
        ofertaAyudaRepository.findBySolicitudId(solicitud.getId())
                .stream()
                .filter(o -> !o.getId().equals(ofertaId) && o.getEstado() == EstadoOferta.pendiente)
                .forEach(o -> {
                    o.setEstado(EstadoOferta.rechazada);
                    o.setFechaRespuesta(LocalDateTime.now());
                    ofertaAyudaRepository.save(o);
                });

        // sumar puntos
        Usuario voluntario = oferta.getVoluntario();
        voluntario.setTotalAyudas(
                voluntario.getTotalAyudas() == null ? 1 : voluntario.getTotalAyudas() + 1
        );

        usuarioService.save(voluntario);
        rankingService.sumarPuntos(voluntario.getId(), 1);
    }

    // ─────────────────────────────────────────────
    // RECHAZAR OFERTA
    // ─────────────────────────────────────────────
    @Transactional
    public void rechazarOferta(Long ofertaId, Long ayudadoId) {

        OfertaAyuda oferta = ofertaAyudaRepository.findById(ofertaId)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

        if (!oferta.getSolicitud().getAyudado().getId().equals(ayudadoId)) {
            throw new RuntimeException("No autorizado");
        }

        if (oferta.getEstado() != EstadoOferta.pendiente) {
            throw new RuntimeException("Oferta ya procesada");
        }

        oferta.setEstado(EstadoOferta.rechazada);
        oferta.setFechaRespuesta(LocalDateTime.now());
        ofertaAyudaRepository.save(oferta);
    }

    // ─────────────────────────────────────────────
    // CERRAR SOLICITUD (FALLBACK)
    // ─────────────────────────────────────────────
    @Transactional
    public void cerrarSolicitud(Long solicitudId, Long ayudadoId) {

        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!solicitud.getAyudado().getId().equals(ayudadoId)) {
            throw new RuntimeException("No autorizado");
        }

        solicitud.setEstado(EstadoSolicitud.cerrada);
        solicitud.setFechaCierre(LocalDateTime.now());
        solicitudRepository.save(solicitud);
    }
}