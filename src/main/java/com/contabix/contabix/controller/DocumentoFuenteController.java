package com.contabix.contabix.controller;

import com.contabix.contabix.model.DocumentoFuente;
import com.contabix.contabix.repository.DocumentoFuenteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/documentos")
public class DocumentoFuenteController {

    private final DocumentoFuenteRepository documentoFuenteRepository;

    public DocumentoFuenteController(DocumentoFuenteRepository documentoFuenteRepository) {
        this.documentoFuenteRepository = documentoFuenteRepository;
    }

    // LISTAR DOCUMENTOS
    @GetMapping
    public String listar(Model model,
                         @RequestParam(value = "success", required = false) String success,
                         @RequestParam(value = "error", required = false) String error) {

        model.addAttribute("documentos", documentoFuenteRepository.findAll());
        if (success != null) {
            model.addAttribute("success", success);
        }
        if (error != null) {
            model.addAttribute("error", error);
        }

        return "documentos-list";
    }

    // FORMULARIO NUEVO
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("documento", new DocumentoFuente());
        return "documentos-form";
    }

    // GUARDAR (SUBIR PDF)
    @PostMapping
    public String guardar(@RequestParam("archivo") MultipartFile archivo,
                          @RequestParam(value = "descripcion", required = false) String descripcion,
                          RedirectAttributes redirectAttributes) {

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

            documentoFuenteRepository.save(doc);
            redirectAttributes.addFlashAttribute("success", "Documento subido correctamente.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al subir el archivo: " + e.getMessage());
        }

        return "redirect:/documentos";
    }

    // DESCARGAR PDF
    @GetMapping("/{id}/descargar")
    public ResponseEntity<byte[]> descargar(@PathVariable Integer id) {
        DocumentoFuente doc = documentoFuenteRepository.findById(id).orElse(null);
        if (doc == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + doc.getNombreArchivo() + "\"")
                .header("Content-Type", doc.getTipoArchivo())
                .body(doc.getContenido());
    }

    // ELIMINAR DOCUMENTO
    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (documentoFuenteRepository.existsById(id)) {
            documentoFuenteRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Documento eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "El documento no existe.");
        }
        return "redirect:/documentos";
    }
}
