package com.diep.coffeeguin_backend.service;

import com.diep.coffeeguin_backend.model.Reporte;
import com.diep.coffeeguin_backend.model.Venta;
import com.diep.coffeeguin_backend.model.VentasPorCategoriaResumen;
import com.diep.coffeeguin_backend.model.VentasPorProductoResumen;
import com.diep.coffeeguin_backend.repository.VentaDetalleRepository;
import com.diep.coffeeguin_backend.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.time.temporal.ChronoUnit;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private VentaDetalleRepository ventaDetalleRepository;

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
}