package com.ayudaz.ayudaz_backend.controller;

import com.ayudaz.ayudaz_backend.dto.SolicitudRequestDTO;
import com.ayudaz.ayudaz_backend.model.EstadoUsuario;
import com.ayudaz.ayudaz_backend.model.Solicitud;
import com.ayudaz.ayudaz_backend.model.Usuario;
import com.ayudaz.ayudaz_backend.service.SolicitudService;
import com.ayudaz.ayudaz_backend.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*")
public class SolicitudController {

    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private UsuarioService usuarioService;

    // ── Solicitudes activas (para voluntarios) ────────────────────────────────
    @GetMapping("/activas")
    public ResponseEntity<?> getActivas(HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");
        if (usuarioId == null)
            return ResponseEntity.status(401).body("Usuario no autenticado");

        Usuario usuario = usuarioService.findById(usuarioId).orElse(null);
        if (usuario != null && usuario.getEstado() != EstadoUsuario.activo)
            return ResponseEntity.status(403).body("Tu cuenta aún no está activa");

        return ResponseEntity.ok(solicitudService.getSolicitudesActivas());
    }

    // ── MIS solicitudes — activas + cerradas del usuario en sesión ────────────
    @GetMapping("/mis-solicitudes")
    public ResponseEntity<?> getMisSolicitudes(HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");
        if (usuarioId == null)
            return ResponseEntity.status(401).body("Usuario no autenticado");

        return ResponseEntity.ok(
                solicitudService.getSolicitudesByAyudado(usuarioId)
        );
    }

    // ── Crear solicitud ───────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> crearSolicitud(@RequestBody SolicitudRequestDTO dto) {
        try {
            Solicitud solicitud = new Solicitud();
            solicitud.setTitulo(dto.getTitulo());
            solicitud.setDescripcion(dto.getDescripcion());
            solicitud.setCategoria(dto.getCategoria());
            solicitud.setUbicacion(dto.getUbicacion());
            solicitud.setUrgencia(dto.getUrgencia());
            solicitud.setLimitePostulantes(5);

            return ResponseEntity.ok(
                    solicitudService.crearSolicitud(solicitud, dto.getAyudadoId())
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── Ofrecer ayuda (voluntario) ────────────────────────────────────────────
    @PostMapping("/{solicitudId}/ofrecer")
    public ResponseEntity<?> ofrecerAyuda(@PathVariable Long solicitudId,
                                          @RequestBody String mensaje,
                                          HttpServletRequest request) {
        Long voluntarioId = (Long) request.getAttribute("usuarioId");
        String tipo       = (String) request.getAttribute("usuarioTipo");

        if (voluntarioId == null || !"voluntario".equals(tipo))
            return ResponseEntity.status(403).body("Solo los voluntarios pueden ofrecer ayuda");

        try {
            return ResponseEntity.ok(
                    solicitudService.ofrecerAyuda(solicitudId, voluntarioId, mensaje)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── Ver ofertas de una solicitud ──────────────────────────────────────────
    @GetMapping("/{solicitudId}/ofertas")
    public ResponseEntity<?> getOfertasBySolicitud(@PathVariable Long solicitudId) {
        return ResponseEntity.ok(
                solicitudService.getOfertasBySolicitud(solicitudId)
        );
    }

    // ── Aceptar oferta ────────────────────────────────────────────────────────
    @PostMapping("/ofertas/{ofertaId}/aceptar")
    public ResponseEntity<?> aceptarOferta(@PathVariable Long ofertaId,
                                           HttpServletRequest request) {
        Long ayudadoId = (Long) request.getAttribute("usuarioId");
        if (ayudadoId == null)
            return ResponseEntity.status(401).body("Usuario no autenticado");

        try {
            solicitudService.aceptarOferta(ofertaId, ayudadoId);
            return ResponseEntity.ok("Oferta aceptada");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── Rechazar oferta ───────────────────────────────────────────────────────
    @PostMapping("/ofertas/{ofertaId}/rechazar")
    public ResponseEntity<?> rechazarOferta(@PathVariable Long ofertaId,
                                            HttpServletRequest request) {
        Long ayudadoId = (Long) request.getAttribute("usuarioId");
        if (ayudadoId == null)
            return ResponseEntity.status(401).body("Usuario no autenticado");

        try {
            solicitudService.rechazarOferta(ofertaId, ayudadoId);
            return ResponseEntity.ok("Oferta rechazada");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── Cerrar solicitud ──────────────────────────────────────────────────────
    @PostMapping("/{solicitudId}/cerrar")
    public ResponseEntity<?> cerrarSolicitud(@PathVariable Long solicitudId,
                                             HttpServletRequest request) {
        Long ayudadoId = (Long) request.getAttribute("usuarioId");
        try {
            solicitudService.cerrarSolicitud(solicitudId, ayudadoId);
            return ResponseEntity.ok("Solicitud cerrada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}