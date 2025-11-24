package com.contabix.contabix.controller;

import com.contabix.contabix.model.PeriodoContable;
import com.contabix.contabix.service.PeriodoContableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/periodos")
public class PeriodoContableController {

    @Autowired
    private PeriodoContableService service;

    @GetMapping
    public String listarPeriodos(Model model,
                                 @RequestParam(value = "error", required = false) String error) {
        List<PeriodoContable> periodos = service.listar();
        model.addAttribute("periodos", periodos);
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "periodos-contables";
    }

    @PostMapping("/crear")
    public String crearPeriodo(@RequestParam("inicio") String inicioStr,
                               @RequestParam("fin") String finStr,
                               Model model) {
        try {
            LocalDate inicio = LocalDate.parse(inicioStr);
            LocalDate fin = LocalDate.parse(finStr);
            service.crearPeriodo(inicio, fin);
        } catch (IllegalStateException e) {
            // Redirigimos con parámetro de error
            return "redirect:/periodos?error=" + e.getMessage().replace(" ", "%20");
        } catch (Exception e) {
            return "redirect:/periodos?error=Error inesperado al crear el periodo";
        }
        return "redirect:/periodos";
    }

    @GetMapping("/cerrar/{id}")
    public String cerrar(@PathVariable Integer id) {
        service.cerrarPeriodo(id);
        return "redirect:/periodos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPeriodo(@PathVariable Integer id) {
        try {
            service.eliminarPeriodo(id);
        } catch (IllegalStateException e) {
            // Redirigir con mensaje de error
            return "redirect:/periodos?error=" + e.getMessage().replace(" ", "%20");
        } catch (Exception e) {
            return "redirect:/periodos?error=Error inesperado al eliminar el periodo";
        }
        return "redirect:/periodos";
    }

}

