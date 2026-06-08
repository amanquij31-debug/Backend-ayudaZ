package com.ayudaz.ayudaz_backend.controller;

import com.ayudaz.ayudaz_backend.model.Usuario;
import com.ayudaz.ayudaz_backend.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Obtener perfil del usuario autenticado (usuarioId viene del filtro).
     */
    @GetMapping("/perfil")
    public ResponseEntity<?> getPerfil(HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");
        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }
        Usuario usuario = usuarioService.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    /**
     * Actualizar perfil (solo nombre por ahora).
     */
    @PutMapping("/perfil")
    public ResponseEntity<?> updatePerfil(@RequestBody Usuario usuarioActualizado, HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("usuarioId");
        Usuario usuario = usuarioService.findById(usuarioId).orElse(null);
        if (usuario == null) return ResponseEntity.notFound().build();
        usuario.setNombre(usuarioActualizado.getNombre());
        // Aquí podrías agregar más campos editables
        usuarioService.registrarOActualizarUsuario(usuario.getFirebaseUid(), usuario.getEmail(), usuario.getNombre(), usuario.getTipoUsuario().toString());
        return ResponseEntity.ok(usuario);
    }
}