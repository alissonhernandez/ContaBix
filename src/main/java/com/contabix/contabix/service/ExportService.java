package com.contabix.contabix.service;

import com.contabix.contabix.dto.BalanceGeneralDTO;
import com.contabix.contabix.model.Asiento;
import com.contabix.contabix.model.DetalleAsiento;
import com.contabix.contabix.model.LibroMayor;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import com.contabix.contabix.dto.EstadoResultadosDTO;


import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExportService {

    public byte[] generarPdfLibroDiario(List<Asiento> asientos) throws Exception {

        Document document = new Document(PageSize.A4.rotate()); // Horizontal para más espacio
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, baos);
        document.open();
        // LOGO
        Image logo = Image.getInstance("src/main/resources/static/img/contabixlogo.png");
        logo.scaleToFit(80, 80); // tamaño
        logo.setAlignment(Image.ALIGN_LEFT);
        document.add(logo);

        // ESPACIO ENTRE LOGO Y TÍTULO
        document.add(new Paragraph(" "));

        // ---------------------
        // TÍTULO
        // ---------------------
        Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph titulo = new Paragraph("LIBRO DIARIO - CONTABIX", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // ---------------------
        // TABLA
        // ---------------------
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{15f, 50f, 15f, 15f}); // Ajusta columnas

        // Encabezados
        table.addCell(new Phrase("Fecha"));
        table.addCell(new Phrase("Cuenta"));
        table.addCell(new Phrase("Debe"));
        table.addCell(new Phrase("Haber"));

        // ---------------------
        // CONTENIDO
        // ---------------------
        for (Asiento a : asientos) {

            if (a.getDetalles() == null) continue;

            for (DetalleAsiento d : a.getDetalles()) {

                table.addCell(a.getFecha().toString());
                table.addCell(d.getCuenta().getCodigo() + " - " + d.getCuenta().getNombre());
                table.addCell(d.getDebe() != null ? d.getDebe().toString() : "");
                table.addCell(d.getHaber() != null ? d.getHaber().toString() : "");
            }
        }

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    public byte[] generarPdfLibroMayor(List<LibroMayor> registros) throws Exception {

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, baos);
        document.open();
        // LOGO
        Image logo = Image.getInstance("src/main/resources/static/img/contabixlogo.png");
        logo.scaleToFit(80, 80); // tamaño
        logo.setAlignment(Image.ALIGN_LEFT);
        document.add(logo);

        // ESPACIO ENTRE LOGO Y TÍTULO
        document.add(new Paragraph(" "));

        // Título
        Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph titulo = new Paragraph("Libro Mayor - Contabix", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // Tabla
        PdfPTable table = new PdfPTable(4);
        table.addCell("Cuenta");
        table.addCell("Debe");
        table.addCell("Haber");
        table.addCell("Saldo");

        for (LibroMayor lm : registros) {

            table.addCell(lm.getCuentaContable().getNombre());
            table.addCell(String.valueOf(lm.getTotalDebe()));
            table.addCell(String.valueOf(lm.getTotalHaber()));
            table.addCell(String.valueOf(lm.getSaldo()));
        }

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    public byte[] generarPdfBalanceGeneral(List<BalanceGeneralDTO> balance) throws Exception {

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, baos);
        document.open();
        // LOGO
        Image logo = Image.getInstance("src/main/resources/static/img/contabixlogo.png");
        logo.scaleToFit(80, 80); // tamaño
        logo.setAlignment(Image.ALIGN_LEFT);
        document.add(logo);

        // ESPACIO ENTRE LOGO Y TÍTULO
        document.add(new Paragraph(" "));

        // Título
        Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph titulo = new Paragraph("Balance General - Contabix", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // Tabla
        PdfPTable table = new PdfPTable(5);
        table.addCell("Código");
        table.addCell("Cuenta");
        table.addCell("Tipo");
        table.addCell("Categoría");
        table.addCell("Saldo");

        for (BalanceGeneralDTO b : balance) {
            table.addCell(b.getCodigo());
            table.addCell(b.getNombre());
            table.addCell(b.getTipo());
            table.addCell(b.getCategoria());
            table.addCell(String.valueOf(b.getSaldo()));
        }

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    public byte[] generarPdfEstadoResultados(EstadoResultadosDTO estado) throws Exception {

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, baos);
        document.open();
        // LOGO
        Image logo = Image.getInstance("src/main/resources/static/img/contabixlogo.png");
        logo.scaleToFit(80, 80); // tamaño
        logo.setAlignment(Image.ALIGN_LEFT);
        document.add(logo);

        // ESPACIO ENTRE LOGO Y TÍTULO
        document.add(new Paragraph(" "));

        Font tituloFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Paragraph titulo = new Paragraph("Estado de Resultados - Contabix", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        table.addCell("Concepto");
        table.addCell("Monto");

        table.addCell("Ingresos");
        table.addCell(String.valueOf(estado.getIngresos()));

        table.addCell("Costos");
        table.addCell(String.valueOf(estado.getCostos()));

        table.addCell("Gastos Operativos");
        table.addCell(String.valueOf(estado.getGastos()));

        table.addCell("Otros Ingresos / Gastos");
        table.addCell(String.valueOf(estado.getOtros()));

        table.addCell("Utilidad del Ejercicio");
        table.addCell(String.valueOf(estado.getUtilidad()));

        document.add(table);
        document.close();

        return baos.toByteArray();
    }


}



