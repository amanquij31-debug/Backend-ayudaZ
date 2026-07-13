package com.ayudaz.ayudaz_backend.service;

import com.ayudaz.ayudaz_backend.model.LogAdmin;
import com.ayudaz.ayudaz_backend.model.Usuario;
import com.ayudaz.ayudaz_backend.repository.LogAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogsAdminService {

    @Autowired
    private LogAdminRepository logAdminRepository;

    public void registrar(Usuario admin, String accion, String detalles) {
        LogAdmin log = new LogAdmin();
        log.setAdmin(admin);
        log.setAccion(accion);
        log.setDetalles(detalles);
        log.setFecha(LocalDateTime.now());
        logAdminRepository.save(log);
    }

    public List<LogAdmin> getTodos() {
        return logAdminRepository.findAll();
    }

    public List<LogAdmin> getByAdmin(Usuario admin) {
        return logAdminRepository.findByAdmin(admin);
    }

    public List<LogAdmin> getByAccion(String accion) {
        return logAdminRepository.findByAccion(accion);
    }

    public List<LogAdmin> getByFecha(LocalDateTime inicio, LocalDateTime fin) {
        return logAdminRepository.findByFechaBetween(inicio, fin);
    }
}