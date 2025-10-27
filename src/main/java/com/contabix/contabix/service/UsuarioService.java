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
    private UsuarioRepository usuarioRepository; // Repositorio para operaciones CRUD de Usuario

    @Autowired
    private RolRepository rolRepository; // Repositorio para operaciones CRUD de Rol

    @Autowired
    private PasswordEncoder passwordEncoder; // Para cifrar y verificar contraseñas

    /**
     * Registrar un usuario nuevo.
     * La contraseña se cifra y se asigna el rol "auditor" por defecto.
     * @param usuario Usuario a registrar
     * @return Usuario guardado en la base de datos
     */
    public Usuario registrar(Usuario usuario) {
        // Cifrar la contraseña antes de guardar
        String hashedPassword = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(hashedPassword);

        // Asignar rol "auditor" por defecto
        Rol rolAuditor = rolRepository.findByNombre("auditor")
                .orElseThrow(() -> new RuntimeException("Rol 'auditor' no encontrado"));
        usuario.setRol(rolAuditor);

        return usuarioRepository.save(usuario); // Guardar usuario en BD
    }

    /**
     * Login de usuario comparando la contraseña cifrada.
     * @param correo Correo del usuario
     * @param contrasena Contraseña ingresada
     * @return Optional<Usuario> si las credenciales son correctas, vacío si no
     */
    public Optional<Usuario> login(String correo, String contrasena) {
        Optional<Usuario> usuario = usuarioRepository.findByCorreo(correo);
        // Verifica que la contraseña ingresada coincida con la cifrada en la BD
        if (usuario.isPresent() && passwordEncoder.matches(contrasena, usuario.get().getContrasena())) {
            return usuario;
        }
        return Optional.empty();
    }

    /**
     * Verifica si un correo ya existe en la base de datos.
     * @param correo Correo a verificar
     * @return true si existe, false si no
     */
    public boolean existsByCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    /**
     * Buscar usuario por ID.
     * @param id ID del usuario
     * @return Optional<Usuario> que puede estar vacío si no existe
     */
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Eliminar un usuario por ID.
     * @param id ID del usuario a eliminar
     */
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario registrarComoAdmin(Usuario usuario) {
        String hashedPassword = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(hashedPassword);

        Rol rolAdmin = rolRepository.findByNombre("admin")
                .orElseThrow(() -> new RuntimeException("Rol 'admin' no encontrado"));

        usuario.setRol(rolAdmin);
        return usuarioRepository.save(usuario);
    }

}
