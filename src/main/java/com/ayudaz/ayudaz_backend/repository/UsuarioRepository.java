package com.ayudaz.ayudaz_backend.repository;

import com.ayudaz.ayudaz_backend.model.EstadoUsuario;
import com.ayudaz.ayudaz_backend.model.TipoUsuario;
import com.ayudaz.ayudaz_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByFirebaseUid(String firebaseUid);

    Optional<Usuario> findByEmail(String email);

    // Método corregido: usa el enum EstadoUsuario directamente
    List<Usuario> findByEstado(EstadoUsuario estado);

    // Alternativa si prefieres String (pero mejor usar el enum)
    // List<Usuario> findByEstado(String estado);
}