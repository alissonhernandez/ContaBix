package com.contabix.contabix.controller;

import com.contabix.contabix.model.Usuario;
import com.contabix.contabix.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/registro")
    public String registroForm() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombre,
                           @RequestParam String correo,
                           @RequestParam String contrasena,
                           Model model) {

        // Validaciones básicas
        if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            model.addAttribute("error", "Todos los campos son obligatorios");
            return "registro";
        }

        // Validar formato de correo
        if (!correo.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            model.addAttribute("error", "Correo inválido. Ejemplo: usuario@dominio.com");
            return "registro";
        }

        // Validar contraseña
        if (contrasena.length() < 6) {
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres. Ejemplo: abc123");
            return "registro";
        }

        // Verificar si el correo ya está registrado
        if (usuarioService.existsByCorreo(correo)) {
            model.addAttribute("error", "El correo ya está registrado. Intenta con otro correo");
            return "registro";
        }

        try {
            // Crear usuario y registrar
            Usuario nuevo = new Usuario();
            nuevo.setNombre(nombre);
            nuevo.setCorreo(correo);
            nuevo.setContrasena(contrasena);
            usuarioService.registrar(nuevo);

            model.addAttribute("mensaje", "Usuario registrado correctamente. Ahora puedes iniciar sesión");
            return "login";

        } catch (Exception e) {
            // Mensaje de error general si algo falla
            model.addAttribute("error", "No se pudo registrar el usuario. Intenta nuevamente");
            return "registro";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam String correo,
                        @RequestParam String contrasena,
                        Model model) {

        if (correo.isBlank() || contrasena.isBlank()) {
            model.addAttribute("error", "Correo y contraseña son obligatorios");
            return "login";
        }

        Optional<Usuario> usuario = usuarioService.login(correo, contrasena);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
            model.addAttribute("mensaje", "Bienvenido " + usuario.get().getNombre());
            return "inicio"; // Página de inicio después del login
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
        }
    }
}
