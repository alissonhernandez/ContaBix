package com.contabix.contabix.repository;

import com.contabix.contabix.model.CuentaContable;
import com.contabix.contabix.model.DetalleAsiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetalleAsientoRepository extends JpaRepository<DetalleAsiento, Integer> {

    // 🔹 Para LIBRO MAYOR (usa la entidad Librodiario que ya tienes mapeada)
    @Query("""
        SELECT d.cuentaContable.id AS cuentaId,
               COALESCE(SUM(d.debe), 0)  AS totalDebe,
               COALESCE(SUM(d.haber), 0) AS totalHaber
        FROM Librodiario d
        GROUP BY d.cuentaContable.id
    """)
    List<Object[]> generarLibroMayor();

    // 🔹 Obtener la cuenta completa por id (la usa LibroMayorService)
    @Query("SELECT c FROM CuentaContable c WHERE c.id = :id")
    CuentaContable findCuentaById(@Param("id") Integer id);

    // 🔹 Para ESTADO DE RESULTADOS
    @Query("""
        SELECT
            SUM(CASE WHEN d.cuenta.tipo = 'ingreso'
                     THEN d.haber - d.debe ELSE 0 END),
            SUM(CASE WHEN d.cuenta.categoria = 'costo'
                     THEN d.debe - d.haber ELSE 0 END),
            SUM(CASE WHEN d.cuenta.categoria = 'operativo'
                     THEN d.debe - d.haber ELSE 0 END),
            SUM(CASE WHEN d.cuenta.categoria = 'otros'
                     THEN d.debe - d.haber ELSE 0 END)
        FROM DetalleAsiento d
    """)
    Object[] generarEstadoResultados();

    // 🔹 Para BALANCE DE COMPROBACIÓN
    @Query("""
        SELECT d.cuenta.codigo,
               d.cuenta.nombre,
               COALESCE(SUM(d.debe), 0),
               COALESCE(SUM(d.haber), 0)
        FROM DetalleAsiento d
        GROUP BY d.cuenta.codigo, d.cuenta.nombre
        ORDER BY d.cuenta.codigo ASC
    """)
    List<Object[]> obtenerBalanceComprobacion();

}

