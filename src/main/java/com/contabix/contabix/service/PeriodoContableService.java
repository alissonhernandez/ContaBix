package com.contabix.contabix.service;

import com.contabix.contabix.model.PeriodoContable;
import com.contabix.contabix.repository.PeriodoContableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PeriodoContableService {

    private final PeriodoContableRepository repo;

    @Autowired
    public PeriodoContableService(PeriodoContableRepository repo) {
        this.repo = repo;
    }

    /**
     * Obtiene el primer periodo abierto. Lanza excepción si hay más de uno abierto.
     */
    public PeriodoContable obtenerPeriodoActivo() {
        List<PeriodoContable> abiertos = repo.findByCerradoFalse();
        if (abiertos.isEmpty()) return null;
        if (abiertos.size() > 1) {
            throw new IllegalStateException("Existen varios periodos abiertos. Revisa la base de datos.");
        }
        return abiertos.get(0);
    }

    /**
     * Verifica si una fecha está dentro de algún periodo activo.
     */
    public boolean fechaValida(LocalDate fecha) {
        return repo.findByFechaDentroDelPeriodo(fecha) != null;
    }

    /**
     * Crea un nuevo periodo contable.
     * Valida:
     * - que fecha inicio <= fecha fin
     * - que no se solape con periodos existentes
     */
    public PeriodoContable crearPeriodo(LocalDate inicio, LocalDate fin) {
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }

        // Verificar solapamiento con periodos existentes
        List<PeriodoContable> solapados = repo.findPeriodosQueSeSolapan(inicio, fin);
        if (!solapados.isEmpty()) {
            throw new IllegalStateException("El nuevo periodo se solapa con periodos existentes.");
        }

        PeriodoContable p = new PeriodoContable();
        p.setFechaInicio(inicio);
        p.setFechaFin(fin);
        p.setCerrado(false);

        return repo.save(p);
    }

    /**
     * Cierra un periodo por su ID.
     */
    public void cerrarPeriodo(Integer id) {
        PeriodoContable p = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Periodo no encontrado"));
        if (p.isCerrado()) {
            throw new IllegalStateException("El periodo ya está cerrado.");
        }
        p.setCerrado(true);
        repo.save(p);
    }

    public List<PeriodoContable> listar() {
        return repo.findAll();
    }

    public void eliminarPeriodo(Integer id) {
        // Opcional: podrías verificar si el periodo está cerrado y bloquear eliminación
        PeriodoContable periodo = repo.findById(id)
                .orElseThrow(() -> new IllegalStateException("Periodo no encontrado"));

        if (periodo.isCerrado()) {
            throw new IllegalStateException("No se puede eliminar un periodo cerrado");
        }

        repo.delete(periodo);
    }


}


