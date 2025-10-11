package com.contabix.contabix.service;

import com.contabix.contabix.model.Asiento;
import com.contabix.contabix.model.DetalleAsiento;
import com.contabix.contabix.repository.AsientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsientoService {

    private final AsientoRepository asientoRepository;

    public AsientoService(AsientoRepository asientoRepository) {
        this.asientoRepository = asientoRepository;
    }

    @Transactional
    public void guardarAsientoConValidacion(Asiento asiento) {
        double totalDebe = asiento.getDetalles().stream().mapToDouble(DetalleAsiento::getDebe).sum();
        double totalHaber = asiento.getDetalles().stream().mapToDouble(DetalleAsiento::getHaber).sum();

        if (totalDebe != totalHaber) {
            throw new IllegalArgumentException("El asiento no está balanceado: Débitos ≠ Créditos");
        }

        // Asociar detalles al asiento
        for (DetalleAsiento detalle : asiento.getDetalles()) {
            detalle.setAsiento(asiento);
        }

        asientoRepository.save(asiento);
    }
}
