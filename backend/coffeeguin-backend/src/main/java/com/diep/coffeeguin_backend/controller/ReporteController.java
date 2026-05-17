package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.model.Reporte;
import com.diep.coffeeguin_backend.model.Venta;
import com.diep.coffeeguin_backend.model.VentasPorCategoriaResumen;
import com.diep.coffeeguin_backend.model.VentasPorProductoResumen;
import com.diep.coffeeguin_backend.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

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
}