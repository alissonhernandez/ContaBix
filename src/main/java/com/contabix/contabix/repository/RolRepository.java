package com.contabix.contabix.repository;

import com.contabix.contabix.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio para la entidad Rol.
 * Extiende JpaRepository para ofrecer operaciones CRUD básicas.
 *
 * JpaRepository<Rol, Long> indica que maneja la entidad Rol con ID de tipo Long.
 */
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
}
