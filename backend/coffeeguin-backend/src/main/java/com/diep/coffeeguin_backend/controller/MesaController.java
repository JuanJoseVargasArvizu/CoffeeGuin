package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.model.Mesa;
import com.diep.coffeeguin_backend.service.MesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    @Autowired
    private MesaService mesaService;

    @GetMapping
    public List<Mesa> obtenerTodasLasMesas() {
        return mesaService.obtenerTodasLasMesas();
    }

    @PostMapping
    public Mesa crearMesa(@RequestBody Mesa nuevaMesa) {
        return mesaService.crearMesa(nuevaMesa);
    }

    @PutMapping("/{id}/estado")
    public Mesa actualizarEstadoMesa(@PathVariable Integer id, @RequestParam String nuevoEstado) {
        return mesaService.actualizarEstadoMesa(id, nuevoEstado);
    }
}