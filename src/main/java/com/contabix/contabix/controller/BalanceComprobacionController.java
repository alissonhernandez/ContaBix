package com.contabix.contabix.controller;

import com.contabix.contabix.service.BalanceComprobacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BalanceComprobacionController {

    private final BalanceComprobacionService service;

    public BalanceComprobacionController(BalanceComprobacionService service) {
        this.service = service;
    }

    @GetMapping("/balance-comprobacion")
    public String verBalance(Model model) {
        model.addAttribute("balance", service.generarBalance());
        return "balance-comprobacion";
    }
}

