package com.contabix.contabix.controller;

import com.contabix.contabix.model.Rol;
import com.contabix.contabix.model.Usuario;
import com.contabix.contabix.repository.RolRepository;
import com.contabix.contabix.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    // Mostrar el perfil del usuario en sesión
    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    // Mostrar perfil de otro usuario (solo admin)
    @GetMapping("/perfilUsuario")
    public String perfilUsuario(@RequestParam Long id, HttpSession session, Model model) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion == null) {
            return "redirect:/login";
        }
        if (!usuarioSesion.getRol().getNombre().equalsIgnoreCase("admin")) {
            return "redirect:/inicio";
        }

        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return "redirect:/admin/usuarios";
        }
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    // Rutas antiguas, solo validan sesión
    @GetMapping("/librodiario")
    public String libroDiario(HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        return "librodiario";
    }

    @GetMapping("/libromayor")
    public String libroMayor(HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        return "libromayor";
    }

    // Listar usuarios (solo admin)
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
        model.addAttribute("usuario", usuarioSesion);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("roles", roles);
        return "gestionar-usuarios";
    }

    // Cambiar rol (solo admin)
    @PostMapping("/admin/usuarios/{id}/rol")
    public String cambiarRol(@PathVariable Long id,
                             @RequestParam Long rolId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion == null || !usuarioSesion.getRol().getNombre().equalsIgnoreCase("admin")) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para realizar esta acción.");
            return "redirect:/inicio";
        }

        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        Rol nuevoRol = rolRepository.findById(rolId).orElse(null);

        if (usuario == null || nuevoRol == null) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el rol. Verifica los datos.");
            return "redirect:/admin/usuarios";
        }

        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("mensaje",
                "Rol actualizado correctamente para " + usuario.getNombre() + ".");
        return "redirect:/admin/usuarios";
    }

    // Eliminar usuario (solo admin)
    @PostMapping("/admin/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion == null || !usuarioSesion.getRol().getNombre().equalsIgnoreCase("admin")) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para realizar esta acción.");
            return "redirect:/inicio";
        }

        if (!usuarioRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "El usuario que intentas eliminar no existe.");
            return "redirect:/admin/usuarios";
        }

        usuarioRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente.");
        return "redirect:/admin/usuarios";
    }

    // Actualizar perfil (solo el propio usuario)
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@RequestParam Long id,
                                   @RequestParam String nombre,
                                   @RequestParam String apellido,
                                   @RequestParam String usuarioNombre,
                                   @RequestParam String correo,
                                   @RequestParam(required = false) String contrasenaNueva,
                                   @RequestParam(required = false) MultipartFile foto,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) throws IOException {

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion == null) {
            redirectAttributes.addFlashAttribute("error", "La sesión ha expirado. Inicia sesión nuevamente.");
            return "redirect:/login";
        }

        if (!usuarioSesion.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("error", "No puedes modificar el perfil de otro usuario.");
            return "redirect:/inicio";
        }

        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "El usuario no existe.");
            return "redirect:/inicio";
        }

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setUsuario(usuarioNombre);
        usuario.setCorreo(correo);

        if (contrasenaNueva != null && !contrasenaNueva.isBlank()) {
            usuario.setContrasena(contrasenaNueva);
        }

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
        session.setAttribute("usuario", usuario);

        redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente.");
        return "redirect:/perfil";
    }
}
