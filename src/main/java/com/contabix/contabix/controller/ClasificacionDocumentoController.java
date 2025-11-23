package com.contabix.contabix.controller;

import com.contabix.contabix.model.ClasificacionDocumento;
import com.contabix.contabix.service.ClasificacionDocumentoService;
import com.contabix.contabix.util.SecurityUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clasificaciones")
public class ClasificacionDocumentoController {

    @Autowired
    private ClasificacionDocumentoService service;

    // LISTA - SOLO ADMIN
    @GetMapping
    public String listar(Model model,
                         HttpSession session,
                         RedirectAttributes ra) {

        if (!SecurityUtils.tieneRol(session, "admin")) {
            ra.addFlashAttribute("error", "No tienes permiso para acceder a Clasificaciones.");
            return "redirect:/inicio";
        }

        model.addAttribute("clasificaciones", service.listar());
        return "clasificaciones/lista";
    }

    // NUEVO - SOLO ADMIN
    @GetMapping("/nuevo")
    public String nuevo(Model model,
                        HttpSession session,
                        RedirectAttributes ra) {

        if (!SecurityUtils.tieneRol(session, "admin")) {
            ra.addFlashAttribute("error", "No tienes permiso para crear clasificaciones.");
            return "redirect:/clasificaciones";
        }

        model.addAttribute("clasificacion", new ClasificacionDocumento());
        model.addAttribute("titulo", "Nueva Clasificación");
        return "clasificaciones/form";
    }

    // EDITAR - SOLO ADMIN
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id,
                         Model model,
                         HttpSession session,
                         RedirectAttributes ra) {

        if (!SecurityUtils.tieneRol(session, "admin")) {
            ra.addFlashAttribute("error", "No tienes permiso para editar clasificaciones.");
            return "redirect:/clasificaciones";
        }

        ClasificacionDocumento c = service.buscarPorId(id);

        if (c == null) {
            ra.addFlashAttribute("error", "La clasificación no existe");
            return "redirect:/clasificaciones";
        }

        model.addAttribute("clasificacion", c);
        model.addAttribute("titulo", "Editar Clasificación");
        return "clasificaciones/form";
    }

    // GUARDAR - SOLO ADMIN
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("clasificacion") ClasificacionDocumento c,
                          HttpSession session,
                          RedirectAttributes ra) {

        if (!SecurityUtils.tieneRol(session, "admin")) {
            ra.addFlashAttribute("error", "No tienes permiso para guardar clasificaciones.");
            return "redirect:/clasificaciones";
        }

        service.guardar(c);
        ra.addFlashAttribute("success", "Clasificación guardada correctamente");
        return "redirect:/clasificaciones";
    }

    // ELIMINAR - SOLO ADMIN
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id,
                           HttpSession session,
                           RedirectAttributes ra) {

        if (!SecurityUtils.tieneRol(session, "admin")) {
            ra.addFlashAttribute("error", "No tienes permiso para eliminar clasificaciones.");
            return "redirect:/clasificaciones";
        }

        try {
            service.eliminar(id);
            ra.addFlashAttribute("success", "Clasificación eliminada correctamente");
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            ra.addFlashAttribute("error",
                    "No se puede eliminar la clasificación porque está siendo utilizada por uno o más documentos fuente.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Ocurrió un error al eliminar la clasificación.");
        }
        return "redirect:/clasificaciones";
    }

}
