package com.contabix.contabix.controller;

import com.contabix.contabix.model.CuentaContable;
import com.contabix.contabix.model.Usuario;
import com.contabix.contabix.repository.CuentaContableRepository;
import com.contabix.contabix.service.AuditoriaService;
import com.contabix.contabix.util.SecurityUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/catalogo-cuentas")
public class CuentaContableController {

    private final CuentaContableRepository cuentaRepo;
    private final AuditoriaService auditoriaService;

    public CuentaContableController(CuentaContableRepository cuentaRepo,
                                    AuditoriaService auditoriaService) {
        this.cuentaRepo = cuentaRepo;
        this.auditoriaService = auditoriaService;
    }

    // LISTAR CUENTAS (admin + contador)
    @GetMapping
    public String listar(Model model,
                         HttpSession session,
                         RedirectAttributes ra) {

        if (!SecurityUtils.tieneRol(session, "admin", "contador")) {
            ra.addFlashAttribute("error", "No tienes permiso para ver el catálogo de cuentas.");
            return "redirect:/inicio";
        }

        List<CuentaContable> cuentas = cuentaRepo.findAll();
        model.addAttribute("cuentas", cuentas);
        return "catalogo-cuentas-list";
    }

    // FORMULARIO NUEVA CUENTA (admin + contador)
    @GetMapping("/nuevo")
    public String nuevo(Model model,
                        HttpSession session,
                        RedirectAttributes ra) {

        if (!SecurityUtils.tieneRol(session, "admin", "contador")) {
            ra.addFlashAttribute("error", "No tienes permiso para crear cuentas.");
            return "redirect:/catalogo-cuentas";
        }

        model.addAttribute("cuenta", new CuentaContable());
        model.addAttribute("titulo", "Nueva Cuenta Contable");
        return "catalogo-cuentas-form";
    }

    // FORMULARIO EDITAR CUENTA (admin + contador)
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id,
                         Model model,
                         HttpSession session,
                         RedirectAttributes ra) {

        if (!SecurityUtils.tieneRol(session, "admin", "contador")) {
            ra.addFlashAttribute("error", "No tienes permiso para editar cuentas.");
            return "redirect:/catalogo-cuentas";
        }

        CuentaContable cuenta = cuentaRepo.findById(id).orElse(null);
        if (cuenta == null) {
            ra.addFlashAttribute("error", "La cuenta contable no existe.");
            return "redirect:/catalogo-cuentas";
        }

        model.addAttribute("cuenta", cuenta);
        model.addAttribute("titulo", "Editar Cuenta Contable");
        return "catalogo-cuentas-form";
    }

    // GUARDAR (crear o actualizar) (admin + contador)
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("cuenta") CuentaContable cuenta,
                          HttpSession session,
                          RedirectAttributes ra) {

        if (!SecurityUtils.tieneRol(session, "admin", "contador")) {
            ra.addFlashAttribute("error", "No tienes permiso para guardar cuentas.");
            return "redirect:/catalogo-cuentas";
        }

        boolean esNueva = (cuenta.getId() == null);

        cuentaRepo.save(cuenta);

        Usuario usuario = SecurityUtils.getUsuario(session);
        if (usuario != null) {
            String accion = esNueva ? "CREAR_CUENTA" : "EDITAR_CUENTA";
            String detalle = "Cuenta: " + cuenta.getCodigo() + " - " + cuenta.getNombre();
            auditoriaService.registrar(usuario, accion, detalle);
        }

        ra.addFlashAttribute("success",
                esNueva ? "Cuenta creada correctamente." : "Cuenta actualizada correctamente.");
        return "redirect:/catalogo-cuentas";
    }

    // ELIMINAR CUENTA (admin + contador)
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id,
                           HttpSession session,
                           RedirectAttributes ra) {

        if (!SecurityUtils.tieneRol(session, "admin", "contador")) {
            ra.addFlashAttribute("error", "No tienes permiso para eliminar cuentas.");
            return "redirect:/catalogo-cuentas";
        }

        CuentaContable cuenta = cuentaRepo.findById(id).orElse(null);
        if (cuenta == null) {
            ra.addFlashAttribute("error", "La cuenta contable no existe.");
            return "redirect:/catalogo-cuentas";
        }

        try {
            cuentaRepo.delete(cuenta);

            Usuario usuario = SecurityUtils.getUsuario(session);
            if (usuario != null) {
                String detalle = "Cuenta eliminada: " + cuenta.getCodigo() + " - " + cuenta.getNombre();
                auditoriaService.registrar(usuario, "ELIMINAR_CUENTA", detalle);
            }

            ra.addFlashAttribute("success", "Cuenta eliminada correctamente.");
        } catch (Exception ex) {
            ra.addFlashAttribute("error",
                    "No se puede eliminar la cuenta, puede estar siendo utilizada en asientos contables.");
        }

        return "redirect:/catalogo-cuentas";
    }
}
