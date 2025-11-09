package com.contabix.contabix.repository;

import com.contabix.contabix.model.DocumentoFuente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentoFuenteRepository extends JpaRepository<DocumentoFuente, Integer> {
}
