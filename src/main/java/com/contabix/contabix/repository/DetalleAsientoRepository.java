package com.contabix.contabix.repository;


import com.contabix.contabix.model.CuentaContable;
import com.contabix.contabix.model.DetalleAsiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DetalleAsientoRepository extends JpaRepository<DetalleAsiento, Integer> {

    @Query("""
        SELECT d.cuentaContable.id AS cuentaId,
               SUM(d.debe) AS totalDebe,
               SUM(d.haber) AS totalHaber
        FROM Librodiario d
        GROUP BY d.cuentaContable.id
    """)
    List<Object[]> generarLibroMayor();

    // ➤ Consulta para obtener la cuenta completa
    @Query("SELECT c FROM CuentaContable c WHERE c.id = :id")
    CuentaContable findCuentaById(@Param("id") Integer id);

    @Query("SELECT " +
            "SUM(CASE WHEN d.cuenta.tipo = 'ingreso' THEN d.haber - d.debe ELSE 0 END), " +
            "SUM(CASE WHEN d.cuenta.categoria = 'costo' THEN d.debe - d.haber ELSE 0 END), " +
            "SUM(CASE WHEN d.cuenta.categoria = 'operativo' THEN d.debe - d.haber ELSE 0 END), " +
            "SUM(CASE WHEN d.cuenta.categoria = 'otros' THEN d.debe - d.haber ELSE 0 END) " +
            "FROM DetalleAsiento d")
    Object[] generarEstadoResultados();

}
