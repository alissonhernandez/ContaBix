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

    @PostMapping("/login")
    public String login(@RequestParam String correo, @RequestParam String contrasena, Model model) {
        Optional<Usuario> usuario = usuarioService.login(correo, contrasena);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
            return "inicio"; // Página de inicio después del login
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "login";
        }
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
        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setCorreo(correo);
        nuevo.setContrasena(contrasena);
        usuarioService.registrar(nuevo);

        model.addAttribute("mensaje", "Usuario registrado con éxito");
        return "login";
    }
}
