package com.contabix.contabix.repository;

import com.contabix.contabix.model.PeriodoContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PeriodoContableRepository extends JpaRepository<PeriodoContable, Integer> {

    /**
     * Retorna todos los periodos abiertos (cerrado = false)
     */
    List<PeriodoContable> findByCerradoFalse();

    /**
     * Retorna un periodo donde la fecha indicada esté dentro del rango [fechaInicio, fechaFin]
     */
    @Query("SELECT p FROM PeriodoContable p " +
            "WHERE p.fechaInicio <= :fecha AND p.fechaFin >= :fecha")
    PeriodoContable findByFechaDentroDelPeriodo(@Param("fecha") LocalDate fecha);

    /**
     * Retorna todos los periodos que se solapan con un rango de fechas dado.
     * Esto es útil para validar que no se creen periodos contables que se crucen.
     */
    @Query("SELECT p FROM PeriodoContable p " +
            "WHERE p.fechaInicio <= :fechaFin AND p.fechaFin >= :fechaInicio")
    List<PeriodoContable> findPeriodosQueSeSolapan(@Param("fechaInicio") LocalDate fechaInicio,
                                                   @Param("fechaFin") LocalDate fechaFin);
}


