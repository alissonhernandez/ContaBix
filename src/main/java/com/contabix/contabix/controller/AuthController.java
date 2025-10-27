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
    // Servicio que maneja la lógica de usuarios: registro, login y consultas a la base de datos

    // --- Mostrar formulario de login ---
    @GetMapping("/login")
    public String loginForm() {
        // Retorna la vista login.html para que el usuario ingrese sus credenciales
        return "login";
    }

    // --- Mostrar formulario de registro ---
    @GetMapping("/registro")
    public String registroForm() {
        // Retorna la vista registro.html para crear un nuevo usuario
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

            // Si elige ser administrador, validar clave especial
            if ("si".equalsIgnoreCase(esAdmin)) {
                if (!"claveAdminContabix".equals(claveAdmin)) { // clave fija para admin
                    model.addAttribute("error", "Contraseña de administrador incorrecta");
                    return "registro";
                }
                usuarioService.registrarComoAdmin(nuevo);
            } else {
                usuarioService.registrar(nuevo); // Por defecto se registra como auditor
            }

            model.addAttribute("mensaje", "Usuario registrado correctamente");
            return "login";

        } catch (Exception e) {
            model.addAttribute("error", "No se pudo registrar el usuario");
            return "registro";
        }
    }


    // --- Procesar login de usuario ---
    @PostMapping("/login")
    public String login(@RequestParam String correo,
                        @RequestParam String contrasena,
                        HttpSession session,
                        Model model) {

        // Intentar iniciar sesión con el correo y contraseña proporcionados
        Optional<Usuario> usuario = usuarioService.login(correo, contrasena);

        if (usuario.isPresent()) {
            // Guardar usuario en sesión para mantener el login activo
            session.setAttribute("usuario", usuario.get());
            return "redirect:/inicio"; // Redirige a la página de inicio
        } else {
            // Si las credenciales son incorrectas, mostrar error
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login"; // Retorna al formulario de login
        }
    }

    // --- Mostrar página de inicio ---
    @GetMapping("/inicio")
    public String inicio(HttpSession session, Model model,
                         @RequestParam(required = false) String mensaje) {

        // Obtener usuario actual desde la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Si no hay usuario logueado, redirigir al login
        if (usuario == null) {
            return "redirect:/login";
        }

        // Pasar usuario a la vista para mostrar información personalizada
        model.addAttribute("usuario", usuario);

        // Mostrar mensaje opcional (por ejemplo, "Registro exitoso")
        if (mensaje != null) {
            model.addAttribute("mensaje", mensaje);
        }

        return "inicio"; // Retorna la vista inicio.html
    }

    // --- Cerrar sesión ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalida la sesión para cerrar sesión del usuario
        session.invalidate();
        return "redirect:/login"; // Redirige al login
    }
}
