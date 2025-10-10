package com.contabix.contabix.repository;

import com.contabix.contabix.model.Asiento;
import com.contabix.contabix.model.CuentaContable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsientoRepository extends JpaRepository<Asiento, Integer> {}