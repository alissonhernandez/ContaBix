package com.contabix.contabix.service;

import com.contabix.contabix.model.LibroMayor;
import com.contabix.contabix.repository.LibroMayorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroMayorService {
    private final LibroMayorRepository repository;

    public LibroMayorService(LibroMayorRepository repository) {
        this.repository = repository;
    }

    public List<LibroMayor> listarTodos() {
        return repository.findAll();
    }
}