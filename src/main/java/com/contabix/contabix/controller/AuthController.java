package com.contabix.contabix.controller;

import com.contabix.contabix.model.Usuario;
import com.contabix.contabix.service.AuditoriaService;
import com.contabix.contabix.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuditoriaService auditoriaService;   // 👈 importante para registrar LOGIN

    // --- Mostrar formulario de login ---
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // --- Mostrar formulario de registro ---
    @GetMapping("/registro")
    public String registroForm() {
        return "registro";
    }

    // --- Procesar registro de usuario ---
    @PostMapping("/registro")
    public String registro(@RequestParam String nombre,
                           @RequestParam String apellido,
                           @RequestParam String usuario,
                           @RequestParam String correo,
                           @RequestParam String contrasena,
                           @RequestParam String esAdmin,
                           @RequestParam(required = false) String claveAdmin,
                           Model model) {

        if (nombre.isBlank() || apellido.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            model.addAttribute("error", "Todos los campos son obligatorios");
            return "registro";
        }

        if (!correo.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            model.addAttribute("error", "Correo inválido");
            return "registro";
        }

        if (contrasena.length() < 6) {
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres");
            return "registro";
        }

        if (usuarioService.existsByCorreo(correo)) {
            model.addAttribute("error", "El correo ya está registrado");
            return "registro";
        }

        try {
            Usuario nuevo = new Usuario();
            nuevo.setNombre(nombre);
            nuevo.setApellido(apellido);
            nuevo.setUsuario(usuario);
            nuevo.setCorreo(correo);
            nuevo.setContrasena(contrasena);

            if ("si".equalsIgnoreCase(esAdmin)) {
                if (!"claveAdminContabix".equals(claveAdmin)) {
                    model.addAttribute("error", "Contraseña de administrador incorrecta");
                    return "registro";
                }
                usuarioService.registrarComoAdmin(nuevo);
            } else {
                usuarioService.registrar(nuevo);  // se registra como auditor por defecto
            }

            model.addAttribute("mensaje", "Usuario registrado correctamente");
            return "login";

        } catch (Exception e) {
            model.addAttribute("error", "No se pudo registrar el usuario");
            return "registro";
        }
    }

    // --- Procesar login de usuario + AUDITORÍA ---
    @PostMapping("/login")
    public String login(@RequestParam String correo,
                        @RequestParam String contrasena,
                        HttpSession session,
                        Model model) {

        Optional<Usuario> usuarioOpt = usuarioService.login(correo, contrasena);

        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
        }

        Usuario usuario = usuarioOpt.get();
        session.setAttribute("usuario", usuario);

        // Registrar en bitácora
        auditoriaService.registrar(
                usuario,
                "LOGIN",
                "El usuario inició sesión."
        );

        return "redirect:/inicio";
    }

    // --- Página de inicio ---
    @GetMapping("/inicio")
    public String inicio(HttpSession session,
                         Model model,
                         @RequestParam(required = false) String mensaje) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);

        if (mensaje != null) {
            model.addAttribute("mensaje", mensaje);
        }

        return "inicio";
    }

    // --- Cerrar sesión ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
