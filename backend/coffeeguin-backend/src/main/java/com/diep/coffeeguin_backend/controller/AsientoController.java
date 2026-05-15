package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.model.Asiento;
import com.diep.coffeeguin_backend.repository.AsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asientos")
public class AsientoController {

    @Autowired
    private AsientoRepository asientoRepository;

    // obtener los asientos
    @GetMapping
    public List<Asiento> obtenerTodosLosAsientos() {
        return asientoRepository.findAll();
    }

    // crear un asiento nuevo 
    @PostMapping
    public Asiento crearAsiento(@RequestBody Asiento nuevoAsiento) {
        return asientoRepository.save(nuevoAsiento);
    }

    // buscar un asiento en especifico
    @GetMapping("/{id}")
    public Asiento obtenerAsientoPorId(@PathVariable Integer id) {
        return asientoRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}/estado")
    public Asiento actualizarEstadoAsiento(@PathVariable Integer id, @RequestParam Boolean estaOcupado) {
        Asiento asientoExistente = asientoRepository.findById(id).orElse(null);
        
        if (asientoExistente != null) {
            asientoExistente.setOcupado(estaOcupado); 
            return asientoRepository.save(asientoExistente);
        }
        return null; 
    }
} 