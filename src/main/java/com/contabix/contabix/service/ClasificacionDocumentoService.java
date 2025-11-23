package com.contabix.contabix.service;

import com.contabix.contabix.model.ClasificacionDocumento;
import com.contabix.contabix.repository.ClasificacionDocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClasificacionDocumentoService {

    @Autowired
    private ClasificacionDocumentoRepository repository;

    public List<ClasificacionDocumento> listar() {
        return repository.findAll();
    }

    public ClasificacionDocumento guardar(ClasificacionDocumento c) {
        return repository.save(c);
    }

    public ClasificacionDocumento buscarPorId(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public void eliminar(Integer id) {
        repository.deleteById(id);
    }
}
