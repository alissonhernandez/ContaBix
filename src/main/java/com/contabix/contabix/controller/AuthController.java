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
                           Model model) {

        // Validar que los campos no estén vacíos
        if (nombre.isBlank() || apellido.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            model.addAttribute("error", "Todos los campos son obligatorios");
            return "registro"; // Retorna al formulario si falta algún campo
        }

        // Validar formato del correo electrónico
        if (!correo.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            model.addAttribute("error", "Correo inválido");
            return "registro";
        }

        // Validar que la contraseña tenga al menos 6 caracteres
        if (contrasena.length() < 6) {
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres");
            return "registro";
        }

        // Verificar si el correo ya está registrado
        if (usuarioService.existsByCorreo(correo)) {
            model.addAttribute("error", "El correo ya está registrado");
            return "registro";
        }

        try {
            // Crear un nuevo objeto Usuario y asignarle los datos del formulario
            Usuario nuevo = new Usuario();
            nuevo.setNombre(nombre);
            nuevo.setApellido(apellido);
            nuevo.setUsuario(usuario);
            nuevo.setCorreo(correo);
            nuevo.setContrasena(contrasena);

            // Guardar el usuario en la base de datos
            usuarioService.registrar(nuevo);

            // Mensaje de éxito
            model.addAttribute("mensaje", "Usuario registrado correctamente");
            return "login"; // Redirige al login para que el usuario inicie sesión
        } catch (Exception e) {
            // Manejo de error si falla el registro
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
