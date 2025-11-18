package com.contabix.contabix.service;

import com.contabix.contabix.model.CuentaContable;
import com.contabix.contabix.model.LibroMayor;
import com.contabix.contabix.repository.CuentaContableRepository;
import com.contabix.contabix.repository.DetalleAsientoRepository;
import com.contabix.contabix.repository.LibroMayorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private CuentaContableRepository cuentaContableRepository;

    public void actualizarLibroMayor() {

        // 1️⃣ Limpiar contenido previo del libro mayor
        libroMayorRepo.deleteAll();

        // 2️⃣ Generar datos desde el detalle de asientos
        List<Object[]> datos = detalleAsientoRepo.generarLibroMayor();

        for (Object[] fila : datos) {

            Integer cuentaId      = (Integer) fila[0];
            Double totalDebeObj   = (Double) fila[1];
            Double totalHaberObj  = (Double) fila[2];

            // ✨ Blindaje contra null (aunque COALESCE ya ayuda)
            double totalDebe  = totalDebeObj  != null ? totalDebeObj  : 0.0;
            double totalHaber = totalHaberObj != null ? totalHaberObj : 0.0;
            double saldo      = totalDebe - totalHaber;

            // Buscar la cuenta real
            CuentaContable cuenta = cuentaContableRepository
                    .findById(cuentaId)
                    .orElse(null);

            if (cuenta == null) {
                // Si por alguna razón no existe, salta esta fila
                continue;
            }

            LibroMayor mayor = new LibroMayor();
            mayor.setCuentaContable(cuenta);   // usa el setter que ya tienes en tu entidad
            mayor.setTotalDebe(totalDebe);
            mayor.setTotalHaber(totalHaber);
            mayor.setSaldo(saldo);

            libroMayorRepo.save(mayor);
        }
    }
}
