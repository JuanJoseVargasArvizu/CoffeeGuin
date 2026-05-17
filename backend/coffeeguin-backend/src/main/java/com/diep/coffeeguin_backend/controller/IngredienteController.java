package com.diep.coffeeguin_backend.controller;

import com.diep.coffeeguin_backend.model.Ingrediente;
import com.diep.coffeeguin_backend.service.IngredienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ingredientes")
public class IngredienteController {

    @Autowired
    private IngredienteService ingredienteService;

    // POST 
    @PostMapping
    public Ingrediente crearIngrediente(@RequestBody Map<String, Object> payload) {
        Ingrediente nuevoIngrediente = new Ingrediente();
        
        nuevoIngrediente.setNombre((String) payload.get("nombre"));
        nuevoIngrediente.setStockActual((Integer) payload.get("stockActual"));
        nuevoIngrediente.setUmbralAlerta((Integer) payload.get("umbralAlerta"));
        
        return ingredienteService.registrar(nuevoIngrediente);
    }
    

    // GET 
    @GetMapping("/alertas")
    public List<Map<String, Object>> obtenerAlertasInventario() {
        List<Ingrediente> ingredientesEnAlerta = ingredienteService.obtenerAlertasDeStock();
        List<Map<String, Object>> respuesta = new ArrayList<>();
        
        for (Ingrediente ing : ingredientesEnAlerta) {
            Map<String, Object> alerta = new LinkedHashMap<>();
            alerta.put("id", ing.getId());
            alerta.put("nombre", ing.getNombre()); 
            alerta.put("stockActual", ing.getStockActual());
            alerta.put("umbralMinimo", ing.getUmbralAlerta());
            alerta.put("mensaje", "¡Alerta! Quedan " + ing.getStockActual() + " unidades de " + ing.getNombre());
            
            respuesta.add(alerta);
        }
        
        return respuesta;
    }
}