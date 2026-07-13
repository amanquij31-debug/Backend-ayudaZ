package com.ayudaz.ayudaz_backend.repository;


import com.ayudaz.ayudaz_backend.model.LogAdmin;
import com.ayudaz.ayudaz_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LogAdminRepository extends JpaRepository<LogAdmin, Long> {

    List<LogAdmin> findByAdmin(Usuario admin);

    List<LogAdmin> findByFechaBetween(
            LocalDateTime inicio,
            LocalDateTime fin
    );

    List<LogAdmin> findByAccion(String accion);

    List<LogAdmin> findAllByOrderByFechaDesc();
}


