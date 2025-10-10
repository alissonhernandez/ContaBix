package com.contabix.contabix.controller;

import com.contabix.contabix.model.Rol;
import com.contabix.contabix.model.Usuario;
import com.contabix.contabix.repository.RolRepository;
import com.contabix.contabix.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    // Mostrar perfil propio
    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    // Mostrar perfil de otros usuario (solo admin)
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

    // Página Libro Diario
    @GetMapping("/librodiario")
    public String libroDiario(HttpSession session) {
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        return "librodiario";
    }

    // Página Libro Mayor
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

    // Cambiar rol
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

        if (usuario != null && nuevoRol != null) {
            usuario.setRol(nuevoRol);
            usuarioRepository.save(usuario);
        }
        return "redirect:/admin/usuarios";
    }

    // Eliminar usuario
    @PostMapping("/admin/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id, HttpSession session) {
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioSesion == null || !usuarioSesion.getRol().getNombre().equalsIgnoreCase("admin")) {
            return "redirect:/inicio";
        }

        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
        }
        return "redirect:/admin/usuarios";
    }
}
