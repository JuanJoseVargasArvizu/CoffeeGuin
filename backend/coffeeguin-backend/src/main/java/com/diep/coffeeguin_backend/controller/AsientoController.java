package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.dao.AsientoDAO;
import com.diep.coffeeguin_backend.model.Asiento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asientos")
public class AsientoController {

    @Autowired
    private AsientoDAO asientoDAO;

    // obtener los asientos
    @GetMapping
    public List<Asiento> obtenerTodosLosAsientos() {
        return asientoDAO.findAll();
    }

    // crear un asiento nuevo 
    @PostMapping
    public Asiento crearAsiento(@RequestBody Asiento nuevoAsiento) {
        return asientoDAO.save(nuevoAsiento);
    }

    // buscar un asiento en especififo
    @GetMapping("/{id}")
    public Asiento obtenerAsientoPorId(@PathVariable Integer id) {
        return asientoDAO.findById(id);
    }

    @PutMapping("/{id}/estado")
    public Asiento actualizarEstadoAsiento(@PathVariable Integer id, @RequestParam Boolean estaOcupado) {
        Asiento asientoExistente = asientoDAO.findById(id);
        
        if (asientoExistente != null) {
            asientoExistente.setOcupado(estaOcupado); 
            return asientoDAO.save(asientoExistente);
        }
        return null; 
    }
}
