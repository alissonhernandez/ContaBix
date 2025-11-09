package com.contabix.contabix.controller;

import com.contabix.contabix.model.Rol;
import com.contabix.contabix.model.Usuario;
import com.contabix.contabix.repository.RolRepository;
import com.contabix.contabix.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository; // Repositorio para operaciones CRUD de Usuario

    @Autowired
    private RolRepository rolRepository; // Repositorio para operaciones CRUD de Rol

    /**
     * Mostrar el perfil del usuario que ha iniciado sesión.
     * Redirige al login si no hay usuario en sesión.
     */
    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login"; // Si no hay sesión, redirige a login
        }
        model.addAttribute("usuario", usuario);
        return "perfil"; // Retorna la vista perfil.html
    }

    /**
     * Mostrar el perfil de otro usuario (solo accesible por admin).
     * @param id ID del usuario a mostrar
     */
    @GetMapping("/perfilUsuario")
    public String perfilUsuario(@RequestParam Long id, HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion == null) {
            return "redirect:/login";
        }
        // Solo el admin puede ver perfiles de otros usuarios
        if (!usuarioSesion.getRol().getNombre().equalsIgnoreCase("admin")) {
            return "redirect:/inicio";
        }

        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return "redirect:/admin/usuarios"; // Redirige si el usuario no existe
        }
        model.addAttribute("usuario", usuario);
        return "perfil"; // Reutiliza la vista perfil.html
    }

    /**
     * Mostrar la página del Libro Diario.
     * Solo accesible si hay usuario en sesión.
     */
    @GetMapping("/librodiario")
    public String libroDiario(HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        return "librodiario";
    }

    /**
     * Mostrar la página del Libro Mayor.
     * Solo accesible si hay usuario en sesión.
     */
    @GetMapping("/libromayor")
    public String libroMayor(HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        return "libromayor";
    }

    /**
     * Listar todos los usuarios (solo admin).
     * Muestra además los roles disponibles para poder asignar.
     */
    @GetMapping("/admin/usuarios")
    public String listarUsuarios(HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion == null) {
            return "redirect:/login";
        }
        if (!usuarioSesion.getRol().getNombre().equalsIgnoreCase("admin")) {
            return "redirect:/inicio";
        }

        List<Usuario> usuarios = usuarioRepository.findAll();
        List<Rol> roles = rolRepository.findAll();
        model.addAttribute("usuario", usuarioSesion); // Usuario que inició sesión
        model.addAttribute("usuarios", usuarios);     // Lista de todos los usuarios
        model.addAttribute("roles", roles);           // Lista de roles para selección
        return "gestionar-usuarios"; // Vista para administrar usuarios
    }

    /**
     * Cambiar el rol de un usuario específico (solo admin).
     * @param id ID del usuario a modificar
     * @param rolId ID del nuevo rol
     */
    @PostMapping("/admin/usuarios/{id}/rol")
    public String cambiarRol(@PathVariable Long id,
                             @RequestParam Long rolId,
                             HttpSession session) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion == null || !usuarioSesion.getRol().getNombre().equalsIgnoreCase("admin")) {
            return "redirect:/inicio";
        }

        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        Rol nuevoRol = rolRepository.findById(rolId).orElse(null);

        // Actualiza rol solo si ambos existen
        if (usuario != null && nuevoRol != null) {
            usuario.setRol(nuevoRol);
            usuarioRepository.save(usuario);
        }
        return "redirect:/admin/usuarios"; // Redirige a la lista de usuarios
    }

    /**
     * Eliminar un usuario específico (solo admin).
     * @param id ID del usuario a eliminar
     */
    @PostMapping("/admin/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id, HttpSession session) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion == null || !usuarioSesion.getRol().getNombre().equalsIgnoreCase("admin")) {
            return "redirect:/inicio";
        }

        // Elimina usuario si existe
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@RequestParam Long id,
                                   @RequestParam String nombre,
                                   @RequestParam String apellido,
                                   @RequestParam String usuarioNombre,
                                   @RequestParam String correo,
                                   @RequestParam(required = false) String contrasenaNueva,
                                   @RequestParam(required = false) MultipartFile foto,
                                   HttpSession session) throws IOException {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion == null) {
            return "redirect:/login";
        }

        // Por seguridad, solo permite que el usuario edite su propio perfil
        if (!usuarioSesion.getId().equals(id)) {
            return "redirect:/inicio";
        }

        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return "redirect:/inicio";
        }

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setUsuario(usuarioNombre);
        usuario.setCorreo(correo);

        // Cambiar contraseña solo si se envía algo
        if (contrasenaNueva != null && !contrasenaNueva.isBlank()) {
            usuario.setContrasena(contrasenaNueva);
        }

        // Guardar foto si se envió
        if (foto != null && !foto.isEmpty()) {
            String uploadsDir = "src/main/resources/static/uploads";
            Path uploadPath = Paths.get(uploadsDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = usuario.getId() + "_" + foto.getOriginalFilename();
            try (InputStream inputStream = foto.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            usuario.setFoto(fileName);
        }

        usuarioRepository.save(usuario);

        // Actualizar el usuario en sesión con los nuevos datos
        session.setAttribute("usuario", usuario);

        return "redirect:/perfil";
    }

}
