package com.contabix.contabix.repository;

import com.contabix.contabix.model.CuentaContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.contabix.contabix.dto.SumaDebeHaberDTO;
import org.springframework.data.repository.query.Param;


public interface CuentaContableRepository extends JpaRepository<CuentaContable, Integer> {
    // Sumar debe/haber por tipo de cuenta
    @Query("""
        SELECT new com.contabix.contabix.dto.SumaDebeHaberDTO(
            SUM(d.debe), SUM(d.haber))
        FROM DetalleAsiento d
        JOIN d.cuenta c
        WHERE c.tipo = :tipo
    """)
    SumaDebeHaberDTO sumDebeHaberByTipo(@Param("tipo") String tipo);

    // Sumar gastos por categoría
    @Query("""
        SELECT new com.contabix.contabix.dto.SumaDebeHaberDTO(
            SUM(d.debe), SUM(d.haber))
        FROM DetalleAsiento d
        JOIN d.cuenta c
        WHERE c.tipo = 'gasto' AND c.categoria = :categoria
    """)
    SumaDebeHaberDTO sumGastosPorCategoria(@Param("categoria") String categoria);
}


