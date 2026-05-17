package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.dto.AnaliticaGraficaDTO;
import com.diep.coffeeguin_backend.model.Reporte;
import com.diep.coffeeguin_backend.model.Venta;
import com.diep.coffeeguin_backend.model.VentasPorCategoriaResumen;
import com.diep.coffeeguin_backend.model.VentasPorProductoResumen;
import com.diep.coffeeguin_backend.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping
    public List<Reporte> obtenerTodosLosReportes() {
        return reporteService.obtenerTodosLosReportes();
    }

    @PostMapping("/generar")
    public Reporte generarReportePorPeriodo(@RequestParam String tipo) {
        return reporteService.generarReportePorPeriodo(tipo);
    }

    @GetMapping("/productos")
    public List<VentasPorProductoResumen> ventasPorProducto(@RequestParam String tipo) {
        return reporteService.resumenVentasPorProducto(tipo);
    }

    @GetMapping("/categorias")
    public List<VentasPorCategoriaResumen> ventasPorCategoria(@RequestParam String tipo) {
        return reporteService.resumenVentasPorCategoria(tipo);
    }

    @GetMapping("/ventas")
    public List<Venta> consultarVentas(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return reporteService.consultarVentasPorPeriodo(inicio, fin);
    }

    @GetMapping(value = "/financiero/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> obtenerReporteFinancieroPdf(@RequestParam String tipo) {
        byte[] pdf = reporteService.generarReporteFinancieroPdf(tipo);
        String nombreArchivo = "reporte-financiero-" + tipo.toLowerCase() + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + nombreArchivo)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/analitica")
    public AnaliticaGraficaDTO obtenerAnalitica(
            @RequestParam String criterio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return reporteService.obtenerAnaliticaGrafica(criterio, fechaInicio, fechaFin);
    }
}