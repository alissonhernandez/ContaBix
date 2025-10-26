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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // ✅ Listado con filtros y sumas
    @GetMapping
    public String listar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fin,

            @RequestParam(required = false)
            String q,

            Model model) {

        List<Asiento> asientos;

        // --- Filtro de búsqueda ---
        if (inicio != null && fin != null) {
            asientos = asientoRepository.findByFechaBetween(inicio, fin);
        } else if (q != null && !q.isBlank()) {
            asientos = asientoRepository.findByDescripcionContainingIgnoreCase(q);
        } else {
            asientos = asientoRepository.findAll();
        }

        // --- Cálculo de subtotales y totales ---
        Map<Integer, Double> subtotalDebeMap = new HashMap<>();
        Map<Integer, Double> subtotalHaberMap = new HashMap<>();
        double totalDebe = 0.0;
        double totalHaber = 0.0;

        for (Asiento asiento : asientos) {
            double sumaDebe = 0.0;
            double sumaHaber = 0.0;

            if (asiento.getDetalles() != null) {
                for (DetalleAsiento detalle : asiento.getDetalles()) {
                    if (detalle.getDebe() != null) {
                        sumaDebe += detalle.getDebe();
                    }
                    if (detalle.getHaber() != null) {
                        sumaHaber += detalle.getHaber();
                    }
                }
            }

            Integer clave = asiento.getId() != null ? asiento.getId() : asiento.hashCode();

            subtotalDebeMap.put(clave, sumaDebe);
            subtotalHaberMap.put(clave, sumaHaber);

            totalDebe += sumaDebe;
            totalHaber += sumaHaber;
        }

        // --- Atributos del modelo ---
        model.addAttribute("asientos", asientos);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fin", fin);
        model.addAttribute("q", q);

        model.addAttribute("subtotalDebeMap", subtotalDebeMap);
        model.addAttribute("subtotalHaberMap", subtotalHaberMap);
        model.addAttribute("totalDebe", totalDebe);
        model.addAttribute("totalHaber", totalHaber);

        return "libro-diario-list";
    }

    // ✅ Formulario nuevo asiento
    @GetMapping("/nuevo")
    public String nuevoAsiento(Model model) {
        Asiento asiento = new Asiento();
        asiento.setDetalles(new ArrayList<>());

        // Se inicia con 2 filas por defecto
        asiento.getDetalles().add(new DetalleAsiento());
        asiento.getDetalles().add(new DetalleAsiento());

        model.addAttribute("asiento", asiento);
        model.addAttribute("cuentas", cuentaRepository.findAll());
        return "libro-diario-form";
    }

    // ✅ Formulario editar asiento
    @GetMapping("/editar/{id}")
    public String editarAsiento(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Asiento asiento = asientoRepository.findById(id).orElse(null);

        if (asiento == null) {
            redirectAttributes.addFlashAttribute("error", "Asiento no encontrado");
            return "redirect:/libro-diario";
        }

        // Si no tiene detalles, agregar uno vacío para evitar errores
        if (asiento.getDetalles() == null || asiento.getDetalles().isEmpty()) {
            asiento.setDetalles(new ArrayList<>());
            asiento.getDetalles().add(new DetalleAsiento());
        }

        model.addAttribute("asiento", asiento);
        model.addAttribute("cuentas", cuentaRepository.findAll());
        model.addAttribute("esEdicion", true);
        return "libro-diario-form";
    }

    // ✅ Guardar asiento (nuevo o editado)
    @PostMapping("/guardar")
    public String guardarAsiento(@ModelAttribute("asiento") Asiento asiento,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            // 🔹 Obtener o validar usuario
            Usuario usuario = usuarioRepository.findById(1L).orElse(null);
            if (usuario == null) {
                model.addAttribute("error", "No existe el usuario con ID 1");
                model.addAttribute("asiento", asiento);
                model.addAttribute("cuentas", cuentaRepository.findAll());
                return "libro-diario-form";
            }
            asiento.setUsuario(usuario);

            // Filtrar detalles válidos (no vacíos)
            List<DetalleAsiento> detallesValidos = new ArrayList<>();
            for (DetalleAsiento d : asiento.getDetalles()) {
                if (d.getCuenta() != null && d.getCuenta().getId() != null &&
                        ((d.getDebe() != null && d.getDebe() > 0) ||
                                (d.getHaber() != null && d.getHaber() > 0))) {
                    CuentaContable cuenta = cuentaRepository.findById(d.getCuenta().getId()).orElse(null);
                    d.setCuenta(cuenta);
                    d.setAsiento(asiento);
                    detallesValidos.add(d);
                }
            }

            asiento.setDetalles(detallesValidos);

            // Guardar validando balance (Debe = Haber)
            asientoService.guardarAsientoConValidacion(asiento);

            String mensaje = asiento.getId() != null ? "Asiento actualizado correctamente" : "Asiento guardado correctamente";
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/libro-diario";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
        } catch (Exception ex) {
            model.addAttribute("error", "Error al guardar asiento: " + ex.getMessage());
        }

        model.addAttribute("asiento", asiento);
        model.addAttribute("cuentas", cuentaRepository.findAll());
        return "libro-diario-form";
    }

    // ✅ Eliminar asiento
    @GetMapping("/eliminar/{id}")
    public String eliminarAsiento(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Asiento asiento = asientoRepository.findById(id).orElse(null);
            if (asiento == null) {
                redirectAttributes.addFlashAttribute("error", "Asiento no encontrado");
            } else {
                asientoRepository.delete(asiento);
                redirectAttributes.addFlashAttribute("success", "Asiento eliminado correctamente");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/libro-diario";
    }
}