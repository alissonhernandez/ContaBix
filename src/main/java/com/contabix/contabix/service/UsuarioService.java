package com.contabix.contabix.service;

import com.contabix.contabix.model.Usuario;
import com.contabix.contabix.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registrar usuario con contraseña cifrada
    public Usuario registrar(Usuario usuario) {
        String hashedPassword = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(hashedPassword);
        return usuarioRepository.save(usuario);
    }

    // Login comparando contraseña cifrada
    public Optional<Usuario> login(String correo, String contrasena) {
        Optional<Usuario> usuario = usuarioRepository.findByCorreo(correo);
        if (usuario.isPresent()) {
            if (passwordEncoder.matches(contrasena, usuario.get().getContrasena())) {
                return usuario;
            }
        }
        return Optional.empty();
    }

    // Verifica si un correo ya existe
    public boolean existsByCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }
}
