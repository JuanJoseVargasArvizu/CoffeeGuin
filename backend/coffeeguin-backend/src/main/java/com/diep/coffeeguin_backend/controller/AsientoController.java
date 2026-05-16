package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.model.Asiento;
import com.diep.coffeeguin_backend.service.AsientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asientos")
public class AsientoController {

    @Autowired
    private AsientoService asientoService;

    @GetMapping
    public List<Asiento> obtenerTodosLosAsientos() {
        return asientoService.obtenerTodosLosAsientos();
    }

    @PostMapping
    public Asiento crearAsiento(@RequestBody Asiento nuevoAsiento) {
        return asientoService.crearAsiento(nuevoAsiento);
    }

    @GetMapping("/{id}")
    public Asiento obtenerAsientoPorId(@PathVariable Integer id) {
        return asientoService.obtenerAsientoPorId(id);
    }

    @PutMapping("/{id}/estado")
    public Asiento actualizarEstadoAsiento(@PathVariable Integer id, @RequestParam Boolean estaOcupado) {
        return asientoService.actualizarEstadoAsiento(id, estaOcupado);
    }
}