package com.contabix.contabix.service;

import com.contabix.contabix.model.Asiento;
import com.contabix.contabix.model.DetalleAsiento;
import com.contabix.contabix.repository.AsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsientoService {

    @Autowired
    private AsientoRepository asientoRepository;

    @Transactional
    public Asiento guardarAsientoConValidacion(Asiento asiento) {
        // Eliminar detalles vacíos
        asiento.getDetalles().removeIf(d ->
                (d.getDebe() == null || d.getDebe() == 0) &&
                        (d.getHaber() == null || d.getHaber() == 0) &&
                        (d.getCuenta() == null)
        );

        // Validar que haya al menos un detalle válido
        if (asiento.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("El asiento debe tener al menos un detalle con Debe o Haber y una cuenta válida.");
        }

        // Validar balance Debe/Haber
        double totalDebe = asiento.getDetalles().stream().mapToDouble(d -> d.getDebe() != null ? d.getDebe() : 0).sum();
        double totalHaber = asiento.getDetalles().stream().mapToDouble(d -> d.getHaber() != null ? d.getHaber() : 0).sum();

        if (totalDebe != totalHaber) {
            throw new IllegalArgumentException("El asiento no está balanceado: Debe = " + totalDebe + ", Haber = " + totalHaber);
        }

        // Asignar relación bidireccional
        for (DetalleAsiento detalle : asiento.getDetalles()) {
            detalle.setAsiento(asiento);
        }

        return asientoRepository.save(asiento);
    }
}
