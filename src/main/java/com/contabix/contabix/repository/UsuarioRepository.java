package com.contabix.contabix.repository;

import com.contabix.contabix.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);

    // Método agregado para validar existencia de correo
    boolean existsByCorreo(String correo);
}
