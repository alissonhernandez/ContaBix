package com.contabix.contabix.controller;

import com.contabix.contabix.model.LibroMayor;
import com.contabix.contabix.service.LibroMayorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class LibroMayorController {
    private final LibroMayorService service;

    public LibroMayorController(LibroMayorService service) {
        this.service = service;
    }

    @GetMapping("/libro-mayor")
    public String mostrarLibroMayor(Model model) {
        List<LibroMayor> registros = service.listarTodos();
        model.addAttribute("registros", registros);
        return "libro-mayor";
    }
}