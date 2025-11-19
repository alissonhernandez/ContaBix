package com.contabix.contabix.service;

import com.contabix.contabix.model.Auditoria;
import com.contabix.contabix.model.Usuario;
import com.contabix.contabix.repository.AuditoriaRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public void registrar(Usuario usuario, String accion, String descripcion) {
        if (usuario == null) {
            return;
        }
        Auditoria auditoria = new Auditoria(usuario, accion, descripcion);
        auditoriaRepository.save(auditoria);
    }
}
