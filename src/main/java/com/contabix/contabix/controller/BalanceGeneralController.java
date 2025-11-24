package com.contabix.contabix.controller;

import com.contabix.contabix.dto.BalanceGeneralDTO;
import com.contabix.contabix.service.BalanceGeneralService;
import com.contabix.contabix.service.ExportService;
import com.contabix.contabix.util.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class BalanceGeneralController {

    @Autowired
    private BalanceGeneralService balanceGeneralService;

    @Autowired
    private ExportService exportService;

    @GetMapping("/balance-general")
    public String mostrarBalanceGeneral(Model model, HttpSession session) {

        if (!SecurityUtils.tieneRol(session, "admin", "contador")) {
            return "redirect:/inicio";
        }

        Map<String, List<BalanceGeneralDTO>> balance =
                balanceGeneralService.generarBalanceGeneral();

        List<BalanceGeneralDTO> activos = balance.get("activos");
        List<BalanceGeneralDTO> pasivos = balance.get("pasivos");
        List<BalanceGeneralDTO> patrimonio = balance.get("patrimonio");

        // Calcular totales
        double totalActivos = activos.stream().mapToDouble(BalanceGeneralDTO::getSaldo).sum();
        double totalPasivos = pasivos.stream().mapToDouble(BalanceGeneralDTO::getSaldo).sum();
        double totalPatrimonio = patrimonio.stream().mapToDouble(BalanceGeneralDTO::getSaldo).sum();

        model.addAttribute("activos", activos);
        model.addAttribute("pasivos", pasivos);
        model.addAttribute("patrimonio", patrimonio);

        model.addAttribute("totalActivos", totalActivos);
        model.addAttribute("totalPasivos", totalPasivos);
        model.addAttribute("totalPatrimonio", totalPatrimonio);

        // Opcional: verificar balance cuadrado
        double totalPasivosPatrimonio = totalPasivos + totalPatrimonio;
        model.addAttribute("totalPasivosPatrimonio", totalPasivosPatrimonio);

        return "balance-general";
    }


    @GetMapping("/balance-general/export/pdf")
    public void exportarPdf(HttpServletResponse response) throws Exception {

        List<BalanceGeneralDTO> balance = balanceGeneralService.obtenerBalanceGeneral();

        byte[] pdf = exportService.generarPdfBalanceGeneral(balance);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=balance_general.pdf");
        response.getOutputStream().write(pdf);
    }
}

