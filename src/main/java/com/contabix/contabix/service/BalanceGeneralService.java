package com.contabix.contabix.service;

import com.contabix.contabix.dto.BalanceGeneralDTO;
import com.contabix.contabix.model.CuentaContable;
import com.contabix.contabix.model.DetalleAsiento;
import com.contabix.contabix.repository.CuentaContableRepository;
import com.contabix.contabix.repository.DetalleAsientoRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BalanceGeneralService {

    private final DetalleAsientoRepository detalleRepo;
    private final CuentaContableRepository cuentaRepo;

    public BalanceGeneralService(DetalleAsientoRepository detalleRepo,
                                 CuentaContableRepository cuentaRepo) {
        this.detalleRepo = detalleRepo;
        this.cuentaRepo = cuentaRepo;
    }

    public Map<String, List<BalanceGeneralDTO>> generarBalanceGeneral() {

        List<DetalleAsiento> detalles = detalleRepo.findAll();
        List<CuentaContable> cuentas = cuentaRepo.findAll();

        Map<Integer, Double> saldos = new HashMap<>();

        // 1. Calcular saldo por cuenta (debe - haber)
        for (DetalleAsiento d : detalles) {
            if (d.getCuenta() == null || d.getCuenta().getId() == null) continue;

            Integer idCuenta = d.getCuenta().getId();
            double debe = d.getDebe() != null ? d.getDebe() : 0.0;
            double haber = d.getHaber() != null ? d.getHaber() : 0.0;

            saldos.put(idCuenta, saldos.getOrDefault(idCuenta, 0.0) + (debe - haber));
        }

        List<BalanceGeneralDTO> activos = new ArrayList<>();
        List<BalanceGeneralDTO> pasivos = new ArrayList<>();
        List<BalanceGeneralDTO> patrimonio = new ArrayList<>();

        // 2. Clasificar por tipo de cuenta
        for (CuentaContable c : cuentas) {

            String tipo = c.getTipo();
            if (tipo == null) {
                // si no tiene tipo, la saltamos para no romper
                continue;
            }

            double saldo = saldos.getOrDefault(c.getId(), 0.0);

            BalanceGeneralDTO dto = new BalanceGeneralDTO(
                    c.getCodigo(),
                    c.getNombre(),
                    tipo,
                    c.getCategoria(),
                    saldo
            );

            switch (tipo.toLowerCase()) {
                case "activo" -> activos.add(dto);
                case "pasivo" -> pasivos.add(dto);
                case "patrimonio" -> patrimonio.add(dto);
                default -> { /* ignorar otros tipos */ }
            }
        }

        Map<String, List<BalanceGeneralDTO>> resultado = new HashMap<>();
        resultado.put("activos", activos);
        resultado.put("pasivos", pasivos);
        resultado.put("patrimonio", patrimonio);

        return resultado;
    }
}
