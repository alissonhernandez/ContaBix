package com.contabix.contabix.controller;

import com.contabix.contabix.dto.EstadoResultadosDTO;
import com.contabix.contabix.service.EstadoResultadosService;
import com.contabix.contabix.util.SecurityUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class EstadoResultadosController {

    @Autowired
    private EstadoResultadosService estadoResultadosService;

    public EstadoResultadosController(EstadoResultadosService estadoResultadosService) {
        this.estadoResultadosService = estadoResultadosService;
    }

    @GetMapping("/estado-resultados")
    public String estadoResultados(Model model,
                                   HttpSession session,
                                   RedirectAttributes ra) {

        if (!SecurityUtils.tieneRol(session, "admin", "contador")) {
            ra.addFlashAttribute("error", "No tienes permiso para acceder al Estado de Resultados.");
            return "redirect:/inicio";
        }

        EstadoResultadosDTO estado = estadoResultadosService.calcularEstadoResultados();
        model.addAttribute("estado", estado);
        return "estado-resultados";
    }
}
