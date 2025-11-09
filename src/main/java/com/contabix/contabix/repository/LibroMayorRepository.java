package com.contabix.contabix.repository;

import com.contabix.contabix.model.LibroMayor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibroMayorRepository extends JpaRepository<LibroMayor, Long> {
}