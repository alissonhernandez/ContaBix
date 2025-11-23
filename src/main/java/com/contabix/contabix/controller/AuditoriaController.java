package com.contabix.contabix.controller;

import com.contabix.contabix.repository.AuditoriaRepository;
import com.contabix.contabix.util.SecurityUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaController(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @GetMapping
    public String listar(Model model,
                         HttpSession session,
                         RedirectAttributes ra) {

        if (!SecurityUtils.tieneRol(session, "admin", "auditor")) {
            ra.addFlashAttribute("error", "No tienes permiso para ver la Auditoría.");
            return "redirect:/inicio";
        }

        model.addAttribute("registros", auditoriaRepository.findAll());
        return "auditoria-list";
    }
}
