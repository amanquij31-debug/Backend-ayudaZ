package com.ayudaz.ayudaz_backend.repository;

import com.ayudaz.ayudaz_backend.model.Mensaje;
import com.ayudaz.ayudaz_backend.model.OfertaAyuda;
import com.ayudaz.ayudaz_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    List<Mensaje> findByOfertaOrderByFechaEnvioAsc(OfertaAyuda oferta);

    List<Mensaje> findByRemitente(Usuario remitente);

    List<Mensaje> findByLeidoFalse();
}