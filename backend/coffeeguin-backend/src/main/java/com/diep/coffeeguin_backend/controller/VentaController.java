package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.model.Venta;
import com.diep.coffeeguin_backend.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping
    public List<Venta> obtenerHistorial() {
        return ventaService.listarTodas();
    }

    @PostMapping
    public Venta registrarVenta(@RequestBody Venta nuevaVenta) {
        return ventaService.registrarVenta(nuevaVenta);
    }
}