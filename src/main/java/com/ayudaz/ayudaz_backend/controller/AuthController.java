package com.ayudaz.ayudaz_backend.controller;

import com.ayudaz.ayudaz_backend.dto.AyudadoRegistroDTO;
import com.ayudaz.ayudaz_backend.dto.RegistroDTO;
import com.ayudaz.ayudaz_backend.model.*;
import com.ayudaz.ayudaz_backend.repository.AyudadoRepository;
import com.ayudaz.ayudaz_backend.service.UsuarioService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AyudadoRepository ayudadoRepository;

    // ── /verify ───────────────────────────────────────────────────────────────
    // Crea el usuario en BD si no existe aún (login social / primera vez)
    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);

            String uid    = decoded.getUid();
            String email  = decoded.getEmail();
            String nombre = decoded.getName() != null
                    ? decoded.getName()
                    : email.split("@")[0];

            // findOrCreate — nunca hace doble INSERT
            Usuario usuario = usuarioService.findByFirebaseUid(uid).orElseGet(() -> {
                Usuario u = new Usuario();
                u.setFirebaseUid(uid);
                u.setEmail(email);
                u.setNombre(nombre);
                u.setTipoUsuario(null);
                u.setEstado(EstadoUsuario.pendiente);
                u.setFechaRegistro(LocalDateTime.now());
                return usuarioService.save(u);
            });

            // Actualizar último acceso
            usuario.setUltimoAcceso(LocalDateTime.now());
            usuarioService.save(usuario);

            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido: " + e.getMessage());
        }
    }

    // ── /registro/voluntario ──────────────────────────────────────────────────
    @PostMapping("/registro/voluntario")
    public ResponseEntity<?> registrarVoluntario(
            @RequestBody RegistroDTO dto,
            @RequestHeader("Authorization") String authHeader) {

        try {

            String uid = verificarToken(authHeader);

            // Buscar si ya existe el usuario creado por /verify
            Usuario usuario = usuarioService.findByFirebaseUid(uid)
                    .orElse(null);

            if (usuario == null) {

                // Crear nuevo usuario
                usuario = new Usuario();
                usuario.setFirebaseUid(uid);
                usuario.setEmail(dto.getEmail());
                usuario.setFechaRegistro(LocalDateTime.now());

            } else {

                // Si existe, actualizar email por seguridad
                usuario.setEmail(dto.getEmail());

            }

            // Datos del voluntario
            usuario.setNombre(dto.getNombre());
            usuario.setTipoUsuario(TipoUsuario.voluntario);
            usuario.setEstado(EstadoUsuario.activo);
            usuario.setUltimoAcceso(LocalDateTime.now());

            usuarioService.save(usuario);

            return ResponseEntity.ok(usuario);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body("Error al registrar voluntario: " + e.getMessage());

        }
    }

    // ── /registro/ayudado ─────────────────────────────────────────────────────
    @PostMapping("/registro/ayudado")
    public ResponseEntity<?> registrarAyudado(
            @RequestBody AyudadoRegistroDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String uid = verificarToken(authHeader);

            // ── 1. Upsert usuario ────────────────────────────────────────────
            // findByFirebaseUid devuelve el existente (creado por /verify)
            // orElseGet crea uno nuevo solo si realmente no existe
            Usuario usuario = usuarioService.findByFirebaseUid(uid).orElseGet(Usuario::new);
            usuario.setFirebaseUid(uid);
            usuario.setNombre(dto.getNombre());
            usuario.setTipoUsuario(TipoUsuario.ayudado);
            usuario.setEstado(EstadoUsuario.pendiente);
            if (usuario.getId() == null) {
                // Solo setear estos campos si es un usuario completamente nuevo
                usuario.setEmail(dto.getEmail());
                usuario.setFechaRegistro(LocalDateTime.now());
            }
            usuario = usuarioService.save(usuario);

            // ── 2. Crear perfil ayudado (solo si no existe ya) ───────────────
            // Evita duplicado si el endpoint se llama dos veces
            Ayudado ayudado = ayudadoRepository
                    .findByUsuarioId(usuario.getId())
                    .orElseGet(Ayudado::new);

            ayudado.setUsuario(usuario);
            ayudado.setDni(dto.getDni());                                      // ← nuevo
            ayudado.setNombreConyuge(dto.getNombreConyuge());
            ayudado.setFechaNacimientoConyuge(dto.getFechaNacimientoConyuge());
            ayudado.setLugarNacimientoConyuge(dto.getLugarNacimientoConyuge());
            ayudado.setCantidadIntegrantes(dto.getCantidadIntegrantes());
            ayudadoRepository.save(ayudado);

            return ResponseEntity.ok("Ayudado registrado. Pendiente de aprobación.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── Helper: extrae y verifica el token, devuelve el UID ──────────────────
    private String verificarToken(String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "");
        FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);
        return decoded.getUid();
    }
}