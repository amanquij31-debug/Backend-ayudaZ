package com.ayudaz.ayudaz_backend.service;

import com.ayudaz.ayudaz_backend.model.Ayudado;
import com.ayudaz.ayudaz_backend.model.EstadoUsuario;
import com.ayudaz.ayudaz_backend.model.TipoUsuario;
import com.ayudaz.ayudaz_backend.model.Usuario;
import com.ayudaz.ayudaz_backend.repository.AyudadoRepository;
import com.ayudaz.ayudaz_backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {


    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AyudadoRepository ayudadoRepository;

    /**
     * Registra o actualiza un usuario basado en Firebase UID.
     * Si el usuario ya existe, actualiza solo el último acceso.
     * Si no existe, crea uno nuevo con estado pendiente.
     * @param firebaseUid UID de Firebase
     * @param email correo electrónico
     * @param nombre nombre del usuario
     * @param tipoUsuario "voluntario", "ayudado" o "admin"
     * @return Usuario guardado
     */
    @Transactional
    public Usuario registrarOActualizarUsuario(String firebaseUid, String email, String nombre, String tipoUsuario) {
        Optional<Usuario> existing = usuarioRepository.findByFirebaseUid(firebaseUid);
        Usuario usuario;
        if (existing.isPresent()) {
            usuario = existing.get();
            usuario.setUltimoAcceso(LocalDateTime.now());
        } else {
            usuario = new Usuario();
            usuario.setFirebaseUid(firebaseUid);
            usuario.setEmail(email);
            usuario.setNombre(nombre);
            usuario.setTipoUsuario(TipoUsuario.valueOf(tipoUsuario));
            usuario.setEstado(EstadoUsuario.pendiente);
            usuario.setFechaRegistro(LocalDateTime.now());
        }
        return usuarioRepository.save(usuario);
    }

    /**
     * Busca un usuario por su Firebase UID.
     * @param firebaseUid UID de Firebase
     * @return Optional<Usuario>
     */
    public Optional<Usuario> findByFirebaseUid(String firebaseUid) {
        return usuarioRepository.findByFirebaseUid(firebaseUid);
    }



    /**
     * Obtiene todos los usuarios con estado 'pendiente' (para aprobación del admin).
     * @return lista de usuarios pendientes
     */
    public List<Usuario> getUsuariosPendientes() {
        return usuarioRepository.findByEstado(EstadoUsuario.pendiente);
    }

    /**
     * Aprueba un usuario cambiando su estado a 'activo'.
     * @param usuarioId ID del usuario
     * @throws RuntimeException si el usuario no existe
     */

    @Transactional
    public void aprobarUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setEstado(EstadoUsuario.activo);
        usuarioRepository.save(usuario);

        // Si el usuario es ayudado, actualizar los campos en la tabla ayudados
        if (usuario.getTipoUsuario() == TipoUsuario.ayudado) {
            Optional<Ayudado> ayudadoOpt = ayudadoRepository.findByUsuarioId(usuarioId);
            if (ayudadoOpt.isPresent()) {
                Ayudado ayudado = ayudadoOpt.get();
                ayudado.setConfirmacionAdmin(true);
                ayudado.setFechaConfirmacion(LocalDateTime.now());
                // Opcional: si quieres agregar una observación por defecto o permitir que el admin la envíe
                // ayudado.setObservaciones("Aprobado por administrador");
                ayudadoRepository.save(ayudado);
            }
        }
    }

    /**
     * Elimina un usuario (rechazo) de la base de datos.
     * @param usuarioId ID del usuario
     */
    @Transactional
    public void eliminarUsuario(Long usuarioId) {
        usuarioRepository.deleteById(usuarioId);
    }

    /**
     * Guarda los datos específicos de un usuario ayudado (nombre cónyuge, integrantes, etc.).
     * @param usuario el usuario ayudado (debe estar guardado previamente)
     * @param datosAyudado objeto con los datos extra
     * @return Ayudado guardado
     */
    @Transactional
    public Ayudado guardarDatosAyudado(Usuario usuario, Ayudado datosAyudado) {
        datosAyudado.setUsuario(usuario);
        return ayudadoRepository.save(datosAyudado);
    }

    /**
     * Obtiene un usuario por su ID.
     * @param id ID del usuario
     * @return Optional<Usuario>
     */
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        // Asegurar que el usuario no tenga rol asignado
        usuario.setTipoUsuario(null);
        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza un usuario existente (para asignarle rol y otros campos después del registro).
     * @param usuario objeto Usuario con los datos actualizados (debe tener id)
     * @return Usuario actualizado
     */
    @Transactional
    public Usuario actualizarUsuario(Usuario usuario) {
        // Verificar que el usuario existe
        if (!usuarioRepository.existsById(usuario.getId())) {
            throw new RuntimeException("Usuario no encontrado para actualizar");
        }
        return usuarioRepository.save(usuario);
    }

    /**
     * Suspende un usuario (cambia su estado a 'rechazado').
     */
    @Transactional
    public void suspenderUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setEstado(EstadoUsuario.rechazado);
        usuarioRepository.save(usuario);
    }

    /**
     * Edita un usuario (nombre, email, tipoUsuario, estado).
     */
    @Transactional
    public Usuario editarUsuario(Long usuarioId, String nombre, String email, String tipoUsuario, String estado) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (nombre != null) usuario.setNombre(nombre);
        if (email != null) usuario.setEmail(email);
        if (tipoUsuario != null) usuario.setTipoUsuario(TipoUsuario.valueOf(tipoUsuario));
        if (estado != null) usuario.setEstado(EstadoUsuario.valueOf(estado));
        return usuarioRepository.save(usuario);
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }


}