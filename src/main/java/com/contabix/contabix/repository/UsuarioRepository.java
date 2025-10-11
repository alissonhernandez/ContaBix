package com.contabix.contabix.repository;

import com.contabix.contabix.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 * Proporciona CRUD automático y consultas personalizadas.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    /** Buscar un usuario por correo */
    Optional<Usuario> findByCorreo(String correo);
    /** Verificar si un correo ya existe */
    boolean existsByCorreo(String correo);
}
