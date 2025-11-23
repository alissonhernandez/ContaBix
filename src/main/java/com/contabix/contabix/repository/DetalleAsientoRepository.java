package com.contabix.contabix.repository;

import com.contabix.contabix.model.DetalleAsiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetalleAsientoRepository extends JpaRepository<DetalleAsiento, Integer> {

    // 🔹 Para LIBRO MAYOR
    @Query("""
        SELECT d.cuenta.id AS cuentaId,
               COALESCE(SUM(d.debe), 0)  AS totalDebe,
               COALESCE(SUM(d.haber), 0) AS totalHaber
        FROM DetalleAsiento d
        GROUP BY d.cuenta.id
    """)
    List<Object[]> generarLibroMayor();

    // 🔹 Para ESTADO DE RESULTADOS
    @Query("""
        SELECT
            SUM(CASE WHEN c.tipo = 'ingreso' THEN d.haber - d.debe ELSE 0 END),
            SUM(CASE WHEN c.tipo = 'gasto' AND c.categoria = 'costo' THEN d.debe - d.haber ELSE 0 END),
            SUM(CASE WHEN c.tipo = 'gasto' AND c.categoria = 'operativo' THEN d.debe - d.haber ELSE 0 END),
            SUM(CASE WHEN c.tipo = 'gasto' AND c.categoria = 'otros' THEN d.debe - d.haber ELSE 0 END)
        FROM DetalleAsiento d
        JOIN d.cuenta c
    """)
    Object[] generarEstadoResultados();

    // 🔹 Para BALANCE DE COMPROBACIÓN
    @Query("""
        SELECT c.codigo,
               c.nombre,
               COALESCE(SUM(d.debe), 0),
               COALESCE(SUM(d.haber), 0)
        FROM DetalleAsiento d
        JOIN d.cuenta c
        GROUP BY c.codigo, c.nombre
        ORDER BY c.codigo ASC
    """)
    List<Object[]> obtenerBalanceComprobacion();

}
