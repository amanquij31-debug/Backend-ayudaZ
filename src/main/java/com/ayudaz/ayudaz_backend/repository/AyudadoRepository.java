package com.ayudaz.ayudaz_backend.repository;

import com.ayudaz.ayudaz_backend.model.Ayudado;
import com.ayudaz.ayudaz_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface AyudadoRepository extends JpaRepository<Ayudado, Long> {
    Optional<Ayudado> findByUsuarioId(Long usuarioId);


}