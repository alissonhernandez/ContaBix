package com.contabix.contabix.service;

import com.contabix.contabix.model.Asiento;
import com.contabix.contabix.model.DetalleAsiento;
import com.contabix.contabix.repository.AsientoRepository;
import com.contabix.contabix.repository.DetalleAsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AsientoService {

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private DetalleAsientoRepository detalleAsientoRepository;

    /**
     * Guarda asiento con sus detalles validando que suma(debe) == suma(haber).
     * Lanza IllegalArgumentException si no balancea.
     */
    @Transactional
    public Asiento guardarAsientoConValidacion(Asiento asiento) {
        double totalDebe = 0.0;
        double totalHaber = 0.0;

        List<DetalleAsiento> detalles = asiento.getDetalles();
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("El asiento debe contener al menos un detalle.");
        }

        for (DetalleAsiento d : detalles) {
            if (d.getDebe() != null) totalDebe += d.getDebe();
            if (d.getHaber() != null) totalHaber += d.getHaber();
        }

        // Comparación con tolerancia de redondeo
        if (Math.abs(totalDebe - totalHaber) > 0.0001) {
            throw new IllegalArgumentException(String.format("Asiento desbalanceado: debe=%.2f, haber=%.2f", totalDebe, totalHaber));
        }

        // Guardar asiento (cascade en DetalleAsiento si está configurado)
        Asiento guardado = asientoRepository.save(asiento);

        // Si no usas cascade, asignar asiento a cada detalle y guardar
        for (DetalleAsiento d : detalles) {
            d.setAsiento(guardado);
            detalleAsientoRepository.save(d);
        }

        return guardado;
    }
}
