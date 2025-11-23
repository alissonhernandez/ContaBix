package com.contabix.contabix.controller;

import com.contabix.contabix.model.ClasificacionDocumento;
import com.contabix.contabix.model.DocumentoFuente;
import com.contabix.contabix.model.Usuario;
import com.contabix.contabix.repository.ClasificacionDocumentoRepository;
import com.contabix.contabix.repository.DocumentoFuenteRepository;
import com.contabix.contabix.service.AuditoriaService;
import com.contabix.contabix.util.SecurityUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/documentos")
public class DocumentoFuenteController {

    private final DocumentoFuenteRepository documentoFuenteRepository;
    private final ClasificacionDocumentoRepository clasificacionDocumentoRepository;
    private final AuditoriaService auditoriaService;

    public DocumentoFuenteController(DocumentoFuenteRepository documentoFuenteRepository,
                                     ClasificacionDocumentoRepository clasificacionDocumentoRepository,
                                     AuditoriaService auditoriaService) {
        this.documentoFuenteRepository = documentoFuenteRepository;
        this.clasificacionDocumentoRepository = clasificacionDocumentoRepository;
        this.auditoriaService = auditoriaService;
    }

    // LISTAR DOCUMENTOS (admin + contador)
    @GetMapping
    public String listar(Model model,
                         @RequestParam(value = "success", required = false) String success,
                         @RequestParam(value = "error", required = false) String error,
                         HttpSession session,
                         RedirectAttributes ra) {

        if (!SecurityUtils.tieneRol(session, "admin", "contador")) {
            ra.addFlashAttribute("error", "No tienes permiso para acceder a Documentos Fuente.");
            return "redirect:/inicio";
        }

        List<DocumentoFuente> documentos = documentoFuenteRepository.findAll();

        model.addAttribute("documentos", documentos);
        model.addAttribute("success", success);
        model.addAttribute("error", error);

        return "documentos-list";
    }

    // FORMULARIO NUEVO (admin + contador)
    @GetMapping("/nuevo")
    public String nuevo(Model model,
                        HttpSession session,
                        RedirectAttributes ra) {

        if (!SecurityUtils.tieneRol(session, "admin", "contador")) {
            ra.addFlashAttribute("error", "No tienes permiso para subir documentos.");
            return "redirect:/documentos";
        }

        model.addAttribute("documento", new DocumentoFuente());
        model.addAttribute("clasificaciones", clasificacionDocumentoRepository.findAll());
        return "documentos-form";
    }

    // GUARDAR (SUBIR PDF) (admin + contador)
    @PostMapping
    public String guardar(@RequestParam("archivo") MultipartFile archivo,
                          @RequestParam(value = "descripcion", required = false) String descripcion,
                          @RequestParam(value = "clasificacionId", required = false) Integer clasificacionId,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {

        if (!SecurityUtils.tieneRol(session, "admin", "contador")) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para subir documentos.");
            return "redirect:/documentos";
        }

        if (archivo == null || archivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar un archivo PDF.");
            return "redirect:/documentos";
        }

        try {
            DocumentoFuente doc = new DocumentoFuente();
            doc.setNombreArchivo(archivo.getOriginalFilename());
            doc.setTipoArchivo(archivo.getContentType());
            doc.setContenido(archivo.getBytes());
            doc.setDescripcion(descripcion);

            if (clasificacionId != null) {
                ClasificacionDocumento clas = clasificacionDocumentoRepository
                        .findById(clasificacionId)
                        .orElse(null);
                doc.setClasificacion(clas);
            }

            documentoFuenteRepository.save(doc);

            Usuario usuario = (Usuario) session.getAttribute("usuario");
            auditoriaService.registrar(
                    usuario,
                    "SUBIR_DOCUMENTO",
                    "Documento: " + doc.getNombreArchivo()
            );

            redirectAttributes.addFlashAttribute("success", "Documento subido correctamente.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al subir el archivo: " + e.getMessage());
        }

        return "redirect:/documentos";
    }

    // DESCARGAR PDF (admin + contador)
    @GetMapping("/{id}/descargar")
    public ResponseEntity<byte[]> descargar(@PathVariable Integer id,
                                            HttpSession session) {

        if (!SecurityUtils.tieneRol(session, "admin", "contador")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        DocumentoFuente doc = documentoFuenteRepository.findById(id).orElse(null);
        if (doc == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + doc.getNombreArchivo() + "\"")
                .header("Content-Type", doc.getTipoArchivo())
                .body(doc.getContenido());
    }

    // ELIMINAR DOCUMENTO (admin + contador)
    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        if (!SecurityUtils.tieneRol(session, "admin", "contador")) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para eliminar documentos.");
            return "redirect:/documentos";
        }

        if (documentoFuenteRepository.existsById(id)) {
            DocumentoFuente doc = documentoFuenteRepository.findById(id).orElse(null);
            documentoFuenteRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Documento eliminado correctamente.");

            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuario != null && doc != null) {
                auditoriaService.registrar(
                        usuario,
                        "ELIMINAR_DOCUMENTO",
                        "Documento ID: " + id + " - " + doc.getNombreArchivo()
                );
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "El documento no existe.");
        }
        return "redirect:/documentos";
    }
}
