package com.ayudaz.ayudaz_backend.controller;

import com.ayudaz.ayudaz_backend.dto.UsuarioEdicionDTO;
import com.ayudaz.ayudaz_backend.dto.VerificacionDTO;
import com.ayudaz.ayudaz_backend.model.Ayudado;
import com.ayudaz.ayudaz_backend.model.Solicitud;
import com.ayudaz.ayudaz_backend.model.Usuario;
import com.ayudaz.ayudaz_backend.model.VerificacionPobreza;
import com.ayudaz.ayudaz_backend.repository.AyudadoRepository;
import com.ayudaz.ayudaz_backend.repository.VerificacionPobrezaRepository;
import com.ayudaz.ayudaz_backend.service.SolicitudService;
import com.ayudaz.ayudaz_backend.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private VerificacionPobrezaRepository verificacionRepository;

    /**
     * Obtener todos los usuarios pendientes de aprobación.
     */
    @GetMapping("/pendientes")
    public ResponseEntity<?> getPendientes(HttpServletRequest request) {
        String tipo = (String) request.getAttribute("usuarioTipo");
        if (!"admin".equals(tipo)) {
            return ResponseEntity.status(403).body("Acceso solo para administradores");
        }
        List<Usuario> pendientes = usuarioService.getUsuariosPendientes();
        return ResponseEntity.ok(pendientes);
    }

    /**
     * Aprobar un usuario (cambia estado a activo).
     */
    @PostMapping("/aprobar/{usuarioId}")
    public ResponseEntity<?> aprobarUsuario(
            @PathVariable Long usuarioId,
            HttpServletRequest request) {

        String tipo = (String) request.getAttribute("usuarioTipo");

        if (!"admin".equals(tipo)) {
            return ResponseEntity.status(403)
                    .body("Acceso solo para administradores");
        }

        try {
            Optional<Ayudado> ayudadoOpt =
                    ayudadoRepository.findByUsuarioId(usuarioId);
            if (ayudadoOpt.isPresent()) {
                Ayudado ayudado = ayudadoOpt.get();
                if (!verificacionRepository.existsByAyudadoId(ayudado.getId())) {
                    return ResponseEntity.badRequest().body(
                            "Debe registrar primero la constancia SISFOH"
                    );
                }
            }
            usuarioService.aprobarUsuario(usuarioId);
            return ResponseEntity.ok("Usuario aprobado exitosamente");

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());

        }
    }

    /**
     * Rechazar/eliminar un usuario.
     */
    @DeleteMapping("/eliminar/{usuarioId}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long usuarioId, HttpServletRequest request) {
        String tipo = (String) request.getAttribute("usuarioTipo");
        if (!"admin".equals(tipo)) {
            return ResponseEntity.status(403).body("Acceso solo para administradores");
        }
        try {
            usuarioService.eliminarUsuario(usuarioId);
            return ResponseEntity.ok("Usuario eliminado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtener todas las solicitudes (para el admin).
     */
    @GetMapping("/solicitudes")
    public ResponseEntity<?> getAllSolicitudes(HttpServletRequest request) {
        String tipo = (String) request.getAttribute("usuarioTipo");
        if (!"admin".equals(tipo)) {
            return ResponseEntity.status(403).body("Acceso solo para administradores");
        }
        List<Solicitud> solicitudes = solicitudService.getAllSolicitudes();
        return ResponseEntity.ok(solicitudes);
    }

    /**
     * (Opcional) Ver todos los usuarios registrados.
     */
    @GetMapping("/usuarios")
    public ResponseEntity<?> getAllUsuarios(HttpServletRequest request) {
        String tipo = (String) request.getAttribute("usuarioTipo");
        if (!"admin".equals(tipo)) {
            return ResponseEntity.status(403).body("Acceso solo para administradores");
        }
        return ResponseEntity.ok(usuarioService.findAll()); // Necesitas agregar findAll() en UsuarioService
    }

    @Autowired
    private AyudadoRepository ayudadoRepository;

    @GetMapping("/ayudados/usuario/{usuarioId}")
    public ResponseEntity<?> getAyudadoByUsuarioId(@PathVariable Long usuarioId) {
        Optional<Ayudado> ayudado = ayudadoRepository.findByUsuarioId(usuarioId);
        return ayudado.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Suspender un usuario (cambiar estado a rechazado).
     */
    @PostMapping("/suspender/{usuarioId}")
    public ResponseEntity<?> suspenderUsuario(@PathVariable Long usuarioId, HttpServletRequest request) {
        String tipo = (String) request.getAttribute("usuarioTipo");
        if (!"admin".equals(tipo)) {
            return ResponseEntity.status(403).body("Acceso solo para administradores");
        }
        try {
            usuarioService.suspenderUsuario(usuarioId);
            return ResponseEntity.ok("Usuario suspendido (rechazado)");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Editar usuario.
     * Se espera un JSON con los campos a modificar (todos opcionales).
     */
    @PutMapping("/usuarios/{usuarioId}")
    public ResponseEntity<?> editarUsuario(@PathVariable Long usuarioId,
                                           @RequestBody UsuarioEdicionDTO dto,
                                           HttpServletRequest request) {
        String tipo = (String) request.getAttribute("usuarioTipo");
        if (!"admin".equals(tipo)) {
            return ResponseEntity.status(403).body("Acceso solo para administradores");
        }
        try {
            Usuario actualizado = usuarioService.editarUsuario(
                    usuarioId,
                    dto.getNombre(),
                    dto.getEmail(),
                    dto.getTipoUsuario(),
                    dto.getEstado()
            );
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verificacion-pobreza/{usuarioId}")
    public ResponseEntity<?> registrarVerificacion(
            @PathVariable Long usuarioId,
            @RequestParam("imagen") MultipartFile imagen,
            @RequestParam("nivel") String nivel,
            @RequestParam(required = false) String observaciones,
            HttpServletRequest request) {

        try {

            // Validar imagen
            if (imagen == null || imagen.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Debe adjuntar una imagen");
            }

            // Validar nivel
            if (!nivel.equals("POBRE") && !nivel.equals("EXTREMA_POBREZA")) {
                return ResponseEntity.badRequest()
                        .body("Nivel inválido");
            }

            // Buscar ayudado
            Ayudado ayudado = ayudadoRepository.findByUsuarioId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Ayudado no encontrado"));

            // Obtener admin autenticado
            Long adminId = (Long) request.getAttribute("usuarioId");

            Usuario admin = usuarioService.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

            // Si ya existe una verificación, actualizar
            VerificacionPobreza verificacion = verificacionRepository
                    .findByAyudadoId(ayudado.getId())
                    .orElse(new VerificacionPobreza());

            verificacion.setAyudado(ayudado);
            verificacion.setAdmin(admin);
            verificacion.setNivel(nivel);
            verificacion.setFechaVerificacion(LocalDateTime.now());
            verificacion.setObservaciones(observaciones);

            verificacion.setImagen(imagen.getBytes());
            verificacion.setImagenNombre(imagen.getOriginalFilename());
            verificacion.setImagenTipo(imagen.getContentType());

            verificacionRepository.save(verificacion);

            return ResponseEntity.ok("Verificación registrada correctamente");

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body("Error: " + e.getMessage());

        }
    }

    @GetMapping("/verificacion-pobreza/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerVerificacion(
            @PathVariable Long usuarioId) {

        try {

            Ayudado ayudado = ayudadoRepository
                    .findByUsuarioId(usuarioId)
                    .orElseThrow(() ->
                            new RuntimeException("Ayudado no encontrado"));

            VerificacionPobreza verificacion = verificacionRepository
                    .findByAyudadoId(ayudado.getId())
                    .orElseThrow(() ->
                            new RuntimeException("No existe verificación"));

            VerificacionDTO dto = new VerificacionDTO();

            dto.setId(verificacion.getId());
            dto.setNivel(verificacion.getNivel());
            dto.setObservaciones(verificacion.getObservaciones());
            dto.setFechaVerificacion(verificacion.getFechaVerificacion());
            dto.setImagenNombre(verificacion.getImagenNombre());
            dto.setImagenTipo(verificacion.getImagenTipo());

            return ResponseEntity.ok(dto);

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());

        }
    }

    @GetMapping("/verificacion-pobreza/imagen/{usuarioId}")
    public ResponseEntity<byte[]> obtenerImagenVerificacion(
            @PathVariable Long usuarioId) {

        Ayudado ayudado = ayudadoRepository
                .findByUsuarioId(usuarioId)
                .orElseThrow(() ->
                        new RuntimeException("Ayudado no encontrado"));

        VerificacionPobreza verificacion = verificacionRepository
                .findByAyudadoId(ayudado.getId())
                .orElseThrow(() ->
                        new RuntimeException("No existe verificación"));

        return ResponseEntity.ok()
                .header("Content-Type", verificacion.getImagenTipo())
                .header(
                        "Content-Disposition",
                        "inline; filename=\"" +
                                verificacion.getImagenNombre() + "\""
                )
                .body(verificacion.getImagen());
    }
}
