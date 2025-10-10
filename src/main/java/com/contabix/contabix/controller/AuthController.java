package com.contabix.contabix.controller;

import com.contabix.contabix.model.Usuario;
import com.contabix.contabix.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    // --- Formulario de login ---
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // --- Formulario de registro ---
    @GetMapping("/registro")
    public String registroForm() {
        return "registro";
    }

    // --- Registro de usuario ---
    @PostMapping("/registro")
    public String registro(@RequestParam String nombre,
                           @RequestParam String apellido,
                           @RequestParam String usuario,
                           @RequestParam String correo,
                           @RequestParam String contrasena,
                           Model model) {

        // Validación de campos vacíos
        if (nombre.isBlank() || apellido.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            model.addAttribute("error", "Todos los campos son obligatorios");
            return "registro";
        }

        // Validación de correo
        if (!correo.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            model.addAttribute("error", "Correo inválido");
            return "registro";
        }

        // Validación de contraseña
        if (contrasena.length() < 6) {
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres");
            return "registro";
        }

        // Verificar si el correo ya existe
        if (usuarioService.existsByCorreo(correo)) {
            model.addAttribute("error", "El correo ya está registrado");
            return "registro";
        }

        try {
            // Crear usuario y guardar
            Usuario nuevo = new Usuario();
            nuevo.setNombre(nombre);
            nuevo.setApellido(apellido);
            nuevo.setUsuario(usuario);
            nuevo.setCorreo(correo);
            nuevo.setContrasena(contrasena);

            usuarioService.registrar(nuevo);

            model.addAttribute("mensaje", "Usuario registrado correctamente");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "No se pudo registrar el usuario");
            return "registro";
        }
    }

    // --- Login ---
    @PostMapping("/login")
    public String login(@RequestParam String correo,
                        @RequestParam String contrasena,
                        HttpSession session,
                        Model model) {

        Optional<Usuario> usuario = usuarioService.login(correo, contrasena);
        if (usuario.isPresent()) {
            session.setAttribute("usuario", usuario.get());
            return "redirect:/inicio";
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
        }
    }

    // --- Página de inicio ---
    @GetMapping("/inicio")
    public String inicio(HttpSession session, Model model,
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

    // --- Logout ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
