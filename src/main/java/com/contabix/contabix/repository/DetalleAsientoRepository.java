package com.contabix.contabix.repository;


import com.contabix.contabix.model.CuentaContable;
import com.contabix.contabix.model.DetalleAsiento;
import org.springframework.data.jpa.repository.JpaRepository;
import com.contabix.contabix.model.Librodiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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

}
