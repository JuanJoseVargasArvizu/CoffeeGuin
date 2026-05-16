package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.model.Reporte;
import com.diep.coffeeguin_backend.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}