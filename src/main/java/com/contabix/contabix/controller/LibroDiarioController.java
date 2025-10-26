package com.contabix.contabix.controller;

import com.contabix.contabix.model.Asiento;
import com.contabix.contabix.model.DetalleAsiento;
import com.contabix.contabix.model.CuentaContable;
import com.contabix.contabix.model.Usuario;
import com.contabix.contabix.repository.AsientoRepository;
import com.contabix.contabix.repository.CuentaContableRepository;
import com.contabix.contabix.repository.UsuarioRepository;
import com.contabix.contabix.service.AsientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/libro-diario")
public class LibroDiarioController {

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private CuentaContableRepository cuentaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AsientoService asientoService;

    // Listado con filtros
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
        model.addAttribute("success", model.asMap().get("success"));

        return "libro-diario-list";
    }

    // Formulario nuevo asiento
    @GetMapping("/nuevo")
    public String nuevoAsiento(Model model) {
        Asiento asiento = new Asiento();
        asiento.setDetalles(new ArrayList<>());

        // Se inicia con 2 filas por defecto (usuario puede agregar más desde el formulario)
        asiento.getDetalles().add(new DetalleAsiento());
        asiento.getDetalles().add(new DetalleAsiento());

        model.addAttribute("asiento", asiento);
        model.addAttribute("cuentas", cuentaRepository.findAll());
        return "libro-diario-form";
    }

    // Guardar asiento
    @PostMapping("/guardar")
    public String guardarAsiento(@ModelAttribute("asiento") Asiento asiento,
                                 BindingResult bindingResult, Model model) {
        try {
            Usuario usuario = usuarioRepository.findById(1L).orElse(null);
            if (usuario == null) {
                model.addAttribute("error", "No existe el usuario con ID 1");
                model.addAttribute("asiento", asiento);
                model.addAttribute("cuentas", cuentaRepository.findAll());
                return "libro-diario-form";
            }
            asiento.setUsuario(usuario);

            // Filtrar detalles vacíos (que el usuario no llenó)
            List<DetalleAsiento> detallesValidos = new ArrayList<>();
            for (DetalleAsiento d : asiento.getDetalles()) {
                if (d.getCuenta() != null && d.getCuenta().getId() != null &&
                        (d.getDebe() != null && d.getDebe().doubleValue() > 0 ||
                                d.getHaber() != null && d.getHaber().doubleValue() > 0)) {
                    CuentaContable cuenta = cuentaRepository.findById(d.getCuenta().getId()).orElse(null);
                    d.setCuenta(cuenta);
                    detallesValidos.add(d);
                }
            }

            // Reemplazamos solo los detalles válidos
            asiento.setDetalles(detallesValidos);

            // Guardar validando balance (Debe = Haber)
            asientoService.guardarAsientoConValidacion(asiento);

            return "redirect:/libro-diario?success=Asiento+guardado+correctamente";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            model.addAttribute("error", "Error al guardar asiento: " + ex.getMessage());
        }

        model.addAttribute("asiento", asiento);
        model.addAttribute("cuentas", cuentaRepository.findAll());
        return "libro-diario-form";
    }
}
