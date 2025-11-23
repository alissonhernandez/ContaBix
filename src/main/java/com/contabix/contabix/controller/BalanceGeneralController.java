package com.contabix.contabix.controller;

import com.contabix.contabix.dto.BalanceGeneralDTO;
import com.contabix.contabix.service.BalanceGeneralService;
import com.contabix.contabix.util.SecurityUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class BalanceGeneralController {

    private final BalanceGeneralService service;

    public BalanceGeneralController(BalanceGeneralService service) {
        this.service = service;
    }

    @GetMapping("/balance-general")
    public String mostrarBalanceGeneral(Model model, HttpSession session) {

        // Solo admin y contador pueden ver el balance
        if (!SecurityUtils.tieneRol(session, "admin", "contador")) {
            return "redirect:/inicio";
        }

        Map<String, List<BalanceGeneralDTO>> balance = service.generarBalanceGeneral();

        model.addAttribute("activos", balance.get("activos"));
        model.addAttribute("pasivos", balance.get("pasivos"));
        model.addAttribute("patrimonio", balance.get("patrimonio"));

        return "balance-general";
    }
}
