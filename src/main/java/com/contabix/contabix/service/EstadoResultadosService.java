package com.contabix.contabix.service;

import com.contabix.contabix.dto.EstadoResultadosDTO;
import com.contabix.contabix.dto.SumaDebeHaberDTO;
import com.contabix.contabix.repository.CuentaContableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstadoResultadosService {

    @Autowired
    private CuentaContableRepository cuentaRepo;

    public EstadoResultadosDTO calcularEstadoResultados() {

        // Ingresos (haber de cuentas tipo ingreso)
        SumaDebeHaberDTO ingresosDTO = cuentaRepo.sumDebeHaberByTipo("ingreso");
        Double ingresos = ingresosDTO != null && ingresosDTO.getHaber() != null ? ingresosDTO.getHaber() : 0.0;

        // Costos (gasto con categoría 'costo')
        SumaDebeHaberDTO costosDTO = cuentaRepo.sumGastosPorCategoria("costo");
        Double costos = costosDTO != null && costosDTO.getDebe() != null ? costosDTO.getDebe() : 0.0;

        // Gastos Operativos (gasto con categoría 'operativo')
        SumaDebeHaberDTO gastosDTO = cuentaRepo.sumGastosPorCategoria("operativo");
        Double gastos = gastosDTO != null && gastosDTO.getDebe() != null ? gastosDTO.getDebe() : 0.0;

        // Otros ingresos (haber de cuentas tipo otro)
        SumaDebeHaberDTO otrosDTO = cuentaRepo.sumDebeHaberByTipo("otro");
        Double otrosIngresos = otrosDTO != null && otrosDTO.getHaber() != null ? otrosDTO.getHaber() : 0.0;

        // Utilidad final
        double utilidad = ingresos - costos - gastos + otrosIngresos;

        return new EstadoResultadosDTO(
                ingresos, costos, gastos, otrosIngresos, utilidad
        );
    }
}









