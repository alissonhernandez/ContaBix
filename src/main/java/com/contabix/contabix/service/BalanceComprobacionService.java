package com.contabix.contabix.service;

import com.contabix.contabix.dto.BalanceComprobacionDTO;
import com.contabix.contabix.repository.DetalleAsientoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BalanceComprobacionService {

    private final DetalleAsientoRepository repo;

    public BalanceComprobacionService(DetalleAsientoRepository repo) {
        this.repo = repo;
    }

    public List<BalanceComprobacionDTO> generarBalance() {

        List<Object[]> datos = repo.obtenerBalanceComprobacion();
        List<BalanceComprobacionDTO> lista = new ArrayList<>();

        for (Object[] fila : datos) {

            String codigo = (String) fila[0];
            String nombre = (String) fila[1];

            Double debe  = fila[2] != null ? (Double) fila[2] : 0.0;
            Double haber = fila[3] != null ? (Double) fila[3] : 0.0;

            lista.add(new BalanceComprobacionDTO(
                    codigo, nombre, debe, haber
            ));
        }

        return lista;
    }
}
