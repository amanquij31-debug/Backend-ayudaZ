package com.ayudaz.ayudaz_backend.controller;

import com.ayudaz.ayudaz_backend.dto.UsuarioEdicionDTO;
import com.ayudaz.ayudaz_backend.dto.VerificacionDTO;
import com.ayudaz.ayudaz_backend.model.*;
import com.ayudaz.ayudaz_backend.repository.AyudadoRepository;
import com.ayudaz.ayudaz_backend.repository.VerificacionPobrezaRepository;
import com.ayudaz.ayudaz_backend.service.LogsAdminService;
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

    @Autowired private UsuarioService usuarioService;
    @Autowired private SolicitudService solicitudService;
    @Autowired private AyudadoRepository ayudadoRepository;
    @Autowired private VerificacionPobrezaRepository verificacionRepository;
    @Autowired private LogsAdminService logsAdminService;  // ← nuevo

    // ── Helper: obtener el usuario admin del request ──────────────────────────
    private Usuario getAdmin(HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("usuarioId");
        if (adminId == null) return null;
        return usuarioService.findById(adminId).orElse(null);
    }

    // ── Usuarios pendientes ───────────────────────────────────────────────────
    @GetMapping("/pendientes")
    public ResponseEntity<?> getPendientes(HttpServletRequest request) {
        String tipo = (String) request.getAttribute("usuarioTipo");
        if (!"admin".equals(tipo))
            return ResponseEntity.status(403).body("Acceso solo para administradores");
        return ResponseEntity.ok(usuarioService.getUsuariosPendientes());
    }

    // ── APROBAR usuario ───────────────────────────────────────────────────────
    @PostMapping("/aprobar/{usuarioId}")
    public ResponseEntity<?> aprobarUsuario(@PathVariable Long usuarioId,
                                            HttpServletRequest request) {
        String tipo = (String) request.getAttribute("usuarioTipo");
        if (!"admin".equals(tipo))
            return ResponseEntity.status(403).body("Acceso solo para administradores");

        try {
            // Validar verificación SISFOH si es ayudado
            Optional<Ayudado> ayudadoOpt = ayudadoRepository.findByUsuarioId(usuarioId);
            if (ayudadoOpt.isPresent()) {
                if (!verificacionRepository.existsByAyudadoId(ayudadoOpt.get().getId()))
                    return ResponseEntity.badRequest().body("Debe registrar primero la constancia SISFOH");
            }

            Usuario objetivo = usuarioService.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            usuarioService.aprobarUsuario(usuarioId);

            // ── LOG ──────────────────────────────────────────────────────────
            Usuario admin = getAdmin(request);
            if (admin != null) {
                logsAdminService.registrar(
                        admin,
                        "APROBAR_USUARIO",
                        "Aprobó al usuario ID=" + usuarioId
                                + " | nombre=" + objetivo.getNombre()
                                + " | email=" + objetivo.getEmail()
                                + " | tipo=" + objetivo.getTipoUsuario()
                );
            }
            return ResponseEntity.ok("Usuario aprobado exitosamente");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── ELIMINAR / RECHAZAR usuario ───────────────────────────────────────────
    @DeleteMapping("/eliminar/{usuarioId}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long usuarioId,
                                             HttpServletRequest request) {
        String tipo = (String) request.getAttribute("usuarioTipo");
        if (!"admin".equals(tipo))
            return ResponseEntity.status(403).body("Acceso solo para administradores");

        try {
            Usuario objetivo = usuarioService.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Guardar datos antes de eliminar
            String datos = "nombre=" + objetivo.getNombre()
                    + " | email=" + objetivo.getEmail()
                    + " | tipo=" + objetivo.getTipoUsuario();

            // ── LOG ANTES de eliminar — evita error de FK ────────────────────
            Usuario admin = getAdmin(request);
            if (admin != null) {
                logsAdminService.registrar(
                        admin,
                        "ELIMINAR_USUARIO",
                        "Eliminó al usuario ID=" + usuarioId + " | " + datos
                );
            }
            usuarioService.eliminarUsuario(usuarioId);

            return ResponseEntity.ok("Usuario eliminado");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── SUSPENDER usuario ─────────────────────────────────────────────────────
    @PostMapping("/suspender/{usuarioId}")
    public ResponseEntity<?> suspenderUsuario(@PathVariable Long usuarioId,
                                              HttpServletRequest request) {
        String tipo = (String) request.getAttribute("usuarioTipo");
        if (!"admin".equals(tipo))
            return ResponseEntity.status(403).body("Acceso solo para administradores");

        try {
            Usuario objetivo = usuarioService.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            usuarioService.suspenderUsuario(usuarioId);

            // ── LOG ──────────────────────────────────────────────────────────
            Usuario admin = getAdmin(request);
            if (admin != null) {
                logsAdminService.registrar(
                        admin,
                        "SUSPENDER_USUARIO",
                        "Suspendió al usuario ID=" + usuarioId
                                + " | nombre=" + objetivo.getNombre()
                                + " | email=" + objetivo.getEmail()
                );
            }

            return ResponseEntity.ok("Usuario suspendido");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // -- activar usuario --------------------

    @PostMapping("/activar/{usuarioId}")
    public ResponseEntity<?> activarUsuario(@PathVariable Long usuarioId,
                                            HttpServletRequest request) {

        String tipo = (String) request.getAttribute("usuarioTipo");

        if (!"admin".equals(tipo)) {
            return ResponseEntity.status(403).body("Acceso solo para administradores");
        }

        try {

            Usuario objetivo = usuarioService.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            usuarioService.activarUsuario(usuarioId);

            Usuario admin = getAdmin(request);

            if (admin != null) {
                logsAdminService.registrar(
                        admin,
                        "ACTIVAR_USUARIO",
                        "Activó al usuario ID=" + usuarioId
                                + " | nombre=" + objetivo.getNombre()
                                + " | email=" + objetivo.getEmail()
                );
            }

            return ResponseEntity.ok("Usuario activado");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── EDITAR usuario ────────────────────────────────────────────────────────
    @PutMapping("/usuarios/{usuarioId}")
    public ResponseEntity<?> editarUsuario(@PathVariable Long usuarioId,
                                           @RequestBody UsuarioEdicionDTO dto,
                                           HttpServletRequest request) {
        String tipo = (String) request.getAttribute("usuarioTipo");
        if (!"admin".equals(tipo))
            return ResponseEntity.status(403).body("Acceso solo para administradores");

        try {
            Usuario anterior = usuarioService.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String cambios = "nombre: '" + anterior.getNombre() + "' → '" + dto.getNombre() + "'"
                    + " | email: '" + anterior.getEmail() + "' → '" + dto.getEmail() + "'"
                    + " | estado: '" + anterior.getEstado() + "' → '" + dto.getEstado() + "'";

            Usuario actualizado = usuarioService.editarUsuario(
                    usuarioId,
                    dto.getNombre(), dto.getEmail(),
                    dto.getTipoUsuario(), dto.getEstado()
            );

            // ── LOG ──────────────────────────────────────────────────────────
            Usuario admin = getAdmin(request);
            if (admin != null) {
                logsAdminService.registrar(
                        admin,
                        "EDITAR_USUARIO",
                        "Editó usuario ID=" + usuarioId + " | cambios: " + cambios
                );
            }

            return ResponseEntity.ok(actualizado);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── VERIFICACIÓN DE POBREZA ───────────────────────────────────────────────
    @PostMapping("/verificacion-pobreza/{usuarioId}")
    public ResponseEntity<?> registrarVerificacion(
            @PathVariable Long usuarioId,
            @RequestParam("imagen") MultipartFile imagen,
            @RequestParam("nivel") String nivel,
            @RequestParam(required = false) String observaciones,
            HttpServletRequest request) {
        try {
            if (imagen == null || imagen.isEmpty())
                return ResponseEntity.badRequest().body("Debe adjuntar una imagen");
            if (!nivel.equals("POBRE") && !nivel.equals("EXTREMA_POBREZA"))
                return ResponseEntity.badRequest().body("Nivel inválido");

            Ayudado ayudado = ayudadoRepository.findByUsuarioId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Ayudado no encontrado"));

            Long adminId = (Long) request.getAttribute("usuarioId");
            Usuario admin = usuarioService.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

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

            // ── LOG ──────────────────────────────────────────────────────────
            logsAdminService.registrar(
                    admin,
                    "VERIFICACION_POBREZA",
                    "Registró verificación de pobreza para usuarioId=" + usuarioId
                            + " | nivel=" + nivel
                            + " | archivo=" + imagen.getOriginalFilename()
            );

            return ResponseEntity.ok("Verificación registrada correctamente");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ── Ver todos los usuarios ────────────────────────────────────────────────
    @GetMapping("/usuarios")
    public ResponseEntity<?> getAllUsuarios(HttpServletRequest request) {
        String tipo = (String) request.getAttribute("usuarioTipo");
        if (!"admin".equals(tipo))
            return ResponseEntity.status(403).body("Acceso solo para administradores");
        return ResponseEntity.ok(usuarioService.findAll());
    }

    // ── Ver todas las solicitudes ─────────────────────────────────────────────
    @GetMapping("/solicitudes")
    public ResponseEntity<?> getAllSolicitudes(HttpServletRequest request) {
        String tipo = (String) request.getAttribute("usuarioTipo");
        if (!"admin".equals(tipo))
            return ResponseEntity.status(403).body("Acceso solo para administradores");
        return ResponseEntity.ok(solicitudService.getAllSolicitudes());
    }

    // ── Detalle de ayudado por usuarioId ──────────────────────────────────────
    @GetMapping("/ayudados/usuario/{usuarioId}")
    public ResponseEntity<?> getAyudadoByUsuarioId(@PathVariable Long usuarioId) {
        return ayudadoRepository.findByUsuarioId(usuarioId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ── Ver imagen de verificación ────────────────────────────────────────────
    @GetMapping("/verificacion-pobreza/imagen/{usuarioId}")
    public ResponseEntity<byte[]> obtenerImagenVerificacion(@PathVariable Long usuarioId) {
        Ayudado ayudado = ayudadoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Ayudado no encontrado"));
        VerificacionPobreza v = verificacionRepository.findByAyudadoId(ayudado.getId())
                .orElseThrow(() -> new RuntimeException("No existe verificación"));
        return ResponseEntity.ok()
                .header("Content-Type", v.getImagenTipo())
                .header("Content-Disposition", "inline; filename=\"" + v.getImagenNombre() + "\"")
                .body(v.getImagen());
    }

    // ── Ver datos de verificación (sin imagen) ────────────────────────────────
    @GetMapping("/verificacion-pobreza/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerVerificacion(@PathVariable Long usuarioId) {
        try {
            Ayudado ayudado = ayudadoRepository.findByUsuarioId(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Ayudado no encontrado"));
            VerificacionPobreza v = verificacionRepository.findByAyudadoId(ayudado.getId())
                    .orElseThrow(() -> new RuntimeException("No existe verificación"));
            VerificacionDTO dto = new VerificacionDTO();
            dto.setId(v.getId());
            dto.setNivel(v.getNivel());
            dto.setObservaciones(v.getObservaciones());
            dto.setFechaVerificacion(v.getFechaVerificacion());
            dto.setImagenNombre(v.getImagenNombre());
            dto.setImagenTipo(v.getImagenTipo());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── Ver logs ──────────────────────────────────────────────────────────────
    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(HttpServletRequest request) {
        String tipo = (String) request.getAttribute("usuarioTipo");
        if (!"admin".equals(tipo))
            return ResponseEntity.status(403).body("Acceso solo para administradores");
        return ResponseEntity.ok(logsAdminService.getTodos());
    }
}