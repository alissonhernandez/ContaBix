package com.contabix.contabix.controller;

import com.contabix.contabix.model.Asiento;
import com.contabix.contabix.model.CuentaContable;
import com.contabix.contabix.model.DetalleAsiento;
import com.contabix.contabix.model.DocumentoFuente;
import com.contabix.contabix.model.Usuario;
import com.contabix.contabix.repository.*;
import com.contabix.contabix.service.AsientoService;
import com.contabix.contabix.service.LibroMayorService;
import com.contabix.contabix.service.AuditoriaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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

    @Autowired
    private DocumentoFuenteRepository documentoFuenteRepository;

    @Autowired
    private LibroMayorService libroMayorService;

    @Autowired
    private AuditoriaService auditoriaService;

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

        if (inicio != null && fin != null) {
            asientos = asientoRepository.findByFechaBetween(inicio, fin);
        } else if (q != null && !q.isBlank()) {
            asientos = asientoRepository.findByDescripcionContainingIgnoreCase(q);
        } else {
            asientos = asientoRepository.findAll();
        }

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
        asiento.setFecha(LocalDate.now());
        asiento.setDetalles(new ArrayList<>());

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

        if (asiento.getDetalles() == null || asiento.getDetalles().isEmpty()) {
            asiento.setDetalles(new ArrayList<>());
            asiento.getDetalles().add(new DetalleAsiento());
        }

        model.addAttribute("asiento", asiento);
        model.addAttribute("cuentas", cuentaRepository.findAll());
        model.addAttribute("esEdicion", true);
        return "libro-diario-form";
    }

    // ✅ Guardar asiento (nuevo o editado) + documento fuente opcional
    @PostMapping("/guardar")
    public String guardarAsiento(@ModelAttribute("asiento") Asiento asiento,
                                 BindingResult bindingResult,
                                 @RequestParam(value = "archivoDocumento", required = false) MultipartFile archivoDocumento,
                                 Model model,
                                 RedirectAttributes redirectAttributes,
                                 HttpSession session) {

        try {
            Usuario usuario = (Usuario) session.getAttribute("usuario");

            if (usuario == null) {
                model.addAttribute("error", "No se encontró el usuario autenticado");
                model.addAttribute("asiento", asiento);
                model.addAttribute("cuentas", cuentaRepository.findAll());
                return "libro-diario-form";
            }

            asiento.setUsuario(usuario);

            List<DetalleAsiento> detallesValidos = new ArrayList<>();

            if (asiento.getDetalles() != null) {
                for (DetalleAsiento d : asiento.getDetalles()) {

                    if (d.getCuenta() == null || d.getCuenta().getId() == null) {
                        continue;
                    }

                    CuentaContable cuenta = cuentaRepository.findById(d.getCuenta().getId()).orElse(null);
                    if (cuenta == null) continue;

                    double debe = d.getDebe() != null ? d.getDebe() : 0.0;
                    double haber = d.getHaber() != null ? d.getHaber() : 0.0;

                    if (debe == 0 && haber == 0) {
                        continue;
                    }

                    d.setCuenta(cuenta);
                    d.setAsiento(asiento);

                    detallesValidos.add(d);
                }
            }

            asiento.setDetalles(detallesValidos);

            if (archivoDocumento != null && !archivoDocumento.isEmpty()) {
                DocumentoFuente doc = new DocumentoFuente();
                doc.setNombreArchivo(archivoDocumento.getOriginalFilename());
                doc.setTipoArchivo(archivoDocumento.getContentType());
                doc.setContenido(archivoDocumento.getBytes());
                doc.setDescripcion("Documento del asiento: " + asiento.getDescripcion());
                documentoFuenteRepository.save(doc);
                asiento.setDocumentoFuente(doc);
            }

            if (asiento.getFecha() == null) {
                asiento.setFecha(LocalDate.now());
            }

            asientoService.guardarAsientoConValidacion(asiento);

            // ACTUALIZAR LIBRO MAYOR
            libroMayorService.actualizarLibroMayor();

            // Registrar en bitácora de auditoría
            auditoriaService.registrar(usuario,
                    "GUARDAR_ASIENTO",
                    "Asiento ID: " + asiento.getId());

            String mensaje = asiento.getId() != null
                    ? "Asiento actualizado correctamente"
                    : "Asiento guardado correctamente";
            redirectAttributes.addFlashAttribute("success", mensaje);
            return "redirect:/libro-diario";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
        } catch (IOException ex) {
            model.addAttribute("error", "Error al adjuntar el documento: " + ex.getMessage());
        } catch (Exception ex) {
            model.addAttribute("error", "Error al guardar asiento: " + ex.getMessage());
        }

        model.addAttribute("asiento", asiento);
        model.addAttribute("cuentas", cuentaRepository.findAll());
        return "libro-diario-form";
    }

    // ✅ Eliminar asiento
    @GetMapping("/eliminar/{id}")
    public String eliminarAsiento(@PathVariable Integer id,
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {
        try {
            Asiento asiento = asientoRepository.findById(id).orElse(null);
            if (asiento == null) {
                redirectAttributes.addFlashAttribute("error", "Asiento no encontrado");
            } else {
                asientoRepository.delete(asiento);
                redirectAttributes.addFlashAttribute("success", "Asiento eliminado correctamente");

                Usuario usuario = (Usuario) session.getAttribute("usuario");
                if (usuario != null) {
                    auditoriaService.registrar(usuario,
                            "ELIMINAR_ASIENTO",
                            "Asiento ID: " + id);
                }
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/libro-diario";
    }

}
