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

        // 1. Calcular saldo por cuenta según tipo contable
        for (DetalleAsiento d : detalles) {
            if (d.getCuenta() == null || d.getCuenta().getId() == null) continue;

            CuentaContable c = d.getCuenta();
            Integer idCuenta = c.getId();
            double debe = d.getDebe() != null ? d.getDebe() : 0.0;
            double haber = d.getHaber() != null ? d.getHaber() : 0.0;

            double saldo;
            switch (c.getTipo().toLowerCase()) {
                case "activo":
                case "gasto":
                    saldo = debe - haber;
                    break;
                case "pasivo":
                case "patrimonio":
                case "ingreso":
                    saldo = haber - debe;
                    break;
                default:
                    saldo = debe - haber;
            }

            saldos.put(idCuenta, saldos.getOrDefault(idCuenta, 0.0) + saldo);
        }

        List<BalanceGeneralDTO> activos = new ArrayList<>();
        List<BalanceGeneralDTO> pasivos = new ArrayList<>();
        List<BalanceGeneralDTO> patrimonio = new ArrayList<>();

        double totalActivos = 0.0;
        double totalPasivos = 0.0;
        double totalPatrimonio = 0.0;
        double totalIngresos = 0.0;
        double totalGastos = 0.0;

        // 2. Clasificar cuentas y calcular totales
        for (CuentaContable c : cuentas) {
            String tipo = c.getTipo();
            if (tipo == null) continue;

            double saldo = saldos.getOrDefault(c.getId(), 0.0);

            switch (tipo.toLowerCase()) {
                case "activo" -> {
                    activos.add(new BalanceGeneralDTO(c.getCodigo(), c.getNombre(), tipo, c.getCategoria(), saldo));
                    totalActivos += saldo;
                }
                case "pasivo" -> {
                    pasivos.add(new BalanceGeneralDTO(c.getCodigo(), c.getNombre(), tipo, c.getCategoria(), saldo));
                    totalPasivos += saldo;
                }
                case "patrimonio" -> {
                    if (!c.getNombre().equalsIgnoreCase("Resultado del Ejercicio")) {
                        patrimonio.add(new BalanceGeneralDTO(c.getCodigo(), c.getNombre(), tipo, c.getCategoria(), saldo));
                        totalPatrimonio += saldo;
                    }
                }
                case "ingreso" -> totalIngresos += saldo;
                case "gasto" -> totalGastos += saldo;
                default -> { /* ignorar otros tipos */ }
            }
        }

        // 3. Calcular Resultado del Ejercicio y agregar al patrimonio
        double resultadoDelEjercicio = totalIngresos - totalGastos;
        BalanceGeneralDTO resultadoDTO = new BalanceGeneralDTO(
                "3.3",
                "Resultado del Ejercicio",
                "patrimonio",
                "Resultado del periodo",
                resultadoDelEjercicio
        );
        patrimonio.add(resultadoDTO);
        totalPatrimonio += resultadoDelEjercicio;

        // 4. Verificación del balance
        double totalPasivosPatrimonio = totalPasivos + totalPatrimonio;
        if (Math.abs(totalActivos - totalPasivosPatrimonio) > 0.01) {
            System.err.println("⚠️ ALERTA: Balance Descuadrado!");
            System.err.println("Total Activos: " + totalActivos);
            System.err.println("Total Pasivos + Patrimonio: " + totalPasivosPatrimonio);
        } else {
            System.out.println("✅ Balance cuadrado correctamente.");
        }

        Map<String, List<BalanceGeneralDTO>> resultado = new HashMap<>();
        resultado.put("activos", activos);
        resultado.put("pasivos", pasivos);
        resultado.put("patrimonio", patrimonio);

        return resultado;
    }

    public List<BalanceGeneralDTO> obtenerBalanceGeneral() {
        Map<String, List<BalanceGeneralDTO>> balance = generarBalanceGeneral();
        List<BalanceGeneralDTO> lista = new ArrayList<>();
        lista.addAll(balance.get("activos"));
        lista.addAll(balance.get("pasivos"));
        lista.addAll(balance.get("patrimonio"));
        return lista;
    }
}
