package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.dao.ReporteDAO;
import com.diep.coffeeguin_backend.model.Reporte;
import com.diep.coffeeguin_backend.model.Venta;
import com.diep.coffeeguin_backend.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteDAO reporteDAO;

    @Autowired
    private VentaService ventaService;

    // reportes hechos anteriormente
    @GetMapping
    public List<Reporte> obtenerTodosLosReportes() {
        return reporteDAO.findAll();
    }
    // genera informes diarios, semanales o mensuales 
    @PostMapping("/generar")
    public Reporte generarReportePorPeriodo(@RequestParam String tipo) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaInicio;

        switch (tipo.toLowerCase()) {
            case "diario":
                fechaInicio = ahora.withHour(0).withMinute(0).withSecond(0); 
                break;
            case "semanal":
                fechaInicio = ahora.minusDays(7); 
                break;
            case "mensual":
                fechaInicio = ahora.minusMonths(1); 
                break;
            default:
                throw new IllegalArgumentException("Tipo inválido. Usa: diario, semanal o mensual");
        }

        List<Venta> todasLasVentas = ventaService.listarTodas();
        List<Venta> ventasDelPeriodo = new ArrayList<>();
        double totalDinero = 0.0;

        for (Venta venta : todasLasVentas) {
            if (venta.getFecha() != null && venta.getFecha().isAfter(fechaInicio)) {
                ventasDelPeriodo.add(venta);
                totalDinero += venta.getTotalFinal();
            }
        }

        // Construir reporte oficial
        Reporte nuevoReporte = new Reporte();
        nuevoReporte.setTitulo("Reporte " + tipo.toUpperCase() + " de Ventas");
        nuevoReporte.setFechaGeneracion(ahora);
        nuevoReporte.setTotalCalculado(totalDinero);
        nuevoReporte.setVentas(ventasDelPeriodo); 
        nuevoReporte.setObservaciones("Reporte generado automáticamente. Total de tickets: " + ventasDelPeriodo.size());
        return reporteDAO.save(nuevoReporte);
    }
}
