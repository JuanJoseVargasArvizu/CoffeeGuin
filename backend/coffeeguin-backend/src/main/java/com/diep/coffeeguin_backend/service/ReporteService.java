package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.model.Reporte;
import com.diep.coffeeguin_backend.model.Venta;
import com.diep.coffeeguin_backend.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private VentaService ventaService;

    public List<Reporte> obtenerTodosLosReportes() {
        return reporteRepository.findAll();
    }

    public Reporte generarReportePorPeriodo(String tipo) {
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

        Reporte nuevoReporte = new Reporte();
        nuevoReporte.setTitulo("Reporte " + tipo.toUpperCase() + " de Ventas");
        nuevoReporte.setFechaGeneracion(ahora);
        nuevoReporte.setTotalCalculado(totalDinero);
        nuevoReporte.setVentas(ventasDelPeriodo);
        nuevoReporte.setObservaciones("Reporte generado automáticamente. Total de tickets: " + ventasDelPeriodo.size());
        
        return reporteRepository.save(nuevoReporte);
    }
}