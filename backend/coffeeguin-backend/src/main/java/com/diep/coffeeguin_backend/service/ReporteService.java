package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.dto.AnaliticaGraficaDTO;
import com.diep.coffeeguin_backend.model.Reporte;
import com.diep.coffeeguin_backend.model.Venta;
import com.diep.coffeeguin_backend.model.VentasPorCategoriaResumen;
import com.diep.coffeeguin_backend.model.VentasPorProductoResumen;
import com.diep.coffeeguin_backend.repository.ReporteRepository;
import com.diep.coffeeguin_backend.repository.VentaDetalleRepository;
import com.diep.coffeeguin_backend.repository.VentaRepository;
import com.diep.coffeeguin_backend.repository.projection.AnaliticaGraficaProjection;
import com.diep.coffeeguin_backend.repository.projection.ReporteFinancieroProjection;
import com.diep.coffeeguin_backend.repository.projection.ProductosVendidosPorMesaProjection;
import com.diep.coffeeguin_backend.repository.projection.VentaResumenProjection;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReporteService {

    private static final DateTimeFormatter FORMATO_FECHA_PDF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final BaseColor COLOR_ENCABEZADO = new BaseColor(34, 91, 138);
    private static final BaseColor COLOR_ZEBRA = new BaseColor(245, 249, 252);

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private VentaDetalleRepository ventaDetalleRepository;

    @Autowired
    private VentaRepository ventaRepository;

    public List<Reporte> obtenerTodosLosReportes() {
        return reporteRepository.findAll();
    }

    public Reporte generarReportePorPeriodo(String tipo) {
        LocalDateTime ahora = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime fechaInicio = resolverFechaInicio(tipo, ahora);
        List<Venta> ventasDelPeriodo = ventaService.listarPorPeriodo(fechaInicio, ahora);
        double totalDinero = ventasDelPeriodo.stream()
                .mapToDouble(Venta::getTotalFinal)
                .sum();

        Reporte nuevoReporte = new Reporte();
        nuevoReporte.setTitulo("Reporte " + tipo.toUpperCase() + " de Ventas");
        nuevoReporte.setFechaGeneracion(ahora);
        nuevoReporte.setTotalCalculado(totalDinero);
        nuevoReporte.setVentas(ventasDelPeriodo);
        nuevoReporte.setObservaciones("Reporte generado automáticamente. Total de tickets: " + ventasDelPeriodo.size());

        return reporteRepository.save(nuevoReporte);
    }

    public List<VentasPorProductoResumen> resumenVentasPorProducto(String tipo) {
        LocalDateTime ahora = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime fechaInicio = resolverFechaInicio(tipo, ahora);
        return ventaDetalleRepository.resumirVentasPorProducto(fechaInicio, ahora);
    }

    public List<VentasPorCategoriaResumen> resumenVentasPorCategoria(String tipo) {
        LocalDateTime ahora = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime fechaInicio = resolverFechaInicio(tipo, ahora);
        return ventaDetalleRepository.resumirVentasPorCategoria(fechaInicio, ahora);
    }

    public byte[] generarReporteFinancieroPdf(String tipo) {
        LocalDateTime ahora = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        RangoFechas rangoFechas = resolverRangoPorTipo(tipo, ahora);
        ReporteFinancieroProjection resumen = ventaRepository.resumirBalancePorPeriodo(rangoFechas.inicio(), rangoFechas.fin());
        List<VentaResumenProjection> ventasPeriodo = ventaRepository.buscarResumenVentas(rangoFechas.inicio(), rangoFechas.fin());
        List<AnaliticaGraficaProjection> topProductos = obtenerTopProductosDelPeriodo(rangoFechas.inicio(), rangoFechas.fin());
        List<ProductosVendidosPorMesaProjection> productosPorMesa = ventaDetalleRepository.productosVendidosPorMesa(rangoFechas.inicio(), rangoFechas.fin());

        long totalTransacciones = resumen != null && resumen.getTotalTransacciones() != null
                ? resumen.getTotalTransacciones().longValue()
                : 0L;
        BigDecimal balanceNeto = resumen != null && resumen.getBalanceNeto() != null
                ? resumen.getBalanceNeto()
                : BigDecimal.ZERO;

        // Declaramos la salida fuera del try-with-resources tradicional para evitar el auto-close conflictivo,
        // ya que documento.close() se encargará de gestionar el cierre de manera segura.
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        try {
            Document documento = new Document(PageSize.A4, 36, 36, 48, 36);
            PdfWriter.getInstance(documento, salida);
            documento.open();

            Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font subtitulo = FontFactory.getFont(FontFactory.HELVETICA, 11);
            Font etiqueta = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font fuenteSeccion = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BaseColor.WHITE);

            Paragraph encabezado = new Paragraph("Reporte Financiero " + tipo.toUpperCase(), titulo);
            encabezado.setAlignment(Element.ALIGN_CENTER);
            documento.add(encabezado);
            documento.add(Chunk.NEWLINE);

            documento.add(new Paragraph("Fecha de generación: " + formatearFecha(ahora), subtitulo));
            documento.add(new Paragraph("Período evaluado: " + formatearFecha(rangoFechas.inicio()) + " a " + formatearFecha(rangoFechas.fin()), subtitulo));
            documento.add(Chunk.NEWLINE);

            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100);
            tabla.setSpacingBefore(10f);
            tabla.setWidths(new float[] { 2f, 3f });

            agregarCeldaEncabezado(tabla, "Métrica", etiqueta);
            agregarCeldaEncabezado(tabla, "Valor", etiqueta);
            agregarCelda(tabla, "Total de transacciones", String.valueOf(totalTransacciones), subtitulo);
            agregarCelda(tabla, "Balance neto recaudado", formatearMoneda(balanceNeto), subtitulo);

            documento.add(tabla);

            documento.add(Chunk.NEWLINE);
            agregarSeccionTitulo(documento, "Desglose de ventas del periodo", fuenteSeccion);
            agregarTablaVentasPeriodo(documento, ventasPeriodo, subtitulo, etiqueta);

            documento.add(Chunk.NEWLINE);
            agregarSeccionTitulo(documento, "Desglose de productos vendidos por mesa", fuenteSeccion);
            agregarTablaProductosPorMesa(documento, productosPorMesa, subtitulo, etiqueta);

            documento.add(Chunk.NEWLINE);
            agregarSeccionTitulo(documento, "Productos mas vendidos", fuenteSeccion);
            agregarTablaTopProductos(documento, topProductos, subtitulo, etiqueta);
            
            // Al cerrar el documento, iText empaqueta todo en el ByteArrayOutputStream de forma limpia
            documento.close(); 
            
            return salida.toByteArray();
        } catch (DocumentException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error en la estructura del PDF", e);
        } finally {
            try {
                salida.close();
            } catch (IOException e) {
                // Un fail aquí es extremadamente raro en memoria, pero dejamos el catch para complacer al compilador
            }
        }
    }

    public AnaliticaGraficaDTO obtenerAnaliticaGrafica(String criterio, LocalDate fechaInicio, LocalDate fechaFin) {
        if (criterio == null || criterio.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El criterio es obligatorio");
        }
        if (fechaInicio == null || fechaFin == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechaInicio y fechaFin son obligatorias");
        }

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
        if (inicio.isAfter(fin)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechaInicio no puede ser mayor que fechaFin");
        }

        List<AnaliticaGraficaProjection> resultados = switch (criterio.toUpperCase()) {
            case "PRODUCTO" -> ventaDetalleRepository.analiticaPorProducto(inicio, fin);
            case "CATEGORIA" -> ventaDetalleRepository.analiticaPorCategoria(inicio, fin);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Criterio inválido. Usa PRODUCTO o CATEGORIA");
        };

        List<String> labels = new ArrayList<>(resultados.size());
        List<Integer> seriesCantidad = new ArrayList<>(resultados.size());
        List<Double> seriesIngresos = new ArrayList<>(resultados.size());

        for (AnaliticaGraficaProjection fila : resultados) {
            labels.add(fila.getEtiqueta());
            seriesCantidad.add(fila.getCantidadVendida() == null ? 0 : fila.getCantidadVendida().intValue());
            seriesIngresos.add(fila.getIngresos() == null ? 0.0 : fila.getIngresos().doubleValue());
        }

        return new AnaliticaGraficaDTO(labels, seriesCantidad, seriesIngresos);
    }

    public List<Venta> consultarVentasPorPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return ventaService.listarPorPeriodo(inicio, fin);
    }

    private LocalDateTime resolverFechaInicio(String tipo, LocalDateTime ahora) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de reporte es obligatorio");
        }

        return switch (tipo.toLowerCase()) {
            case "diario" -> ahora.toLocalDate().atStartOfDay();
            case "semanal" -> ahora.minusDays(7);
            case "mensual" -> ahora.minusMonths(1);
            default -> throw new IllegalArgumentException("Tipo inválido. Usa: diario, semanal o mensual");
        };
    }

    private RangoFechas resolverRangoPorTipo(String tipo, LocalDateTime ahora) {
        LocalDateTime inicio = resolverFechaInicio(tipo, ahora);
        return new RangoFechas(inicio, ahora);
    }

    private List<AnaliticaGraficaProjection> obtenerTopProductosDelPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        List<AnaliticaGraficaProjection> topProductos = ventaDetalleRepository.analiticaPorProducto(inicio, fin);
        return topProductos.size() <= 5 ? topProductos : topProductos.subList(0, 5);
    }

    private void agregarTablaVentasPeriodo(Document documento, List<VentaResumenProjection> ventasPeriodo, Font fontTexto, Font fontEncabezado)
            throws DocumentException {
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(4f);
        tabla.setWidths(new float[] { 1.2f, 2.0f, 3.0f, 1.6f });

        agregarCeldaEncabezado(tabla, "Folio", fontEncabezado);
        agregarCeldaEncabezado(tabla, "Fecha", fontEncabezado);
        agregarCeldaEncabezado(tabla, "Cliente / Mesa", fontEncabezado);
        agregarCeldaEncabezado(tabla, "Total", fontEncabezado);

        if (ventasPeriodo.isEmpty()) {
            agregarCeldaVacia(tabla, "Sin ventas en el periodo", 4, fontTexto);
        } else {
            boolean usarZebra = false;
            for (VentaResumenProjection venta : ventasPeriodo) {
                String clienteMesa = construirClienteMesa(venta.getNombreCliente(), venta.getNumeroMesa());
                agregarCelda(tabla, String.valueOf(venta.getIdVenta()), formatearFecha(venta.getFecha()), clienteMesa, formatearMoneda(valorSeguro(venta.getTotalFinal())), fontTexto, usarZebra);
                usarZebra = !usarZebra;
            }
        }

        documento.add(tabla);
    }

    private void agregarTablaProductosPorMesa(Document documento, List<ProductosVendidosPorMesaProjection> productosPorMesa,
            Font fontTexto, Font fontEncabezado) throws DocumentException {
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(4f);
        tabla.setWidths(new float[] { 1.4f, 4.0f, 1.4f, 1.8f });

        agregarCeldaEncabezado(tabla, "Mesa", fontEncabezado);
        agregarCeldaEncabezado(tabla, "Producto", fontEncabezado);
        agregarCeldaEncabezado(tabla, "Cantidad", fontEncabezado);
        agregarCeldaEncabezado(tabla, "Ingreso", fontEncabezado);

        if (productosPorMesa.isEmpty()) {
            agregarCeldaVacia(tabla, "Sin datos para el periodo", 4, fontTexto);
        } else {
            boolean usarZebra = false;
            for (ProductosVendidosPorMesaProjection fila : productosPorMesa) {
                agregarCelda(tabla,
                        valorTexto(fila.getNumeroMesa(), "Sin mesa"),
                        valorTexto(fila.getNombreProducto(), "-"),
                        String.valueOf(fila.getCantidadVendida() != null ? fila.getCantidadVendida() : 0),
                        formatearMoneda(fila.getIngresos() != null ? fila.getIngresos() : BigDecimal.ZERO),
                        fontTexto,
                        usarZebra);
                usarZebra = !usarZebra;
            }
        }

        documento.add(tabla);
    }

    private void agregarTablaTopProductos(Document documento, List<AnaliticaGraficaProjection> topProductos, Font fontTexto, Font fontEncabezado)
            throws DocumentException {
        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(4f);
        tabla.setWidths(new float[] { 4.2f, 1.4f, 1.8f });

        agregarCeldaEncabezado(tabla, "Producto", fontEncabezado);
        agregarCeldaEncabezado(tabla, "Cantidad", fontEncabezado);
        agregarCeldaEncabezado(tabla, "Ingreso generado", fontEncabezado);

        if (topProductos.isEmpty()) {
            agregarCeldaVacia(tabla, "Sin datos para el periodo", 3, fontTexto);
        } else {
            boolean usarZebra = false;
            for (AnaliticaGraficaProjection producto : topProductos) {
                agregarCelda(tabla,
                        producto.getEtiqueta(),
                        String.valueOf(producto.getCantidadVendida() != null ? producto.getCantidadVendida() : 0),
                        formatearMoneda(producto.getIngresos() != null ? producto.getIngresos() : BigDecimal.ZERO),
                        fontTexto,
                        usarZebra);
                usarZebra = !usarZebra;
            }
        }

        documento.add(tabla);
    }

    private void agregarCelda(PdfPTable tabla, String valor1, String valor2, String valor3, String valor4, Font font, boolean sombrear) {
        tabla.addCell(crearCelda(valor1, font, sombrear));
        tabla.addCell(crearCelda(valor2, font, sombrear));
        tabla.addCell(crearCelda(valor3, font, sombrear));
        tabla.addCell(crearCelda(valor4, font, sombrear));
    }

    private void agregarCelda(PdfPTable tabla, String valor1, String valor2, String valor3, Font font, boolean sombrear) {
        tabla.addCell(crearCelda(valor1, font, sombrear));
        tabla.addCell(crearCelda(valor2, font, sombrear));
        tabla.addCell(crearCelda(valor3, font, sombrear));
    }

    private void agregarCeldaVacia(PdfPTable tabla, String mensaje, int colspan, Font font) {
        PdfPCell celda = crearCelda(mensaje, font);
        celda.setColspan(colspan);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(celda);
    }

    private PdfPCell crearCelda(String texto, Font font) {
        return crearCelda(texto, font, false);
    }

    private PdfPCell crearCelda(String texto, Font font, boolean sombrear) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, font));
        celda.setPadding(8f);
        if (sombrear) {
            celda.setBackgroundColor(COLOR_ZEBRA);
        }
        return celda;
    }

    private String valorTexto(String valor, String defecto) {
        return valor != null && !valor.isBlank() ? valor : defecto;
    }

    private String formatearFecha(LocalDateTime fecha) {
        return fecha != null ? fecha.format(FORMATO_FECHA_PDF) : "-";
    }

    private String construirClienteMesa(String nombreCliente, String numeroMesa) {
        String cliente = nombreCliente != null && !nombreCliente.isBlank() ? nombreCliente : "Público general";
        String mesa = numeroMesa != null && !numeroMesa.isBlank() ? numeroMesa : "Sin mesa";
        return cliente + " / " + mesa;
    }

    private double valorSeguro(Double valor) {
        return valor != null ? valor : 0.0;
    }

    private void agregarCeldaEncabezado(PdfPTable tabla, String texto, Font font) {
        PdfPCell celda = crearCelda(texto, font);
        celda.setHorizontalAlignment(Element.ALIGN_LEFT);
        celda.setBackgroundColor(COLOR_ENCABEZADO);
        celda.getPhrase().getFont().setColor(BaseColor.WHITE);
        tabla.addCell(celda);
    }

    private void agregarCelda(PdfPTable tabla, String etiqueta, String valor, Font font) {
        tabla.addCell(crearCelda(etiqueta, font));
        tabla.addCell(crearCelda(valor, font));
    }

    private void agregarSeccionTitulo(Document documento, String tituloSeccion, Font font) throws DocumentException {
        PdfPTable banner = new PdfPTable(1);
        banner.setWidthPercentage(100);
        PdfPCell celda = new PdfPCell(new Phrase(tituloSeccion, font));
        celda.setBackgroundColor(COLOR_ENCABEZADO);
        celda.setPadding(9f);
        celda.setBorder(0);
        banner.addCell(celda);
        documento.add(banner);
    }

    private String formatearMoneda(BigDecimal valor) {
        return "$" + valor.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private String formatearMoneda(double valor) {
        return formatearMoneda(BigDecimal.valueOf(valor));
    }

    private record RangoFechas(LocalDateTime inicio, LocalDateTime fin) {
    }
}