package com.contabix.contabix.service;

import com.contabix.contabix.model.Rol;
import com.contabix.contabix.model.Usuario;
import com.contabix.contabix.repository.RolRepository;
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
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registrar usuario con rol AUDITOR por defecto
    public Usuario registrar(Usuario usuario) {
        String hashedPassword = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(hashedPassword);

        Rol rolAuditor = rolRepository.findByNombre("auditor")
                .orElseThrow(() -> new RuntimeException("Rol 'auditor' no encontrado"));
        usuario.setRol(rolAuditor);

        return usuarioRepository.save(usuario);
    }

    // Login comparando contraseña cifrada
    public Optional<Usuario> login(String correo, String contrasena) {
        Optional<Usuario> usuario = usuarioRepository.findByCorreo(correo);
        if (usuario.isPresent() && passwordEncoder.matches(contrasena, usuario.get().getContrasena())) {
            return usuario;
        }
        return Optional.empty();
    }

    public boolean existsByCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
}
