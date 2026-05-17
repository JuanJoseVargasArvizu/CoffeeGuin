package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.dto.VentaDetalleDTO;
import com.diep.coffeeguin_backend.dto.VentaResumenDTO;
import com.diep.coffeeguin_backend.model.Venta;
import com.diep.coffeeguin_backend.model.VentaMesaRequest;
import com.diep.coffeeguin_backend.model.VentaMesaResponse;
import com.diep.coffeeguin_backend.service.VentaMesaService;
import com.diep.coffeeguin_backend.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private VentaMesaService ventaMesaService;

    @GetMapping
    public List<VentaResumenDTO> obtenerHistorial(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ventaService.listarResumenPorPeriodo(fechaInicio, fechaFin);
    }

    @GetMapping("/{id}/detalle")
    public List<VentaDetalleDTO> obtenerDetalleVenta(@PathVariable Integer id) {
        return ventaService.listarDetalleVenta(id);
    }

    @PostMapping
    public Venta registrarVenta(@RequestBody Venta nuevaVenta) {
        return ventaService.registrarVenta(nuevaVenta);
    }

    @PostMapping("/mesa")
    public VentaMesaResponse registrarVentaMesa(@RequestBody VentaMesaRequest request) {
        return ventaMesaService.registrarVentaMesa(request);
    }
}