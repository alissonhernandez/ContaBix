package com.contabix.contabix.service;

import com.contabix.contabix.model.LibroMayor;
import com.contabix.contabix.repository.LibroMayorRepository;
import org.springframework.stereotype.Service;
import com.contabix.contabix.model.CuentaContable;
import com.contabix.contabix.repository.DetalleAsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private DetalleAsientoRepository detalleAsientoRepo;

    @Autowired
    private LibroMayorRepository libroMayorRepo;

    public void actualizarLibroMayor() {

        // Eliminar contenido previo
        libroMayorRepo.deleteAll();

        // Generar datos desde el detalle de asientos
        List<Object[]> datos = detalleAsientoRepo.generarLibroMayor();

        for (Object[] fila : datos) {

            Integer cuentaId   = (Integer) fila[0];
            Double totalDebe   = (Double) fila[1];
            Double totalHaber  = (Double) fila[2];
            Double saldo       = totalDebe - totalHaber;

            CuentaContable cuenta = new CuentaContable();
            cuenta = detalleAsientoRepo.findCuentaById(cuentaId);

            LibroMayor mayor = new LibroMayor();
            mayor.setCuentaContable(cuenta);
            mayor.setTotalDebe(totalDebe);
            mayor.setTotalHaber(totalHaber);
            mayor.setSaldo(saldo);

            libroMayorRepo.save(mayor);
        }
    }
}