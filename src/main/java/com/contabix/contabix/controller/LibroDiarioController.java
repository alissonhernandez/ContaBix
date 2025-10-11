package com.contabix.contabix.controller;

import com.contabix.contabix.model.Asiento;
import com.contabix.contabix.repository.AsientoRepository;
import com.contabix.contabix.service.AsientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/libro-diario")
public class LibroDiarioController {

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private AsientoService asientoService;

    @GetMapping
    public String listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            @RequestParam(required = false) String q,
            Model model) {

        List<Asiento> asientos;

        if (inicio != null && fin != null) {
            asientos = asientoRepository.findByFechaBetween(inicio, fin);
        } else if (q != null && !q.isBlank()) {
            asientos = asientoRepository.findByDescripcionContainingIgnoreCase(q);
        } else {
            asientos = asientoRepository.findAll();
        }

        model.addAttribute("asientos", asientos);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fin", fin);
        model.addAttribute("q", q);
        return "libro-diario-list";
    }

    @GetMapping("/nuevo")
    public String nuevoAsiento(Model model) {
        Asiento asiento = new Asiento();
        model.addAttribute("asiento", asiento);
        return "libro-diario-form";
    }

    @PostMapping("/guardar")
    public String guardarAsiento(@ModelAttribute("asiento") Asiento asiento,
                                 BindingResult bindingResult, Model model) {
        try {
            asientoService.guardarAsientoConValidacion(asiento);
            return "redirect:/libro-diario?success=1";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("asiento", asiento);
            return "libro-diario-form";
        } catch (Exception ex) {
            model.addAttribute("error", "Error al guardar asiento: " + ex.getMessage());
            model.addAttribute("asiento", asiento);
            return "libro-diario-form";
        }
    }
}
