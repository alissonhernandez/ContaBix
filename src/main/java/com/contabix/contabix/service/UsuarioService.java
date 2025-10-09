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
    private PasswordEncoder passwordEncoder; // Inyectamos el encoder

    // Registrar usuario con contraseña cifrada
    public Usuario registrar(Usuario usuario) {
        // Ciframos la contraseña antes de guardarla
        String hashedPassword = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(hashedPassword);
        return usuarioRepository.save(usuario);
    }

    // Login comparando la contraseña cifrada
    public Optional<Usuario> login(String correo, String contrasena) {
        Optional<Usuario> usuario = usuarioRepository.findByCorreo(correo);

        if (usuario.isPresent()) {
            // Comparamos la contraseña ingresada con la cifrada en DB
            if (passwordEncoder.matches(contrasena, usuario.get().getContrasena())) {
                return usuario;
            }
        }
        return Optional.empty();
    }
}

