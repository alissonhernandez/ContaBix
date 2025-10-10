package com.contabix.contabix.repository;

import com.contabix.contabix.model.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AsientoRepository extends JpaRepository<Asiento, Integer> {
    // Filtrar por rango de fechas
    List<Asiento> findByFechaBetween(LocalDate start, LocalDate end);

    // Búsqueda simple por descripción conteniendo texto
    List<Asiento> findByDescripcionContainingIgnoreCase(String texto);
}
