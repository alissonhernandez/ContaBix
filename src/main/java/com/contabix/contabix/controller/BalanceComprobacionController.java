package com.contabix.contabix.controller;

import com.contabix.contabix.service.BalanceComprobacionService;
import com.contabix.contabix.util.SecurityUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BalanceComprobacionController {

    private final BalanceComprobacionService service;

    public BalanceComprobacionController(BalanceComprobacionService service) {
        this.service = service;
    }

    @GetMapping("/balance-comprobacion")
    public String verBalance(Model model,
                             HttpSession session,
                             RedirectAttributes ra) {

        if (!SecurityUtils.tieneRol(session, "admin", "contador")) {
            ra.addFlashAttribute("error", "No tienes permiso para acceder al Balance de Comprobación.");
            return "redirect:/inicio";
        }

        model.addAttribute("balance", service.generarBalance());
        return "balance-comprobacion";
    }
}
