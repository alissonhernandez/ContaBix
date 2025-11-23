package com.contabix.contabix.controller;

import com.contabix.contabix.model.LibroMayor;
import com.contabix.contabix.service.LibroMayorService;
import com.contabix.contabix.util.SecurityUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class LibroMayorController {

    private final LibroMayorService service;

    public LibroMayorController(LibroMayorService service) {
        this.service = service;
    }

    @GetMapping("/libro-mayor")
    public String mostrarLibroMayor(Model model,
                                    HttpSession session,
                                    RedirectAttributes ra) {

        if (!SecurityUtils.tieneRol(session, "admin", "contador")) {
            ra.addFlashAttribute("error", "No tienes permiso para acceder al Libro Mayor.");
            return "redirect:/inicio";
        }

        List<LibroMayor> registros = service.listarTodos();
        model.addAttribute("registros", registros);
        return "libro-mayor";
    }
}
