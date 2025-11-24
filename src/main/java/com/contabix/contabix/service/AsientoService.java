package com.contabix.contabix.service;

import com.contabix.contabix.model.Asiento;
import com.contabix.contabix.model.DetalleAsiento;
import com.contabix.contabix.model.PeriodoContable;
import com.contabix.contabix.repository.AsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AsientoService {

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private PeriodoContableService periodoService;

    public Asiento guardarAsientoConValidacion(Asiento asiento) {

        // ---------------------------------------------
        // 1. VALIDAR PERIODO CONTABLE ACTIVO
        // ---------------------------------------------
        PeriodoContable activo = periodoService.obtenerPeriodoActivo();

        if (activo == null) {
            throw new IllegalArgumentException("No existe un período contable activo.");
        }

        if (asiento.getFecha().isBefore(activo.getFechaInicio())
                || asiento.getFecha().isAfter(activo.getFechaFin())) {

            throw new IllegalArgumentException(
                    "La fecha del asiento (" + asiento.getFecha() + ") no está dentro del período contable activo: "
                            + activo.getFechaInicio() + " al " + activo.getFechaFin()
            );
        }

        // ---------------------------------------------
        // 2. ELIMINAR DETALLES VACÍOS
        // ---------------------------------------------
        asiento.getDetalles().removeIf(d ->
                (d.getDebe() == null || d.getDebe() == 0) &&
                        (d.getHaber() == null || d.getHaber() == 0) &&
                        (d.getCuenta() == null)
        );

        // ---------------------------------------------
        // 3. VALIDAR QUE HAYA DETALLES
        // ---------------------------------------------
        if (asiento.getDetalles().isEmpty()) {
            throw new IllegalArgumentException(
                    "El asiento debe tener al menos un detalle con Debe o Haber y una cuenta válida."
            );
        }

        // ---------------------------------------------
        // 4. VALIDAR BALANCE DEBE / HABER
        // ---------------------------------------------
        double totalDebe = asiento.getDetalles()
                .stream()
                .mapToDouble(d -> d.getDebe() != null ? d.getDebe() : 0)
                .sum();

        double totalHaber = asiento.getDetalles()
                .stream()
                .mapToDouble(d -> d.getHaber() != null ? d.getHaber() : 0)
                .sum();

        if (totalDebe != totalHaber) {
            throw new IllegalArgumentException(
                    "El asiento no está balanceado. Debe = " + totalDebe + ", Haber = " + totalHaber
            );
        }

        // ---------------------------------------------
        // 5. RELACIÓN BIDIRECCIONAL ASIENTO -> DETALLES
        // ---------------------------------------------
        for (DetalleAsiento detalle : asiento.getDetalles()) {
            detalle.setAsiento(asiento);
        }

        // ---------------------------------------------
        // 6. GUARDAR ASIENTO
        // ---------------------------------------------
        return asientoRepository.save(asiento);
    }

    public List<Asiento> listarTodos() {
        return asientoRepository.findAll();
    }

    // Método que permite filtrar asientos por fecha
    public List<Asiento> findByFechaBetween(LocalDate start, LocalDate end) {
        return asientoRepository.findByFechaBetween(start, end);
    }


}
